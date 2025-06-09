package epam.exception;

import epam.exception.exception_handler.ExceptionMessage;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExceptionMessageTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        ExceptionMessage exceptionMassage = new ExceptionMessage();
        exceptionMassage.setStatus(404);
        exceptionMassage.setError("Not Found");
        exceptionMassage.setMessage("The requested resource was not found");

        assertEquals(404, exceptionMassage.getStatus());
        assertEquals("Not Found", exceptionMassage.getError());
        assertEquals("The requested resource was not found", exceptionMassage.getMessage());
        assertNotNull(exceptionMassage.getTimestamp()); // Should be auto-set
    }

    @Test
    void testAllArgsConstructor() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ExceptionMessage exceptionMassage = new ExceptionMessage(timestamp, 500, "Internal Server Error", "Server Error", "2681-5360-4b");

        assertEquals(500, exceptionMassage.getStatus());
        assertEquals("Internal Server Error", exceptionMassage.getError());
        assertEquals("Server Error", exceptionMassage.getMessage());
        assertEquals("2681-5360-4b", exceptionMassage.getRequestId());
        assertEquals(timestamp, exceptionMassage.getTimestamp());
    }

    @Test
    void testBuilder() {
        ExceptionMessage exceptionMassage = ExceptionMessage.builder()
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .build();

        assertEquals(400, exceptionMassage.getStatus());
        assertEquals("Bad Request", exceptionMassage.getError());
        assertEquals("Invalid input", exceptionMassage.getMessage());
        assertNotNull(exceptionMassage.getTimestamp());
    }

    @Test
    void testToString() {
        ExceptionMessage exceptionMassage = new ExceptionMessage();
        exceptionMassage.setStatus(403);
        exceptionMassage.setError("Forbidden");
        exceptionMassage.setMessage("You don't have permission to access this resource");
        exceptionMassage.setRequestId("2681-5360-4b");
        String expected = "ExceptionMessage(timestamp=" + exceptionMassage.getTimestamp() +
                ", status=403, error=Forbidden, message=You don't have permission to access this resource, requestId=2681-5360-4b)";

        assertEquals(expected, exceptionMassage.toString());
    }
}
