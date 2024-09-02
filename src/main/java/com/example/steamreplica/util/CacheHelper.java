package com.example.steamreplica.util;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.service.exception.CacheException;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeBase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class CacheHelper {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;
    private final ServiceHelper serviceHelper;

    private static String makeCacheKey(String cacheKeyPrefix) {
        return cacheKeyPrefix + "::";
    }

    private static String makePaginationCacheKey(String cacheKeyPrefix, int page) {
        return cacheKeyPrefix + "::Pagination_Cache::" + page;
    }

    private static String makeListCacheKey(String cacheKeyPrefix) {
        return cacheKeyPrefix + "::List_Cache";
    }

    public <T extends ApplicationEvent> void publishCacheEvent(T eventType) {
        applicationEventPublisher.publishEvent(eventType);
    }

    /**
     * Clears the cache entries based on the provided cache key prefix using the helper interface to determine
     * if each entry should be removed. If an entry is related to the specified ID, it is deleted from the cache.
     * Usually used for if A is updated (the specified ID) and this entity has a relationship with B (the entity), then B should be updated or otherwise removed.
     *
     * @param cacheKeyPrefix    The prefix for the cache key to identify the cache entries.
     * @param IMyTemplateHelper The interface used to check the relationship between cache entries and the specified ID.
     */
    public <T extends BaseResponse> void refreshAllCachesSelectiveOnUpdatedEventReceived(String cacheKeyPrefix, List<String> paginationCacheKeyPrefix, List<String> listCacheKeyPrefix, int pageRange, long id, IMyTemplateHelper<T> IMyTemplateHelper) {
        refreshCacheOnUpdatedEventReceived(cacheKeyPrefix, id, IMyTemplateHelper);
        listCacheKeyPrefix.forEach(this::deleteListCaches);
        refreshPaginationCacheOnUpdatedEventReceived(paginationCacheKeyPrefix, pageRange, id, IMyTemplateHelper);
    }

    public <T extends BaseResponse> void refreshPaginationCacheOnUpdatedEventReceived(List<String> paginationCacheKeyPrefix, int pageRange, long id, IMyTemplateHelper<T> IMyTemplateHelper) {
        paginationCacheKeyPrefix.stream().parallel().forEach(prefix -> IntStream.rangeClosed(1, pageRange).parallel().forEach(i -> {
            String paginationCacheKey = makePaginationCacheKey(prefix, i);
            String objectJson = (String) redisTemplate.opsForValue().get(paginationCacheKey);
            CollectionModel<EntityModel<T>> deserializedObject = null;
            try {
                deserializedObject = objectMapper.readValue(objectJson, objectMapper.getTypeFactory().constructParametricType(CollectionModel.class, objectMapper.getTypeFactory().constructParametricType(EntityModel.class, BaseResponse.class)));
            } catch (Exception e) {
                handleException(e);
                return;
            }
            deserializedObject.getContent().forEach(tEntityModel -> {
                if (IMyTemplateHelper.isRelated(tEntityModel.getContent(), id)) {
                    redisTemplate.delete(paginationCacheKey);
                }
            });
        }));
    }

    public <T extends BaseResponse> void refreshCacheOnUpdatedEventReceived(String cacheKeyPrefix, long id, IMyTemplateHelper<T> IMyTemplateHelper) {
        String cacheKey = makeCacheKey(cacheKeyPrefix);

        Map<Object, Object> map = redisTemplate.opsForHash().entries(cacheKey);
        if (map.isEmpty()) return;
        map.values().stream().parallel().map(o -> (T) o).forEach(entity -> {
            if (IMyTemplateHelper.isRelated(entity, id))
                redisTemplate.opsForHash().delete(cacheKey, String.valueOf(entity.getId()));
        });
    }

    /**
     * Retrieves an object from the Redis cache based on the provided cache key prefix and key.
     * If the object exists in the cache, it is returned; otherwise, a new object is created using the specified function (trFunction), cached, and then returned.
     *
     * @param <R>            The type of the object to retrieve from the cache.
     * @param <T>            The type of the repository used to create the object if not found in the cache.
     * @param cacheKeyPrefix The prefix for the cache key.
     * @param key            The key to identify the object in the cache.
     * @param repo           The repository used to create the object if not found in the cache.
     * @param trFunction     The function to create the object if not found in the cache.
     * @return The retrieved object from the cache or a newly created object.
     * @throws CacheException if an error occurs while retrieving the object from the cache.
     */
    public <R extends BaseResponse, T> EntityModel<R> getCache(String cacheKeyPrefix, Class<R> Clazz, Object key, T repo, Function<T, EntityModel<R>> trFunction, long timeToLive) {
        try {
            String cacheKey = makeCacheKey(cacheKeyPrefix);

            String objectJson = (String) redisTemplate.opsForHash().get(cacheKey, key);
            EntityModel<R> deserializedObject = objectMapper.readValue(objectJson, objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Clazz));
            if (deserializedObject != null) return deserializedObject;

            EntityModel<R> objectToCache = trFunction.apply(repo);
            redisTemplate.opsForHash().put(cacheKey, key, objectMapper.writeValueAsString(objectToCache));
            redisTemplate.expire(cacheKey, timeToLive, TimeUnit.MINUTES);

            return objectToCache;
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    // Get target list in cache from Redis with cacheKeyPrefix and key and return the list if it exists, otherwise create the object with trFunction and cache it.
    public <R extends BaseResponse, T> CollectionModel<EntityModel<R>> getListCache(String cacheKeyPrefix, Class<R> responseType, T repo, Function<T, CollectionModel<EntityModel<R>>> trFunction) {
        try {
            String cacheKey = makeListCacheKey(cacheKeyPrefix);

            String objectJson = (String) redisTemplate.opsForValue().get(cacheKey);
            CollectionModel<EntityModel<R>> cachedResponses = objectMapper.readValue(objectJson, objectMapper.getTypeFactory().constructParametricType(CollectionModel.class, objectMapper.getTypeFactory().constructParametricType(EntityModel.class, responseType)));

            if (!cachedResponses.getContent().isEmpty()) return cachedResponses;

            CollectionModel<EntityModel<R>> entityModels = trFunction.apply(repo);
            redisTemplate.opsForValue().set(cacheKey, entityModels);

            return entityModels;
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    // Get pagination list in cache from Redis with cacheKeyPrefix and key and return the list if it exists, otherwise create the object with trFunction and cache it.
    public <R extends BaseResponse, T> CollectionModel<EntityModel<R>> getPaginationCache(String cacheKeyPrefix, Class<R> Clazz, int page, T repo, Function<T, CollectionModel<EntityModel<R>>> trFunction) {
        try {
            String cacheKey = makePaginationCacheKey(cacheKeyPrefix, page);

            String objectJson = (String) redisTemplate.opsForValue().get(cacheKey);
            if (objectJson != null) {
                CollectionModel<EntityModel<R>> deserializedObject = objectMapper.readValue(objectJson, objectMapper.getTypeFactory().constructParametricType(CollectionModel.class, objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Clazz)));
                if (!deserializedObject.getContent().isEmpty()) return deserializedObject;
            }

            CollectionModel<EntityModel<R>> entityModels = trFunction.apply(repo);
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(entityModels));
            redisTemplate.expire(cacheKey, 15, TimeUnit.MINUTES);

            return entityModels;
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public <T extends BaseResponse> void updateCache(EntityModel<T> objectToUpdate, String cacheKeyPrefix) {
        if (objectToUpdate.getContent() == null) return;
        try {
            redisTemplate.opsForValue().set(makeCacheKey(cacheKeyPrefix), objectMapper.writeValueAsString(objectToUpdate));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public <T extends BaseResponse> void updateCache(T objectToUpdate, String cacheKeyPrefix, String ListCacheKeyPrefix, Object key, IMyTemplateHelper<T> IMyTemplateHelper) {
        // Update the object in the key-value store
        redisTemplate.opsForHash().put(makeCacheKey(cacheKeyPrefix), String.valueOf(key), objectToUpdate);

        // Update the object in the list cache if it exists
        String listCacheKeyName = makeListCacheKey(ListCacheKeyPrefix);
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(listCacheKeyName, ScanOptions.NONE);
        cursor.forEachRemaining(pair -> {
            T value = null;
            try {
                value = objectMapper.readValue((String) pair.getValue(), new TypeReference<T>() {
                });
                if (IMyTemplateHelper.isRelated(value, key)) {
                    redisTemplate.opsForHash().put(listCacheKeyName, String.valueOf(objectToUpdate.getId()), objectMapper.writeValueAsString(objectToUpdate));
                }
            } catch (Exception e) {
                handleException(e);
            }
        });

        cursor.close();
    }

    public <T extends BaseResponse> void updatePaginationCache(EntityModel<T> objectToUpdate, int pageRange, String... paginationCacheKeyPrefix) {
        if (objectToUpdate.getContent() == null) {
            throw new IllegalArgumentException("objectToUpdate cannot be null");
        }
        Arrays.stream(paginationCacheKeyPrefix).parallel().forEach(prefix -> IntStream.rangeClosed(1, pageRange).parallel().forEach(i -> {
            String paginationCacheKey = makePaginationCacheKey(prefix, i);
            String objectJson = (String) redisTemplate.opsForValue().get(paginationCacheKey);
            CollectionModel<EntityModel<T>> deserializedObject = null;
            try {
                deserializedObject = objectMapper.readValue(objectJson, objectMapper.getTypeFactory().constructParametricType(CollectionModel.class, objectMapper.getTypeFactory().constructParametricType(EntityModel.class, BaseResponse.class)));
            } catch (Exception e) {
                handleException(e);
                return;
            }
            deserializedObject.getContent().forEach(tEntityModel -> {
                if (Objects.equals(Objects.requireNonNull(tEntityModel.getContent()).getId(), objectToUpdate.getContent().getId()))
                    redisTemplate.delete(paginationCacheKey);
            });
        }));
    }

    public void deleteCaches(String cachePrefix, Object id, String cacheListPrefix) {
        String cacheKey = makeCacheKey(cachePrefix);
        String cacheList = makeListCacheKey(cacheListPrefix);
        redisTemplate.opsForHash().delete(cacheKey, String.valueOf(id));
        redisTemplate.delete(cacheList);
    }

    public void deleteListCaches(String cacheListPrefix) {
        String cacheList = makeListCacheKey(cacheListPrefix);
        redisTemplate.delete(cacheList);
    }

    public void deleteCache(String cachePrefix, Object key) {
        String cacheKey = makeCacheKey(cachePrefix);
        redisTemplate.opsForHash().delete(cacheKey, String.valueOf(key));
    }

    public <T extends BaseResponse> void deletePaginationCache(int pageRange, String... paginationCachePrefix) {
        Arrays.stream(paginationCachePrefix).parallel().forEach(prefix -> IntStream.rangeClosed(1, pageRange).forEach(i -> {
            String cacheKeyName = makePaginationCacheKey(prefix, i);
            redisTemplate.delete(cacheKeyName);
        }));
    }

    private void handleException(Exception e) throws ResourceNotFoundException, CacheException {
        if (e instanceof ResourceNotFoundException) {
            throw (ResourceNotFoundException) e;
        } else if (e instanceof CacheException) {
            throw (CacheException) e;
        } else {
            throw new CacheException("Error while getting data in cache", e);
        }
    }
}
