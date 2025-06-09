package epam.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String invalidAccessToken) {
        super(invalidAccessToken);
    }
}
