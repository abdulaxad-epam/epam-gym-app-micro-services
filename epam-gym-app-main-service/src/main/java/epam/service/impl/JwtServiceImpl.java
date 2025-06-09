package epam.service.impl;

import epam.configuration.redis.RedisTemplate;
import epam.exception.exception.InvalidTokenType;
import epam.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final RedisTemplate redisTemplate;

    @Value("${application.security.jwt-token.keys.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt-token.access-expiration}")
    private long accessExpiration;

    @Value("${application.security.jwt-token.refresh-expiration}")
    private long refreshExpiration;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return buildToken(extraClaims, userDetails, expiration);
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
    public String generateAccessToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "access");
        claims.put("role", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return generateToken(claims, user, accessExpiration);

    }

    @Override
    public String generateRefreshToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "refresh");
        claims.put("role", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return generateToken(claims, user, refreshExpiration);

    }

    @Override
    public void blackList(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            var token = header.substring(7);

            long expiration = extractExpiration(token).getTime() - System.currentTimeMillis();
            if (tokenType(token).equals("refresh")) {
                redisTemplate.opsForValue().set(token, "blacklisted", expiration, TimeUnit.MILLISECONDS);
            } else {
                throw new InvalidTokenType("Invalid refresh token type");
            }
        } else {
            throw new InvalidTokenType("Invalid token");
        }
    }

    @Override
    public boolean validateAccessToken(String token, UserDetails userDetails) {
        if (isTokenBlacklisted(token)) {
            return false;
        }
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && isTokenNotExpired(token) && tokenType(token).equals("access");
    }

    @Override
    public boolean validateRefreshToken(String token, UserDetails userDetails) {
        if (isTokenBlacklisted(token)) {
            return false;
        }
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && isTokenNotExpired(token) && tokenType(token).equals("refresh");
    }

    public String tokenType(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("token_type").toString();
    }


    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
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

    private boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

}
