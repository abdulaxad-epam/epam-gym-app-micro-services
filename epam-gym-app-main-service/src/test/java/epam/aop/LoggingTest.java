package epam.aop;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoggingTest {

    @Mock
    private Logger mockLogger;

    private Logging logging;

    @BeforeEach
    void setUp() throws Exception {
        logging = new Logging(LoggingTest.class);

        Field loggerField = Logging.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(logging, mockLogger);
    }

    @Test
    void testGetTransactionId() {
        String transactionId1 = Logging.getTransactionId();
        String transactionId2 = Logging.getTransactionId();
        assertNotNull(transactionId1);
        assertEquals(transactionId1, transactionId2, "Transaction ID should be consistent within the same thread");
    }

    @Test
    void testResetTransactionId() {
        String oldTransactionId = Logging.getTransactionId();
        logging.resetTransactionId();
        String newTransactionId = Logging.getTransactionId();
        assertNotNull(newTransactionId);
        assertNotEquals(oldTransactionId, newTransactionId, "Transaction ID should change after reset");
    }

    @Test
    void testInfoLogging() {
        testLoggingMethod("info", "Test message {}", "arg1");
    }

    @Test
    void testErrorLogging() {
        testLoggingMethod("error", "Error occurred {}", "arg1");
    }

    @Test
    void testDebugLogging() {
        testLoggingMethod("debug", "Debugging {}", "arg1");
    }

    @Test
    void testWarnLogging() {
        testLoggingMethod("warn", "Warning {}", "arg1");
    }

    private void testLoggingMethod(String logLevel, String message, Object... args) {

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<Object[]> argsCaptor = ArgumentCaptor.forClass(Object[].class);

        switch (logLevel) {
            case "info":
                logging.info(message, args);
                verify(mockLogger).infov(messageCaptor.capture(), argsCaptor.capture());
                break;
            case "error":
                logging.error(message, args);
                verify(mockLogger).errorv(messageCaptor.capture(), argsCaptor.capture());
                break;
            case "debug":
                logging.debug(message, args);
                verify(mockLogger).debugv(messageCaptor.capture(), argsCaptor.capture());
                break;
            case "warn":
                logging.warn(message, args);
                verify(mockLogger).warnv(messageCaptor.capture(), argsCaptor.capture());
                break;
            default:
                fail("Invalid log level: " + logLevel);
        }

        String capturedMessage = messageCaptor.getValue();
        Object[] capturedArgs = argsCaptor.getValue();

        assertTrue(capturedMessage.startsWith("TransactionID: {0} - "), "Message format is incorrect");
        assertEquals(args.length + 1, capturedArgs.length, "Argument count mismatch");
        assertNotNull(capturedArgs[0], "Transaction ID should not be null");
        for (int i = 0; i < args.length; i++) {
            assertEquals(args[i], capturedArgs[i + 1], "Argument mismatch at index " + i);
        }
    }
}
