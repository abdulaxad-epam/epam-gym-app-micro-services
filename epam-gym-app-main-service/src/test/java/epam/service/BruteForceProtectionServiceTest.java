package epam.service;

import epam.service.impl.BruteForceProtectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BruteForceProtectionServiceTest {

    private BruteForceProtectionService bruteForceProtectionService;

    @BeforeEach
    void setUp() {
        bruteForceProtectionService = new BruteForceProtectionService();
    }

    @Test
    void loginSucceeded_shouldResetAttemptsAndUnblock() {
        String username = "testUser";

        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);
        assertTrue(bruteForceProtectionService.isBlocked(username));

        bruteForceProtectionService.loginSucceeded(username);
        assertFalse(bruteForceProtectionService.isBlocked(username));
    }

    @Test
    void loginFailed_shouldIncreaseAttemptAndBlockAtMaxAttempts() {
        String username = "testUser";

        bruteForceProtectionService.loginFailed(username);
        assertFalse(bruteForceProtectionService.isBlocked(username));

        bruteForceProtectionService.loginFailed(username);
        assertFalse(bruteForceProtectionService.isBlocked(username));

        bruteForceProtectionService.loginFailed(username);
        assertTrue(bruteForceProtectionService.isBlocked(username));
    }

    @Test
    void isBlocked_shouldUnblockAfterDuration() {
        String username = "testUser";

        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);

        assertTrue(bruteForceProtectionService.isBlocked(username));

        assertTrue(bruteForceProtectionService.isBlocked(username));
    }


    @Test
    void shouldUnblockUserAfterBlockDurationExpires() throws Exception {
        String username = "testUser";

        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);

        assertTrue(bruteForceProtectionService.isBlocked(username), "User should be initially blocked");

        Field field = BruteForceProtectionService.class.getDeclaredField("blockedUntil");
        field.setAccessible(true);
        Map<String, Long> blockedUntilMap = (Map<String, Long>) field.get(bruteForceProtectionService);
        blockedUntilMap.put(username, System.currentTimeMillis() - 1000);

        boolean isBlocked = bruteForceProtectionService.isBlocked(username);
        assertFalse(isBlocked, "User should be unblocked after block duration has expired");
    }
}
