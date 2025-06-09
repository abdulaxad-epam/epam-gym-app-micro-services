package epam.exception.exception;

public class TraineeNotFoundException extends GymBaseException {
    public TraineeNotFoundException(String message) {
        super(message, "TRAINEE_NOT_FOUND");
    }
}
