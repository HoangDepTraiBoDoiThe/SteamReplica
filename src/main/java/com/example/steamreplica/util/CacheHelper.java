package com.example.steamreplica.util;

import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.service.exception.CacheException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class CacheHelper {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

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
     * @param cacheKeyPrefix  The prefix for the cache key to identify the cache entries.
     * @param helperInterface The interface used to check the relationship between cache entries and the specified ID.
     */
    public <T extends BaseCacheableModel> void refreshAllCachesSelectiveOnUpdatedEventReceived(String cacheKeyPrefix, List<String> paginationCacheKeyPrefix, List<String> listCacheKeyPrefix, int pageRange, long id, HelperInterface<T> helperInterface) {
        refreshCacheOnUpdatedEventReceived(cacheKeyPrefix, id, helperInterface);
        refreshListCachesOnUpdatedEventReceived(listCacheKeyPrefix);
        refreshPaginationCacheOnUpdatedEventReceived(paginationCacheKeyPrefix, pageRange, id, helperInterface);
    }

    public <T extends BaseCacheableModel> void refreshPaginationCacheOnUpdatedEventReceived(List<String> paginationCacheKeyPrefix, int pageRange, long id, HelperInterface<T> helperInterface) {
        paginationCacheKeyPrefix.stream()
                .parallel()
                .forEach(prefix -> {
                    for (int i = 1; i <= pageRange; i++) {
                        String paginationKey = makePaginationCacheKey(prefix, i);
                        redisTemplate.opsForHash().entries(paginationKey).values().stream().map(o -> (T) o).forEach(t -> {
                            if (helperInterface.isRelated(t, id)) {
                                redisTemplate.delete(paginationKey);
                            }
                        });
                    }
                });
    }

    public void refreshListCachesOnUpdatedEventReceived(List<String> listCacheKeyPrefix) {
        listCacheKeyPrefix.forEach(prefix -> CompletableFuture.runAsync(() -> redisTemplate.delete(makeListCacheKey(prefix))));
    }

    public <T extends BaseCacheableModel> void refreshCacheOnUpdatedEventReceived(String cacheKeyPrefix, long id, HelperInterface<T> helperInterface) {
        String cacheKey = makeCacheKey(cacheKeyPrefix);

        Map<Object, Object> map = redisTemplate.opsForHash().entries(cacheKey);
        if (map.isEmpty()) return;
        map.values().stream().parallel().map(o -> (T) o).forEach(entity -> {
            if (helperInterface.isRelated(entity, id)) redisTemplate.opsForHash().delete(cacheKey, entity.getId());
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
    public <R extends BaseCacheableModel, T> R getCache(String cacheKeyPrefix, Object key, T repo, Function<T, R> trFunction) {
        try {
            String cacheKey = makeCacheKey(cacheKeyPrefix);

            R cachedObject = (R) redisTemplate.opsForHash().get(cacheKey, key);
            if (cachedObject != null) return cachedObject;

            R objectToCache = trFunction.apply(repo);
            redisTemplate.opsForHash().put(cacheKey, key, objectToCache);

            return objectToCache;
        } catch (Exception e) {
            throw new CacheException("Error while getting cache.", e);
        }
    }

    // Get target list in cache from Redis with cacheKeyPrefix and key and return the list if it exists, otherwise create the object with trFunction and cache it.
    public <R extends BaseCacheableModel, T> List<R> getListCache(String cacheKeyPrefix, T repo, Function<T, List<R>> trFunction) {
        try {
            String cacheKey = makeListCacheKey(cacheKeyPrefix);

            Map<Object, Object> object = redisTemplate.opsForHash().entries(cacheKey);
            List<R> exitingList = object.values().stream().map(value -> (R) value).toList();
            if (!exitingList.isEmpty()) return exitingList;

            List<R> list = trFunction.apply(repo);
            for (R item : list) {
                redisTemplate.opsForHash().put(cacheKey, item.getId(), list);
            }

            return list;
        } catch (Exception e) {
            throw new CacheException("Error while getting target list of data in cache.", e);
        }
    }

    // Get pagination list in cache from Redis with cacheKeyPrefix and key and return the list if it exists, otherwise create the object with trFunction and cache it.
    public <R extends BaseCacheableModel, T> List<R> getPaginationCache(String cacheKeyPrefix, int page, T repo, Function<T, List<R>> trFunction) {
        try {
            String cacheKey = makePaginationCacheKey(cacheKeyPrefix, page);

            Map<Object, Object> object = redisTemplate.opsForHash().entries(cacheKey);
            List<R> exitingList = object.values().stream().map(value -> (R) value).toList();
            if (!exitingList.isEmpty()) return exitingList;

            List<R> list = trFunction.apply(repo);
            for (R item : list) {
                redisTemplate.opsForHash().put(cacheKey, item.getId(), list);
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
            T value = (T) pair.getValue();
            if (Objects.equals(value.getId(), objectToUpdate.getId()))
                redisTemplate.opsForHash().put(listCacheKeyName, objectToUpdate.getId(), objectToUpdate);
        });
        cursor.close();
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
                Object value = entry.getValue();
                T item = (T) value;
                if (Objects.equals(item.getId(), objectToUpdate.getId())) {
                    redisTemplate.opsForHash().put(paginationCacheKey, item.getId(), item);
                }
            });
            cursor.close();
        }));
    }

    public void deleteCaches(String cachePrefix, Object id, String cacheListPrefix) {
        String cacheKey = makeCacheKey(cachePrefix);
        String cacheList = makeListCacheKey(cacheListPrefix);
        redisTemplate.opsForHash().delete(cacheKey, id);
        redisTemplate.delete(cacheList);
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
                T value = (T) pair.getValue();
                if (Objects.equals(value.getId(), id)) {
                    redisTemplate.delete(cacheKeyName);
                }
            });
            cursor.close();
        }));
    }
}
