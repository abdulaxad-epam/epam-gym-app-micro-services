package epam.exception.exception;

public class InvalidTokenType extends RuntimeException {
    public InvalidTokenType(String message) {
        super(message);
    }
}
