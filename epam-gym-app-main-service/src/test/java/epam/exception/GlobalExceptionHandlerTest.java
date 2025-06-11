package epam.exception;

import epam.exception.exception.InvalidTokenType;
import epam.exception.exception.TraineeHasAssignedBeforeException;
import epam.exception.exception.TraineeHasNotAssignedBeforeException;
import epam.exception.exception.TraineeNotFoundException;
import epam.exception.exception.TrainerNotFoundException;
import epam.exception.exception.TrainerWorkloadIsUnavailableException;
import epam.exception.exception.TrainingNotFoundException;
import epam.exception.exception.TrainingTypeNotFoundException;
import epam.exception.exception.UserNotAuthenticated;
import epam.exception.exception.UserNotFoundException;
import epam.exception.exception.UsernameGenerateException;
import epam.exception.exception_handler.ExceptionMessage;
import epam.exception.exception_handler.GlobalExceptionHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleTraineeNotFound() {
        String message = "Trainee not found";
        TraineeNotFoundException exception = new TraineeNotFoundException(message);

        ResponseEntity<ExceptionMessage> response = handler.handleTraineeNotFound(exception);

        assertEquals(404, response.getStatusCode().value());
        assertEquals(message, Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleTrainerNotFound() {
        TrainerNotFoundException exception = new TrainerNotFoundException("Trainer missing");
        ResponseEntity<ExceptionMessage> response = handler.handleTrainerNotFound(exception);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("Trainer missing", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleTrainingNotFound() {
        TrainingNotFoundException exception = new TrainingNotFoundException("Training not found");
        ResponseEntity<ExceptionMessage> response = handler.handleTrainingNotFound(exception);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("Training not found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleTraineeHasNotAssignedBefore() {
        TraineeHasNotAssignedBeforeException exception = new TraineeHasNotAssignedBeforeException("Not assigned before");
        ResponseEntity<ExceptionMessage> response = handler.handleTraineeHasNotAssignedBefore(exception);
        assertEquals(409, response.getStatusCode().value());
        assertEquals("Not assigned before", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleUserNotFound() {
        UserNotFoundException exception = new UserNotFoundException("User does not exist");
        ResponseEntity<ExceptionMessage> response = handler.handleUserNotFound(exception);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("User does not exist", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleInvalidTokenType() {
        InvalidTokenType exception = new InvalidTokenType("Invalid token type");
        ResponseEntity<ExceptionMessage> response = handler.handleInvalidTokenType(exception);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid token type", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleUsernameGenerate() {
        UsernameGenerateException exception = new UsernameGenerateException("Username generation failed", new IllegalAccessException("Could not set generated username"));
        ResponseEntity<ExceptionMessage> response = handler.handleUsernameGenerate(exception);
        assertEquals(303, response.getStatusCode().value());
        assertEquals("Username generation failed", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleUserNotAuthenticated() {
        UserNotAuthenticated exception = new UserNotAuthenticated("User not authenticated");
        ResponseEntity<ExceptionMessage> response = handler.handleUserNotAuthenticated(exception);
        assertEquals(403, response.getStatusCode().value());
        assertEquals("User not authenticated", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleTrainingTypeNotFound() {
        TrainingTypeNotFoundException exception = new TrainingTypeNotFoundException("Type not found");
        ResponseEntity<ExceptionMessage> response = handler.handleTrainingTypeNotFound(exception);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("Type not found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleTraineeHasAssignedBefore() {
        TraineeHasAssignedBeforeException exception = new TraineeHasAssignedBeforeException("Already assigned");
        ResponseEntity<ExceptionMessage> response = handler.handleTraineeHasAssignedBefore(exception);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Already assigned", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleMethodArgumentNotValid() {
        FieldError fieldError = new FieldError("object", "field", "must not be null");
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, new BindException(new Object(), "object"));
        exception.getBindingResult().addError(fieldError);

        ResponseEntity<ExceptionMessage> response = handler.handleMethodArgumentNotValid(exception);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("must not be null"));
    }


    @Test
    public void testHandleTrainerWorkloadIsUnavailable() {
        TrainerWorkloadIsUnavailableException ex = new TrainerWorkloadIsUnavailableException("Service not available");

        ResponseEntity<ExceptionMessage> response = handler.handleTrainerWorkloadIsUnavailable(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Service not available", response.getBody().getMessage());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), response.getBody().getStatus());
    }

    @Test
    public void testHandleConstraintViolation() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation);

        ConstraintViolationException exception = new ConstraintViolationException(violations);

        ResponseEntity<ExceptionMessage> response = handler.handleConstraintViolation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("must not be null"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
    }

    @Test
    void testHandleJsonParsingException() {
        Exception cause = new RuntimeException("Unrecognized date format");
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON", cause);

        ResponseEntity<ExceptionMessage> response = handler.handleJsonParsingException(exception);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("Unrecognized date format"));
    }

//    @Test
//    void testHandleGenericException() {
//        Exception exception = new Exception("Unexpected error");
//
//        ResponseEntity<ExceptionMessage> response = handler.handleException(exception);
//
//        assertEquals(500, response.getStatusCode().value());
//        assertEquals("Unexpected error", Objects.requireNonNull(response.getBody()).getMessage());
//    }
}
