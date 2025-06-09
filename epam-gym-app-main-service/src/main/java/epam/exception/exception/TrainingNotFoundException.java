package epam.exception.exception;

public class TrainingNotFoundException extends GymBaseException {
    public TrainingNotFoundException(String message) {
        super(message, "TRAINING_NOT_FOUND");
    }
}
