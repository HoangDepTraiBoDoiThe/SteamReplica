package com.example.steamreplica;
import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.service.exception.CacheException;
import com.example.steamreplica.util.CacheHelper;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.HashOperations;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CacheHelperTest {

    private CacheHelper MakeCacheHelperWithMockRedisTemplate(RedisTemplate<String, Object> mockRedisTemplate) {
        return new CacheHelper(mockRedisTemplate, null);
    }

    @Test
    public void GetCache_ExistingObject_ReturnsCachedObject() {
        // Arrange
        RedisTemplate<String, Object> mockRedisTemplate = mock(RedisTemplate.class);
        HashOperations<String, Object, Object> mockHashOperations = mock(HashOperations.class);
        when(mockRedisTemplate.opsForHash()).thenReturn(mockHashOperations);
        String cacheKeyPrefix = "prefix";
        String key = "key";
        BaseCacheableModel expectedObject = new BaseCacheableModel();
        when(mockHashOperations.get(cacheKeyPrefix + "::", key)).thenReturn(expectedObject);
        CacheHelper cacheHelper = MakeCacheHelperWithMockRedisTemplate(mockRedisTemplate);

        // Act
        BaseCacheableModel result = cacheHelper.getCache(cacheKeyPrefix, key, null, null);

        // Assert
        assertEquals(expectedObject, result);
    }

    @Test
    public void GetCache_NonExistingObject_CreatesAndCachesObject() {
        // Arrange
        RedisTemplate<String, Object> mockRedisTemplate = mock(RedisTemplate.class);
        HashOperations<String, Object, Object> mockHashOperations = mock(HashOperations.class);
        when(mockRedisTemplate.opsForHash()).thenReturn(mockHashOperations);
        String cacheKeyPrefix = "prefix";
        String key = "key";
        BaseCacheableModel newObject = new BaseCacheableModel();
        Function<Object, BaseCacheableModel> trFunction = repo -> newObject;
        when(mockHashOperations.get(cacheKeyPrefix + "::", key)).thenReturn(null);
        CacheHelper cacheHelper = MakeCacheHelperWithMockRedisTemplate(mockRedisTemplate);

        // Act
        BaseCacheableModel result = cacheHelper.getCache(cacheKeyPrefix, key, null, trFunction);

        // Assert
        assertEquals(newObject, result);
        verify(mockHashOperations).put(cacheKeyPrefix + "::", key, newObject);
    }

    @Test
    public void GetCache_ErrorOccurs_ThrowsCacheException() {
        // Arrange
        RedisTemplate<String, Object> mockRedisTemplate = mock(RedisTemplate.class);
        HashOperations<String, Object, Object> mockHashOperations = mock(HashOperations.class);
        when(mockRedisTemplate.opsForHash()).thenReturn(mockHashOperations);
        String cacheKeyPrefix = "prefix";
        String key = "key";
        Function<Object, BaseCacheableModel> trFunction = repo -> new BaseCacheableModel();
        when(mockHashOperations.get(cacheKeyPrefix + "::", key)).thenThrow(new RuntimeException("Redis error"));
        CacheHelper cacheHelper = MakeCacheHelperWithMockRedisTemplate(mockRedisTemplate);

        // Act & Assert
        assertThrows(CacheException.class, () -> cacheHelper.getCache(cacheKeyPrefix, key, null, trFunction));
    }
}
