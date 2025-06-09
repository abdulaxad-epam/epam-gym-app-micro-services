package epam.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;


@Component
public class ServiceAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof ServiceAuthenticationToken)) {
            return null;
        }
        ServiceAuthenticationToken token = (ServiceAuthenticationToken) authentication;

        if ("trainer-workload-service".equals(token.getPrincipal())) {
            return new ServiceAuthenticationToken(token.getPrincipal().toString(), Collections.singletonList(new SimpleGrantedAuthority("trainer-workload-service:read")));
        } else {
            throw new BadCredentialsException("Invalid service ID");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ServiceAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
