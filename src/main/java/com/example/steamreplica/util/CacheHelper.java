package com.example.steamreplica.util;

import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.service.exception.CacheException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class CacheHelper {
    private final RedisTemplate<String, Object> redisTemplate;

    private static String makeCacheKey(String cacheKeyPrefix, Object key) {
        return cacheKeyPrefix + "::" + key;
    }
    private static String makePaginationCacheKey(String cacheKeyPrefix, int page) {
        return cacheKeyPrefix + "::Pagination_Cache::" + page;
    }
    private static String makeListCacheKey(String cacheKeyPrefix) {
        return cacheKeyPrefix + "::List_Cache";
    }

    // Get cache from Redis with cacheKeyPrefix and key and return the object if it exists, otherwise create the object with trFunction and cache it.
    public <R extends BaseCacheableModel, T> R getCache(String cacheKeyPrefix, Object key, T repo, Function<T, R> trFunction) {
        try {
            String cacheKey = makeCacheKey(cacheKeyPrefix, key);

            R object = (R) redisTemplate.opsForValue().get(cacheKey);
            if (object != null) return object;

            R objectToCache = trFunction.apply(repo);
            redisTemplate.opsForValue().set(cacheKey, objectToCache);

            return objectToCache;
        } catch (Exception e) {
            throw new CacheException("Error while getting cache.", e);
        }
    }
    
    // Get target list in cache from Redis with cacheKeyPrefix and key and return the list if it exists, otherwise create the object with trFunction and cache it.
    public <R extends BaseCacheableModel, T> List<R> getListCache(String cacheKeyPrefix, Object key, T repo, Function<T, List<R>> trFunction) {
        try {
            String cacheKey = makeCacheKey(cacheKeyPrefix, key);

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
     * @param objectToUpdate The object to update in the cache.
     * @param cacheKeyPrefix The prefix for the cache key.
     * @param ListCacheKeyPrefix The prefix for the list cache key.
     */
    public <T extends BaseCacheableModel> void updateCache(T objectToUpdate, String cacheKeyPrefix, String ListCacheKeyPrefix) {
        // Update the object in the key-value store
        redisTemplate.opsForValue().set(makeCacheKey(cacheKeyPrefix, objectToUpdate.getId()), objectToUpdate);

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
     * @param objectToUpdate The object to be updated in the cache.
     * @param pageRange The range of pages to update in the cache.
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
    
    public void deleteCaches(String cachePrefix, long id, String cacheListPrefix) {
        String cacheKey = makeCacheKey(cachePrefix, id);
        String cacheList = makeListCacheKey(cachePrefix);
        redisTemplate.delete(cacheKey);
        redisTemplate.delete(cacheList);
    }

    /**
     * Deletes the cache entries associated with the specified object ID within the given page range and pagination cache key prefixes.
     *
     * @param id The ID of the object to match for deletion in the cache.
     * @param pageRange The range of pages to search for cache entries.
     * @param paginationCachePrefix The prefixes used to construct the pagination cache keys.
     */
    public <T extends BaseCacheableModel> void deletePaginationCache(long id, int pageRange, String... paginationCachePrefix ) {
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
//
//    public <T extends BaseCacheableModel> void updateCacheSelective(T objectToCache, String prefix, String cacheContainerName, Integer pageRange, String entityCacheName) {
//        updateCache(objectToCache, prefix, cacheContainerName, pageRange, List.of(entityCacheName));
//    }
//
//    private <T extends BaseCacheableModel> void updateCache(T objectToCache, String singleCachePrefix, String paginationCachePrefix, Integer pageRange) {
//        try {
//            String key = makeCacheKey(singleCachePrefix, objectToCache.getId());
//            redisTemplate.opsForValue().set(key, objectToCache);
//            
//            for (int i = 1; i <= pageRange; i++) {
//                String paginationCacheKey = makePaginationCacheKey(paginationCachePrefix, i);
//
//                List<T> list = redisTemplate.opsForHash().put();
//                if (list == null) return;
//                List<T> modifiedList = replaceInList(list, objectToCache, T::getId);
//
//                cacheContainer.put(cacheKeyName, modifiedList);
//            }
//        } catch (Exception e) {
//            throw new CacheException("Error while updating cache.", e);
//        }
//    }
//
//    private <T extends BaseCacheableModel, ID> List<T> replaceInList(List<T> list, T objectToCache, Function<T, ID> idExtractor) {
//        if (list != null) {
//            list.replaceAll(o -> idExtractor.apply(o).equals(idExtractor.apply(objectToCache)) ? objectToCache : o);
//        }
//        return list;
//    }
//
//    public <T extends BaseCacheableModel> void deleteCacheSelective(T objectToCache, String prefix, String cacheContainerName, Integer pageRange, String entityCacheName) {
//        deleteCache(objectToCache, prefix, cacheContainerName, pageRange, List.of(entityCacheName));
//    }
//
//    private <T extends BaseCacheableModel> void deleteCache(T objectToCache, String prefix, String cacheContainerName, Integer pageRange, List<String> entityCacheNames) {
//        try {
//            for (String entityCacheName : entityCacheNames) {
//                Cache entityCache = cacheManager.getCache(entityCacheName);
//                Optional.ofNullable(entityCache).ifPresent(cache -> cache.evict(objectToCache.getId()));
//            }
//            deletePaginationCaches(objectToCache, prefix, cacheContainerName, pageRange);
//        } catch (Exception e) { 
//            throw new CacheException("Error while deleting cache.", e);
//        }
//    }
//
//    public <T extends BaseCacheableModel> void deletePaginationCaches(T objectToCache, String prefix, String cacheContainerName, Integer pageRange) {
//        try {
//            String cacheKeyNamePrefix = prefix + "_Pagination_Cache_";
//            Cache cache = cacheManager.getCache(cacheContainerName);
//            if (cache == null) return;
//            
//            for (int i = 1; i <= pageRange; i++) {
//                String cacheKeyName = cacheKeyNamePrefix + i;
//                List<T> list = cache.get(cacheKeyName, List.class);
//                if (list != null && list.contains(objectToCache)) cache.evict(cacheKeyName);
//            }
//        } catch (Exception e) { 
//            throw new CacheException("Error while deleting cache.", e);
//        }
//    }
}
