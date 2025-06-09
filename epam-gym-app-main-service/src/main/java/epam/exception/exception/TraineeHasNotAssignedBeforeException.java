package epam.exception.exception;

public class TraineeHasNotAssignedBeforeException extends GymBaseException {
    public TraineeHasNotAssignedBeforeException(String message) {
        super(message, "TRAINEE_HAS_NOT_ASSIGNED_BEFORE_EXCEPTION");
    }
}
