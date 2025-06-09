package epam.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class ServiceAuthenticationToken extends AbstractAuthenticationToken {

    private final String serviceId;

    public ServiceAuthenticationToken(String serviceId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.serviceId = serviceId;
        super.setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return this.serviceId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to authenticated. Use the constructor with authorities.");
        }
        super.setAuthenticated(false);
    }
}
