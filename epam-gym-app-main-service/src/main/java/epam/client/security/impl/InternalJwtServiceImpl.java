package epam.client.security.impl;

import epam.client.security.InternalJwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class InternalJwtServiceImpl implements InternalJwtService {
    @Value("${application.security.internal-token.keys.secret-key}")
    private String secretKey;

    @Value("${application.security.internal-token.token-expiration}")
    private long tokenExpiration;

    @Value("${application.security.internal-token.subject-id}")
    private String subjectId;

    @Value("${application.security.internal-token.issuer-id}")
    private String issuerId;

    @Override
    public String generateServiceToken(String audienceId, String scope) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("scope", String.join(" ", scope));
        claims.put("token_type", "internal-token");

        return Jwts.builder()
                .claims(claims)
                .audience()
                .add(audienceId)
                .and()
                .subject(subjectId)
                .issuer(issuerId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
