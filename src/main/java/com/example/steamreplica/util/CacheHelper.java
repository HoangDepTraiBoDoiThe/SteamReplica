package com.example.steamreplica.util;

import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.service.exception.CacheException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class CacheHelper {
    private final CacheManager cacheManager;

    public <T extends BaseCacheableModel> void updateCacheSelective(T objectToCache, String entityCacheName, String... entityListCacheNames) {
        extracted(objectToCache, List.of(entityCacheName), Arrays.stream(entityListCacheNames).toList());
    }

    public <T extends BaseCacheableModel> void updateCacheSelective(T objectToCache, List<String> entityCacheNames, String... entityListCacheNames) {
        extracted(objectToCache, entityCacheNames, Arrays.stream(entityListCacheNames).toList());
    }

    public <T extends BaseCacheableModel> void updateCacheSelective(T objectToCache, List<String> entityCacheNames, List<String> entityListCacheNames) {
        extracted(objectToCache, entityCacheNames, entityListCacheNames);
    }

    private <T extends BaseCacheableModel> void extracted(T objectToCache, List<String> entityCacheName, List<String> entityListCacheNames) {
        try {
            for (String cacheName : entityCacheName) {
                // Fetch the entity cache
                Optional.ofNullable(cacheManager.getCache(cacheName))
                        .ifPresent(entityCache -> entityCache.put(objectToCache.getId(), objectToCache));
            }

            for (String entityListCacheName : entityListCacheNames) {
                // Fetch the entity list cache
                Optional.ofNullable(cacheManager.getCache(entityListCacheName))
                        .map(entityListCache -> entityListCache.get(entityListCacheName, List.class))
                        .map(cacheList -> {
                            // Replace the item in the list and return the modified list
                            return replaceInList(cacheList, objectToCache, T::getId);
                        })
                        .ifPresent(modifiedList -> {
                            // Update the cache with the modified list
                            Cache entityListCache = cacheManager.getCache(entityListCacheName);
                            if (entityListCache != null) {
                                entityListCache.put(entityListCacheName, modifiedList);
                            }
                        });
            }
        } catch (Exception e) {
            throw new CacheException("Error while updating cache.", e);
        }
    }

    private <T extends BaseCacheableModel, ID> List<T> replaceInList(List<T> list, T objectToCache, Function<T, ID> idExtractor) {
        if (list != null) {
            list.replaceAll(o -> idExtractor.apply(o).equals(idExtractor.apply(objectToCache)) ? objectToCache : o);
        }
        return list;
    }

    public <T extends BaseCacheableModel> void deleteCacheSelective(T objectToCache, List<String> entityCacheNames, List<String> entityListCacheNames) {
        deleteCache(objectToCache, entityCacheNames, entityListCacheNames);
    }

    public <T extends BaseCacheableModel> void deleteCacheSelective(T objectToCache, List<String> entityCacheNames, String... entityListCacheNames) {
        deleteCache(objectToCache, entityCacheNames, Arrays.stream(entityListCacheNames).toList());
    }
    public <T extends BaseCacheableModel> void deleteCacheSelective(T objectToCache, String entityCacheName, String... entityListCacheName) {
        deleteCache(objectToCache, List.of(entityCacheName), Arrays.stream(entityListCacheName).toList());
    }

    private <T extends BaseCacheableModel> void deleteCache(T objectToCache, List<String> entityNames, List<String> entityListCacheNames) {
        try {
            for (String entityCacheName : entityNames) {
                Cache entityCache = cacheManager.getCache(entityCacheName);
                Optional.ofNullable(entityCache).ifPresent(cache -> cache.evict(objectToCache.getId()));
            }
            for (String cacheName : entityListCacheNames) {
                Cache entityListCache = cacheManager.getCache(cacheName);
                if (entityListCache == null) continue;

                List<T> cacheList = entityListCache.get(cacheName, List.class);
                if (cacheList == null) continue;

                cacheList.remove(objectToCache);
                if (!cacheList.isEmpty()) entityListCache.put(cacheName, cacheList);
            }
        } catch (Exception e) {
            throw new CacheException("Error while deleting cache.", e);
        }
    }
}
