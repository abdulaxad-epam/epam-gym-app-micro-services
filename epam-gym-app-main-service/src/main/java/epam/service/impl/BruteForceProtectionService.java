package epam.service.impl;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BruteForceProtectionService {

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, Long> blockedUntil = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION = 5 * 60 * 1000;

    public void loginSucceeded(String username) {
        attempts.remove(username);
        blockedUntil.remove(username);
    }

    public void loginFailed(String username) {
        int current = attempts.getOrDefault(username, 0) + 1;
        attempts.put(username, current);
        if (current >= MAX_ATTEMPTS) {
            blockedUntil.put(username, System.currentTimeMillis() + BLOCK_DURATION);
        }
    }

    public boolean isBlocked(String username) {
        Long until = blockedUntil.get(username);
        if (until == null) return false;
        if (System.currentTimeMillis() > until) {
            blockedUntil.remove(username);
            attempts.remove(username);
            return false;
        }
        return true;
    }
}
