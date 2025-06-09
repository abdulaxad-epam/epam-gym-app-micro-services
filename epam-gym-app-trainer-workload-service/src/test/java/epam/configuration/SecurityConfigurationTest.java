package epam.configuration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class SecurityConfigurationTest {

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @Test
    public void testSecurityConfigurationThrowsException() {
        assertThrows(UsernameNotFoundException.class, () ->
                securityConfiguration.userDetailsService().loadUserByUsername("username"));
    }

}
