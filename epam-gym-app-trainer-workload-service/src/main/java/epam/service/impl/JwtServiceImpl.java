package epam.service.impl;

import epam.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {


    @Value("${application.security.internal-token.keys.secret-key}")
    private String secretKey;

    @Value("${application.security.internal-token.audience-id}")
    private String expectedAudience;

    @Value("${application.security.internal-token.issuer}")
    private String tokenIssuer;

    @Override
    public String extractServiceSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public List<String> extractScopes(String token) {
        log.info("Extracting scopes from token");
        return extractClaim(token, claims -> {
            log.info("ExtractScopes claims : {}", claims);
            Object scopes = claims.get("scope");
            if (scopes instanceof String) {
                return List.of((String) scopes);
            } else if (scopes instanceof List) {
                return (List<String>) scopes;
            }
            return List.of();
        });
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean validateInternalToken(String token) {
        try {

            final String serviceId = extractServiceSubject(token);
            final List<String> audiences = extractClaim(token, claims -> claims.getAudience().stream().toList());
            String tokenType = tokenType(token);
            log.info("Validating token type: {}", tokenType);
            if (!tokenType.equals("internal-token")) {
                return false;
            }
            log.info("Validating audiences: {}", audiences);

            if (audiences == null || !audiences.contains(expectedAudience)) {
                log.warn("Service token validation failed: Incorrect audience. Expected '{}', got '{}'", expectedAudience, audiences);
                return false;
            }
            log.info("Validating issuer: {}", tokenIssuer);

            if (!isTokenNotExpired(token)) {
                log.warn("Service token validation failed: Token expired for service {}", serviceId);
                return false;
            }

            String issuer = extractClaim(token, Claims::getIssuer);
            log.info("Validating issuer: {}", issuer);

            return tokenIssuer.equals(issuer);

        } catch (Exception e) {
            log.error("Error validating service token: {}", e.getMessage());
            return false;
        }
    }


    public String tokenType(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("token_type").toString();
    }


    @Override
    public boolean isTokenNotExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
