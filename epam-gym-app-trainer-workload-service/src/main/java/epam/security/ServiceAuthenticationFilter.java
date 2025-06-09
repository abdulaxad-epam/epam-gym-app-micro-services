package epam.security;


import epam.aop.Logging;
import epam.exception.TokenExpiredException;
import epam.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final ServiceAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        doTransactionInternal(request);
        String header = request.getHeader("X-Internal-Token");
        String token = null;
        String serviceId = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                serviceId = jwtService.extractServiceSubject(token);
            } catch (Exception e) {
                log.warn("Failed to extract service name from token: {}", e.getMessage());
            }
        }
        if (serviceId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtService.validateInternalToken(token)) {

                    List<String> scopes = jwtService.extractScopes(token);
                    List<SimpleGrantedAuthority> authorities = scopes.stream()
                            .map(scope -> new SimpleGrantedAuthority("ROLE_" + scope.toUpperCase().replace(":", "_"))).toList();

                    AbstractAuthenticationToken authenticationToken = new ServiceAuthenticationToken(serviceId, authorities);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    throw new TokenExpiredException("Invalid access token");
                }
            } catch (TokenExpiredException | AuthenticationException ex) {
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(request, response, new InsufficientAuthenticationException(ex.getMessage()));
            }
        }
        filterChain.doFilter(request, response);
    }

    private void doTransactionInternal(HttpServletRequest request) {
        String transactionId = request.getHeader("X-Transaction-ID");
        ThreadLocal<String> local = new ThreadLocal<>();
        local.set(transactionId);
        Logging.setTransactionId(local);
    }
}

