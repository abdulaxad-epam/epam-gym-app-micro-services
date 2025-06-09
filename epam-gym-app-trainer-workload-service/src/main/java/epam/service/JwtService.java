package epam.service;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;


@Service
public interface JwtService {
    String extractServiceSubject(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    Claims extractAllClaims(String token);

    boolean validateInternalToken(String token);

    boolean isTokenNotExpired(String token);

    List<String> extractScopes(String token);
}
