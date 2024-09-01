package com.example.steamreplica.util;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.service.exception.CacheException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class CacheHelper {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

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
    public <T extends BaseCacheableModel> void refreshAllCachesSelectiveOnUpdatedEventReceived(String cacheKeyPrefix, List<String> paginationCacheKeyPrefix, List<String> listCacheKeyPrefix, int pageRange, long id, IMyTemplateHelper<T> IMyTemplateHelper) {
        refreshCacheOnUpdatedEventReceived(cacheKeyPrefix, id, IMyTemplateHelper);
        refreshListCachesOnUpdatedEventReceived(listCacheKeyPrefix);
        refreshPaginationCacheOnUpdatedEventReceived(paginationCacheKeyPrefix, pageRange, id, IMyTemplateHelper);
    }

    public <T extends BaseCacheableModel> void refreshPaginationCacheOnUpdatedEventReceived(List<String> paginationCacheKeyPrefix, int pageRange, long id, IMyTemplateHelper<T> IMyTemplateHelper) {
        paginationCacheKeyPrefix.stream()
                .parallel()
                .forEach(prefix -> {
                    for (int i = 0; i < pageRange; i++) {
                        String paginationKey = makePaginationCacheKey(prefix, i);
                        redisTemplate.opsForHash().entries(paginationKey).values().stream().map(o -> (T) o).forEach(t -> {
                            if (IMyTemplateHelper.isRelated(t, id)) {
                                redisTemplate.delete(paginationKey);
                            }
                        });
                    }
                });
    }

    public void refreshListCachesOnUpdatedEventReceived(List<String> listCacheKeyPrefix) {
        listCacheKeyPrefix.forEach(prefix -> CompletableFuture.runAsync(() -> redisTemplate.delete(makeListCacheKey(prefix))));
    }

    public <T extends BaseCacheableModel> void refreshCacheOnUpdatedEventReceived(String cacheKeyPrefix, long id, IMyTemplateHelper<T> IMyTemplateHelper) {
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
    public <R extends BaseResponse, T> R getCache(String cacheKeyPrefix, Object key, T repo, Function<T, R> trFunction) {
        try {
            String cacheKey = makeCacheKey(cacheKeyPrefix);

            Object cachedObject = redisTemplate.opsForHash().get(cacheKey, String.valueOf(key));
            if (cachedObject != null) return objectMapper.readValue((String) cachedObject, new TypeReference<R>() {
            });

            R objectToCache = trFunction.apply(repo);
            redisTemplate.opsForHash().put(cacheKey, String.valueOf(key), objectToCache);

            return objectToCache;
        } catch (Exception e) {
            throw new CacheException("Error while getting cache.", e);
        }
    }

    // Get target list in cache from Redis with cacheKeyPrefix and key and return the list if it exists, otherwise create the object with trFunction and cache it.
    public <R extends BaseResponse, T> List<R> getListCache(String cacheKeyPrefix, T repo, Function<T, List<R>> trFunction) {
        try {
            String cacheKey = makeListCacheKey(cacheKeyPrefix);

            Map<Object, Object> object = redisTemplate.opsForHash().entries(cacheKey);
            List<R> exitingList = object.values().stream().map(value -> {
                try {
                    return objectMapper.readValue((String) value, new TypeReference<R>() {
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).toList();
            if (!exitingList.isEmpty()) return exitingList;

            List<R> list = trFunction.apply(repo);
            for (R item : list) {
                redisTemplate.opsForHash().put(cacheKey, String.valueOf(item.getId()), list);
            }

            return list;
        } catch (Exception e) {
            throw new CacheException("Error while getting target list of data in cache.", e);
        }
    }

    // Get pagination list in cache from Redis with cacheKeyPrefix and key and return the list if it exists, otherwise create the object with trFunction and cache it.
    public <R extends BaseResponse, T> List<R> getPaginationCache(String cacheKeyPrefix, Class<R> Clazz, int page, T repo, Function<T, List<R>> trFunction) {
        try {
            String cacheKey = makePaginationCacheKey(cacheKeyPrefix, page);

            Map<Object, Object> object = redisTemplate.opsForHash().entries(cacheKey);
            List<R> exitingList = object.values().stream().map(value -> {
                try {
                    return objectMapper.readValue((String) value, Clazz);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).toList();
            if (!exitingList.isEmpty()) return exitingList;

            List<R> list = trFunction.apply(repo);
            for (R item : list) {
                redisTemplate.opsForHash().put(cacheKey, String.valueOf(item.getId()), objectMapper.writeValueAsString(EntityModel.of(item)));
            }

            return list;
        } catch (Exception e) {
            throw new CacheException("Error while getting pagination list of data in cache.", e);
        }
    }

    /**
     * Updates the cache for a specific object in Redis. If the object exists in the list cache, it updates the object in the hash map.
     *
     * @param objectToUpdate     The object to update in the cache.
     * @param cacheKeyPrefix     The prefix for the cache key.
     * @param ListCacheKeyPrefix The prefix for the list cache key.
     */
    public <T extends BaseCacheableModel> void updateCache(T objectToUpdate, String cacheKeyPrefix, String ListCacheKeyPrefix) {
        // Update the object in the key-value store
        redisTemplate.opsForHash().put(makeCacheKey(cacheKeyPrefix), objectToUpdate.getId(), objectToUpdate);

        // Update the object in the list cache if it exists
        String listCacheKeyName = makeListCacheKey(ListCacheKeyPrefix);
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(listCacheKeyName, ScanOptions.NONE);
        cursor.forEachRemaining(pair -> {
            T value = null;
            try {
                value = objectMapper.readValue((String) pair.getValue(), new TypeReference<T>() {
                });
                if (Objects.equals(value.getId(), objectToUpdate.getId())) {
                    redisTemplate.opsForHash().put(listCacheKeyName, String.valueOf(objectToUpdate.getId()), objectMapper.writeValueAsString(objectToUpdate));
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        cursor.close();
    }

    public <T extends BaseCacheableModel> void updateCache(T objectToUpdate, String cacheKeyPrefix, String ListCacheKeyPrefix, Object key, IMyTemplateHelper<T> IMyTemplateHelper) {
        // Update the object in the key-value store
        redisTemplate.opsForHash().put(makeCacheKey(cacheKeyPrefix), key, objectToUpdate);

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
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        cursor.close();
    }

    public <T extends BaseCacheableModel, R> void updateCache(String cacheKeyPrefix, T objectToUpdate, Function<T, R> trFunction) {
        // Update the object in the key-value store
        try {
            redisTemplate.opsForHash().put(makeCacheKey(cacheKeyPrefix), trFunction.apply(objectToUpdate), objectMapper.writeValueAsString(objectToUpdate));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the pagination cache for the specified object with the given page range and cache key prefixes.
     * Retrieves the cache entries from Redis based on the pagination cache key prefixes and iterates over each page within the range.
     * Compares the object IDs in the cache with the ID of the object to update, and updates the cache entry if a match is found.
     *
     * @param objectToUpdate           The object to be updated in the cache.
     * @param pageRange                The range of pages to update in the cache.
     * @param paginationCacheKeyPrefix The prefixes used to construct the pagination cache keys.
     */
    public <T extends BaseCacheableModel> void updatePaginationCache(T objectToUpdate, int pageRange, String... paginationCacheKeyPrefix) {
        if (objectToUpdate == null) {
            throw new IllegalArgumentException("objectToUpdate cannot be null");
        }
        Arrays.stream(paginationCacheKeyPrefix).parallel().forEach(prefix -> IntStream.rangeClosed(1, pageRange).parallel().forEach(i -> {
            String paginationCacheKey = makePaginationCacheKey(prefix, i);
            Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(paginationCacheKey, ScanOptions.NONE);
            cursor.forEachRemaining(entry -> {
                try {
                    T item = null;
                    item = objectMapper.readValue((String) entry.getValue(), new TypeReference<T>() {
                    });
                    if (Objects.equals(item.getId(), objectToUpdate.getId())) {
                        redisTemplate.opsForHash().put(paginationCacheKey, String.valueOf(item.getId()), objectMapper.writeValueAsString(item));
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
            cursor.close();
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

    public <T extends BaseCacheableModel, R> void deleteCache(String cachePrefix, Object key) {
        String cacheKey = makeCacheKey(cachePrefix);
        redisTemplate.opsForHash().delete(cacheKey, String.valueOf(key));
    }

    /**
     * Deletes the cache entries associated with the specified object ID within the given page range and pagination cache key prefixes.
     *
     * @param id                    The ID of the object to match for deletion in the cache.
     * @param pageRange             The range of pages to search for cache entries.
     * @param paginationCachePrefix The prefixes used to construct the pagination cache keys.
     */
    public <T extends BaseCacheableModel> void deletePaginationCache(long id, int pageRange, String... paginationCachePrefix) {
        Arrays.stream(paginationCachePrefix).parallel().forEach(prefix -> IntStream.rangeClosed(1, pageRange).forEach(i -> {
            String cacheKeyName = makePaginationCacheKey(prefix, i);
            Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(cacheKeyName, ScanOptions.NONE);
            cursor.forEachRemaining(pair -> {
                try {
                    T value = null;
                    value = objectMapper.readValue((String) pair.getValue(), new TypeReference<T>() {
                    });
                    if (Objects.equals(value.getId(), id)) {
                        redisTemplate.delete(cacheKeyName);
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            });
            cursor.close();
        }));
    }
}
