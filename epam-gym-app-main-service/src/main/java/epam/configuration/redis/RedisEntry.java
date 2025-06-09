package epam.configuration.redis;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class RedisEntry {
    @Getter
    private final Object value;
    private final long expiryTimeMillis;

    public RedisEntry(Object value, long ttl, TimeUnit unit) {
        this.value = value;
        this.expiryTimeMillis = System.currentTimeMillis() + unit.toMillis(ttl);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTimeMillis;
    }
}
