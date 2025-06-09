package epam.security;

import epam.exception.TokenExpiredException;
import epam.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ServiceAuthenticationFilterTest {

    @InjectMocks
    private ServiceAuthenticationFilter filter;

    @Mock
    private JwtService jwtService;

    @Mock
    private ServiceAuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testValidTokenAuthenticationSuccess() throws ServletException, IOException {
        String token = "valid-token";
        String serviceId = "trainer-workload-service";
        String headerValue = "Bearer " + token;

        when(request.getHeader("X-Internal-Token")).thenReturn(headerValue);
        when(jwtService.extractServiceSubject(token)).thenReturn(serviceId);
        when(jwtService.validateInternalToken(token)).thenReturn(true);
        when(jwtService.extractScopes(token)).thenReturn(List.of("trainer-workload-service:read"));

        filter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(serviceId, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(chain).doFilter(request, response);
    }

    @Test
    void testInvalidTokenTriggersEntryPoint() throws Exception {
        String token = "expired-token";
        String headerValue = "Bearer " + token;
        String serviceId = "trainer-workload-service";

        when(request.getHeader("X-Internal-Token")).thenReturn(headerValue);
        when(jwtService.extractServiceSubject(token)).thenReturn(serviceId);
        when(jwtService.validateInternalToken(token)).thenReturn(false);

        doNothing().when(authenticationEntryPoint)
                .commence(eq(request), eq(response), any());

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationEntryPoint, times(1)).commence(eq(request), eq(response), any());
    }

    @Test
    void testJwtServiceThrowsTokenExpiredException() throws Exception {
        String token = "expired-token";
        String headerValue = "Bearer " + token;
        String serviceId = "trainer-workload-service";

        when(request.getHeader("X-Internal-Token")).thenReturn(headerValue);
        when(jwtService.extractServiceSubject(token)).thenReturn(serviceId);
        when(jwtService.validateInternalToken(token)).thenThrow(new TokenExpiredException("Token expired"));

        doNothing().when(authenticationEntryPoint)
                .commence(eq(request), eq(response), any());

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationEntryPoint, times(1)).commence(eq(request), eq(response), any());
    }

    @Test
    void testHeaderMissingSkipsAuthentication() throws Exception {
        when(request.getHeader("X-Internal-Token")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void testMalformedTokenSkipsAuthentication() throws Exception {
        when(request.getHeader("X-Internal-Token")).thenReturn("InvalidToken");

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void testExceptionDuringServiceIdExtractionIsHandled() throws Exception {
        String token = "some-token";
        when(request.getHeader("X-Internal-Token")).thenReturn("Bearer " + token);
        when(jwtService.extractServiceSubject(token)).thenThrow(new RuntimeException("Parse error"));

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void testTransactionIdIsSet() throws Exception {
        when(request.getHeader("X-Internal-Token")).thenReturn(null);
        when(request.getHeader("X-Transaction-ID")).thenReturn("txn-123");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
