package epam.exception;

public class DailyTrainingDurationExceededException extends RuntimeException {
    public DailyTrainingDurationExceededException(String message) {
        super(message);
    }
}
