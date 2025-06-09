package epam.exception.exception;

public class TrainerNotFoundException extends GymBaseException {
    public TrainerNotFoundException(String message) {
        super(message, "TRAINER_NOT_FOUND");
    }
}
