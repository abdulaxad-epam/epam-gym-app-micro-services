package epam.service;

import epam.configuration.redis.RedisTemplate;
import epam.exception.exception.InvalidTokenType;
import epam.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class JwtServiceTest {

    private JwtServiceImpl jwtService;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private RedisTemplate.ValueOperations valueOperations;

    private final String secretKey = Base64.getEncoder().encodeToString("testtesttesttesttesttesttesttest".getBytes());

    private User userDetails;

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set private field: " + fieldName, e);
        }
    }


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtServiceImpl(redisTemplate);

        setPrivateField(jwtService, "secretKey", secretKey);
        setPrivateField(jwtService, "accessExpiration", 100000L);
        setPrivateField(jwtService, "refreshExpiration", 200000L);

        userDetails = new User("testuser", "password", List.of((GrantedAuthority) () -> "ROLE_USER"));

    }

    @Test
    void shouldGenerateAndValidateAccessTokenSuccessfully() {
        String token = jwtService.generateAccessToken(userDetails);

        assertNotNull(token);
        assertTrue(jwtService.validateAccessToken(token, userDetails));
    }

    @Test
    void shouldGenerateAndValidateRefreshTokenSuccessfully() {
        String token = jwtService.generateRefreshToken(userDetails);

        assertNotNull(token);
        assertTrue(jwtService.validateRefreshToken(token, userDetails));
    }

    @Test
    void shouldExtractUsernameCorrectly() {
        String token = jwtService.generateAccessToken(userDetails);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(userDetails.getUsername(), extractedUsername);
    }

    @Test
    void shouldReturnFalseForBlacklistedToken() {
        String token = jwtService.generateAccessToken(userDetails);
        when(redisTemplate.hasKey(token)).thenReturn(true);

        assertFalse(jwtService.validateAccessToken(token, userDetails));
        assertFalse(jwtService.validateRefreshToken(token, userDetails));
    }

    @Test
    void shouldThrowInvalidTokenTypeWhenBlacklistingAccessToken() {
        String token = jwtService.generateAccessToken(userDetails);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + token);

        InvalidTokenType ex = assertThrows(InvalidTokenType.class,
                () -> jwtService.blackList(httpServletRequest));

        assertEquals("Invalid refresh token type", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidAuthorizationHeader() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

        InvalidTokenType ex = assertThrows(InvalidTokenType.class,
                () -> jwtService.blackList(httpServletRequest));

        assertEquals("Invalid token", ex.getMessage());
    }

    @Test
    void shouldDetectTokenExpirationCorrectly() {
        String token = jwtService.generateAccessToken(userDetails);
        assertTrue(jwtService.isTokenNotExpired(token));
    }

    @Test
    void shouldExtractAllClaimsSuccessfully() {
        String token = jwtService.generateAccessToken(userDetails);
        Claims claims = jwtService.extractAllClaims(token);

        assertEquals("access", claims.get("token_type"));
        assertEquals("testuser", claims.getSubject());
    }

    @Test
    void shouldExtractTokenTypeCorrectly() {
        String token = jwtService.generateRefreshToken(userDetails);
        assertEquals("refresh", jwtService.tokenType(token));
    }
}
