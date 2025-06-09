package epam.exception.exception;

public class GymBaseException extends RuntimeException {
    public GymBaseException(String message, String errorCode) {
        super(message);
    }
}
