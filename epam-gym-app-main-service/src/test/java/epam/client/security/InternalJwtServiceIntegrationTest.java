package epam.client.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class InternalJwtServiceIntegrationTest {

    @Autowired
    private InternalJwtService internalJwtService;

    @Value("${application.security.internal-token.keys.secret-key}")
    private String secretKey;

    @Value("${application.security.internal-token.token-expiration}")
    private long tokenExpiration;

    @Value("${application.security.internal-token.subject-id}")
    private String subjectId;

    @Value("${application.security.internal-token.issuer-id}")
    private String issuerId;

    private String trainerAudience;

    private String trainerWorkloadScope;

    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        trainerAudience = "trainer-workload-service";
        trainerWorkloadScope = "trainer_workload:read trainer_workload:write";
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    void testGenerateServiceToken_ShouldGenerateValidTokenWithCorrectClaims() {

        String token = internalJwtService.generateServiceToken(trainerAudience, trainerWorkloadScope);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(subjectId, claims.getSubject(), "Subject should match");
        assertEquals(issuerId, claims.getIssuer(), "Issuer should match");
        assertTrue(claims.getAudience().contains(trainerAudience), "Audience should contain the specified ID");

        assertEquals(trainerWorkloadScope, claims.get("scope", String.class), "Scope should match");
        assertEquals("internal-token", claims.get("token_type", String.class), "Token type should be internal-token");

        assertNotNull(claims.getIssuedAt(), "IssuedAt should not be null");
        assertNotNull(claims.getExpiration(), "Expiration should not be null");

        long expectedExpirationMillis = System.currentTimeMillis() + tokenExpiration;
        long tolerance = 1500;
        assertTrue(Math.abs(claims.getExpiration().getTime() - expectedExpirationMillis) < tolerance,
                "Expiration time should be approximately " + tokenExpiration + "ms from now");
        assertTrue(claims.getIssuedAt().before(claims.getExpiration()), "IssuedAt should be before Expiration");
    }

    @Test
    void testGenerateServiceToken_WithDifferentScopeAndAudience() {


        // Act
        String token = internalJwtService.generateServiceToken(trainerAudience, trainerWorkloadScope);

        // Assert
        assertNotNull(token);
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertTrue(claims.getAudience().contains(trainerAudience), "Audience should contain the new ID");
        assertEquals(trainerWorkloadScope, claims.get("scope", String.class), "Scope should match the new scope");
    }

    @Test
    void testGenerateServiceToken_TokenIsInvalidWithoutCorrectKey() {
        String token = internalJwtService.generateServiceToken(trainerAudience, trainerWorkloadScope);

        byte[] wrongKeyBytes = Decoders.BASE64.decode(secretKey + "wrongKey");
        SecretKey wrongSigningKey = Keys.hmacShaKeyFor(wrongKeyBytes);

        assertThrows(io.jsonwebtoken.security.SignatureException.class, () ->
                        Jwts.parser()
                                .verifyWith(wrongSigningKey)
                                .build()
                                .parseSignedClaims(token),
                "Token should not be parsable with a wrong key");
    }

    @Test
    void testGenerateServiceToken_TokenExpiration() throws InterruptedException {
        long shortExpiration = 100;

        String shortLivedToken = Jwts.builder()
                .claims(new HashMap<>(Map.of("scope", "test:short")))
                .audience()
                .add("short-test-audience")
                .and()
                .subject(subjectId)
                .issuer(issuerId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + shortExpiration))
                .signWith(getSigningKeyFromSecret(secretKey), Jwts.SIG.HS256)
                .compact();

        Thread.sleep(shortExpiration + 50);

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () ->
                        Jwts.parser()
                                .verifyWith(signingKey)
                                .build()
                                .parseSignedClaims(shortLivedToken),
                "Expired token should throw ExpiredJwtException");
    }

    private SecretKey getSigningKeyFromSecret(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}