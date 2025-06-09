package epam.configuration.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
@EnableScheduling
public class RedisTemplate {

    private final Map<String, RedisEntry> store = new ConcurrentHashMap<>();

    public ValueOperations opsForValue() {
        return new ValueOperations();
    }


    public Boolean hasKey(String token) {
        RedisEntry entry = store.get(token);
        return entry != null && !entry.isExpired();
    }

    @Scheduled(fixedRate = 600)
    public void cleanUpExpiredEntries() {
        store.entrySet().removeIf(entry ->
                entry.getValue().isExpired());
    }

    public class ValueOperations {
        public void set(String key, Object value, long timeout, TimeUnit unit) {
            store.put(key, new RedisEntry(value, timeout, unit));
        }

        public Object get(String key) {
            RedisEntry entry = store.get(key);
            if (entry == null || entry.isExpired()) {
                store.remove(key);
                return null;
            }
            return entry.getValue();
        }
    }
}
