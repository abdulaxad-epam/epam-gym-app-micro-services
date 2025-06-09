package epam.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceAuthenticationProviderTest {

    private ServiceAuthenticationProvider provider;

    @BeforeEach
    void setUp() {
        provider = new ServiceAuthenticationProvider();
    }

    @Test
    void testAuthenticate_ValidServiceId() {
        ServiceAuthenticationToken token = new ServiceAuthenticationToken("trainer-workload-service", null);
        Authentication result = provider.authenticate(token);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals("trainer-workload-service", result.getPrincipal());

        List<SimpleGrantedAuthority> expectedAuthorities = List.of(
                new SimpleGrantedAuthority("trainer-workload-service:read")
        );
        assertEquals(expectedAuthorities, result.getAuthorities());
    }

    @Test
    void testAuthenticate_InvalidServiceId_ThrowsException() {
        ServiceAuthenticationToken token = new ServiceAuthenticationToken("unauthorized-service", null);

        assertThrows(BadCredentialsException.class, () -> {
            provider.authenticate(token);
        });
    }

    @Test
    void testAuthenticate_WithUnsupportedAuthenticationType_ReturnsNull() {
        Authentication otherAuth = new Authentication() {
            @Override
            public List<SimpleGrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }

            @Override
            public String getName() {
                return "test";
            }
        };

        Authentication result = provider.authenticate(otherAuth);
        assertNull(result);
    }

    @Test
    void testSupports_WithCorrectClass_ReturnsTrue() {
        assertTrue(provider.supports(ServiceAuthenticationToken.class));
    }

    @Test
    void testSupports_WithIncorrectClass_ReturnsFalse() {
        assertFalse(provider.supports(String.class));
    }
}
