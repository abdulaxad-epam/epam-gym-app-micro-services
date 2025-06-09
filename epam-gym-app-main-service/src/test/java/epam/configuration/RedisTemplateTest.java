package epam.configuration;

import epam.configuration.redis.RedisEntry;
import epam.configuration.redis.RedisTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RedisTemplateTest {

    private RedisTemplate redisTemplate;
    private RedisTemplate.ValueOperations valueOps;

    @BeforeEach
    void setUp() {
        redisTemplate = new RedisTemplate();
        valueOps = redisTemplate.opsForValue();
    }

    @Test
    void testSetAndGetValue_NotExpired() {
        String key = "token123";
        String value = "userData";

        valueOps.set(key, value, 5, TimeUnit.SECONDS);

        Object retrieved = valueOps.get(key);

        assertNotNull(retrieved);
        assertEquals(value, retrieved);
    }

    @Test
    void testGetValue_Expired() throws InterruptedException {
        String key = "tokenExpired";
        String value = "tempData";

        valueOps.set(key, value, 1, TimeUnit.MILLISECONDS);

        Thread.sleep(10); // Allow expiration

        Object retrieved = valueOps.get(key);

        assertNull(retrieved);
    }

    @Test
    void testHasKey_KeyExistsAndNotExpired() {
        String key = "validKey";
        valueOps.set(key, "someData", 5, TimeUnit.SECONDS);

        assertTrue(redisTemplate.hasKey(key));
    }

    @Test
    void testHasKey_KeyDoesNotExist() {
        assertFalse(redisTemplate.hasKey("nonExistent"));
    }

    @Test
    void testHasKey_KeyExpired() throws InterruptedException {
        String key = "expiredKey";
        valueOps.set(key, "someData", 1, TimeUnit.MILLISECONDS);

        Thread.sleep(10);

        assertFalse(redisTemplate.hasKey(key));
    }

    @Test
    void testCleanUpExpiredEntries_RemovesOnlyExpired() throws InterruptedException {
        valueOps.set("key1", "value1", 1, TimeUnit.MILLISECONDS);
        valueOps.set("key2", "value2", 5, TimeUnit.SECONDS);

        Thread.sleep(10);

        redisTemplate.cleanUpExpiredEntries();

        assertNull(valueOps.get("key1"));
        assertEquals("value2", valueOps.get("key2"));
    }

    @Test
    void testRedisEntry_ExpiryLogic() throws InterruptedException {
        RedisEntry entry = new RedisEntry("test", 5, TimeUnit.MILLISECONDS);
        assertFalse(entry.isExpired());

        Thread.sleep(10);

        assertTrue(entry.isExpired());
    }
}
