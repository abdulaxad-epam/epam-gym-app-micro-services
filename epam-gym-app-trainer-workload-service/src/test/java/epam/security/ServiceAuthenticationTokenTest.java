package epam.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceAuthenticationTokenTest {

    @Test
    void testConstructorSetsFieldsCorrectly() {
        String serviceId = "trainer-workload-service";
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("trainer-workload-service:read"));

        ServiceAuthenticationToken token = new ServiceAuthenticationToken(serviceId, authorities);

        assertEquals(serviceId, token.getPrincipal());
        assertNull(token.getCredentials());
        assertTrue(token.isAuthenticated());
        assertEquals(1, token.getAuthorities().size());
        assertTrue(token.getAuthorities().stream().anyMatch(
                authority -> authority.getAuthority().equals("trainer-workload-service:read")));
    }

    @Test
    void testSetAuthenticatedFalse() {
        String serviceId = "trainer-workload-service";
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("trainer-workload-service:read"));

        ServiceAuthenticationToken token = new ServiceAuthenticationToken(serviceId, authorities);
        assertTrue(token.isAuthenticated());

        token.setAuthenticated(false);
        assertFalse(token.isAuthenticated());
    }

    @Test
    void testSetAuthenticatedTrueThrowsException() {
        String serviceId = "trainer-workload-service";
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("trainer-workload-service:read"));

        ServiceAuthenticationToken token = new ServiceAuthenticationToken(serviceId, authorities);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                token.setAuthenticated(true));

        assertEquals("Cannot set this token to authenticated. Use the constructor with authorities.", exception.getMessage());
    }
}
