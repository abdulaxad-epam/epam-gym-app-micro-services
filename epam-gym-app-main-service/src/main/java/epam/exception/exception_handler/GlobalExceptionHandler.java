package epam.exception.exception_handler;

import epam.aop.Logging;
import epam.exception.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TraineeNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleTraineeNotFound(TraineeNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionMessage.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(TrainerNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleTrainerNotFound(TrainerNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionMessage.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(TrainingNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleTrainingNotFound(TrainingNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionMessage.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(TraineeHasNotAssignedBeforeException.class)
    public ResponseEntity<ExceptionMessage> handleTraineeHasNotAssignedBefore(TraineeHasNotAssignedBeforeException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionMessage.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionMessage.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidTokenType.class)
    public ResponseEntity<ExceptionMessage> handleInvalidTokenType(InvalidTokenType e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionMessage.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(UsernameGenerateException.class)
    public ResponseEntity<ExceptionMessage> handleUsernameGenerate(UsernameGenerateException e) {
        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .body(ExceptionMessage.builder()
                        .status(HttpStatus.SEE_OTHER.value())
                        .error(HttpStatus.SEE_OTHER.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(UserNotAuthenticated.class)
    public ResponseEntity<ExceptionMessage> handleUserNotAuthenticated(UserNotAuthenticated e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionMessage.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(TrainingTypeNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleTrainingTypeNotFound(TrainingTypeNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionMessage.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(e.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(TraineeHasAssignedBeforeException.class)
    public ResponseEntity<ExceptionMessage> handleTraineeHasAssignedBefore(TraineeHasAssignedBeforeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionMessage.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(e.getMessage())
                        .requestId(Logging.getTransactionId())
                        .message(e.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(TrainerWorkloadIsUnavailableException.class)
    public ResponseEntity<ExceptionMessage> handleTrainerWorkloadIsUnavailable(TrainerWorkloadIsUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ExceptionMessage.builder()
                        .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(e.getLocalizedMessage())
                        .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionMessage> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ExceptionMessage.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(errors.values().toString())
                        .build()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionMessage> handleConstraintViolation(ConstraintViolationException e) {

        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ExceptionMessage.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(errors.values().toString())
                        .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionMessage> handleJsonParsingException(HttpMessageNotReadableException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid date format: " + e.getMostSpecificCause().getMessage() + ". Must be yyyy-MM-dd");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ExceptionMessage.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(error.values().toString())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessage> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionMessage.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .requestId(Logging.getTransactionId())
                        .message(e.getMessage())
                        .build()
                );
    }
}
