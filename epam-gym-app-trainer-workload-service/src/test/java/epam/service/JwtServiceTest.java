package epam.service;

import epam.service.impl.JwtServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class JwtServiceTest {

    private JwtServiceImpl jwtService;

    private String expectedAudience;
    private String issuer;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        String secret = Base64.getEncoder().encodeToString("mySuperSecretKey1234567890ItIsSuperSecret".getBytes(StandardCharsets.UTF_8));
        expectedAudience = "internal-service";
        issuer = "my-service";

        jwtService = new JwtServiceImpl();
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
        ReflectionTestUtils.setField(jwtService, "expectedAudience", expectedAudience);
        ReflectionTestUtils.setField(jwtService, "tokenIssuer", issuer);
    }

    private String generateToken(Map<String, Object> claims, Date expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject("test-service")
                .audience().add(expectedAudience).and()
                .issuer(issuer)
                .expiration(expiration)
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    @Test
    void testExtractServiceSubject() {
        String token = generateToken(Map.of("token_type", "internal-token"), new Date(System.currentTimeMillis() + 100000));
        String subject = jwtService.extractServiceSubject(token);
        assertEquals("test-service", subject);
    }

    @Test
    void testExtractScopesAsString() {
        String token = generateToken(Map.of("scope", "read", "token_type", "internal-token"), new Date(System.currentTimeMillis() + 100000));
        List<String> scopes = jwtService.extractScopes(token);
        assertEquals(List.of("read"), scopes);
    }

    @Test
    void testExtractScopesAsList() {
        String token = generateToken(Map.of("scope", List.of("read", "write"), "token_type", "internal-token"), new Date(System.currentTimeMillis() + 100000));
        List<String> scopes = jwtService.extractScopes(token);
        assertEquals(List.of("read", "write"), scopes);
    }

    @Test
    void testTokenType() {
        String token = generateToken(Map.of("token_type", "internal-token"), new Date(System.currentTimeMillis() + 100000));
        String type = jwtService.tokenType(token);
        assertEquals("internal-token", type);
    }

    @Test
    void testIsTokenNotExpired_ValidToken() {
        String token = generateToken(Map.of("token_type", "internal-token"), new Date(System.currentTimeMillis() + 100000));
        assertTrue(jwtService.isTokenNotExpired(token));
    }

    @Test
    void testIsTokenNotExpired_ExpiredToken() {
        String token = generateToken(Map.of("token_type", "internal-token"), new Date(System.currentTimeMillis() - 100));
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenNotExpired(token));
    }

    @Test
    void testValidateInternalToken_Valid() {
        String token = generateToken(Map.of("token_type", "internal-token"), new Date(System.currentTimeMillis() + 100000));
        assertTrue(jwtService.validateInternalToken(token));
    }

    @Test
    void testValidateInternalToken_InvalidTokenType() {
        String token = generateToken(Map.of("token_type", "external-token"), new Date(System.currentTimeMillis() + 100000));
        assertFalse(jwtService.validateInternalToken(token));
    }

    @Test
    void testValidateInternalToken_InvalidAudience() {
        String token = Jwts.builder()
                .claims(Map.of("token_type", "internal-token"))
                .subject("test-service")
                .audience().add("other-audience").and()
                .issuer(issuer)
                .expiration(new Date(System.currentTimeMillis() + 100000))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
        assertFalse(jwtService.validateInternalToken(token));
    }

    @Test
    void testValidateInternalToken_ExpiredToken() {
        String token = generateToken(Map.of("token_type", "internal-token"), new Date(System.currentTimeMillis() - 1000));
        assertFalse(jwtService.validateInternalToken(token));
    }

    @Test
    void testValidateInternalToken_InvalidIssuer() {
        String token = Jwts.builder()
                .claims(Map.of("token_type", "internal-token"))
                .subject("test-service")
                .audience().add(expectedAudience).and()
                .issuer("wrong-issuer")
                .expiration(new Date(System.currentTimeMillis() + 100000))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
        assertFalse(jwtService.validateInternalToken(token));
    }

    @Test
    void testValidateInternalToken_ExceptionThrown() {
        String invalidToken = "invalid.token.structure";
        assertFalse(jwtService.validateInternalToken(invalidToken));
    }
}
