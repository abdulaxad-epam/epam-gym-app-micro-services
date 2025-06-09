package epam.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.function.Function;


@Service
public interface JwtService {
    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    Claims extractAllClaims(String token);

    void blackList(HttpServletRequest request);

    String generateAccessToken(UserDetails user);

    String generateRefreshToken(UserDetails user);

    boolean validateAccessToken(String token, UserDetails userDetails);

    boolean validateRefreshToken(String token, UserDetails userDetails);

    boolean isTokenNotExpired(String token);
}
