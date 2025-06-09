package epam.exception.exception;

public class UsernameGenerateException extends RuntimeException {
    public UsernameGenerateException(String message, IllegalAccessException e) {
        super(message, e);
    }
}
