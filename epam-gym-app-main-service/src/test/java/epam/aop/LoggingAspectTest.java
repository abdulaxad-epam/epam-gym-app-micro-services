package epam.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @Mock
    private Logging mockLogger;

    @Mock
    private Signature mockSignature;

    @Mock
    private JoinPoint mockJoinPoint;

    private LoggingAspect loggingAspect;

    @BeforeEach
    void setUp() throws Exception {
        loggingAspect = new LoggingAspect();

        // Inject mock logger using reflection
        java.lang.reflect.Field loggerField = LoggingAspect.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(loggingAspect, mockLogger);

        when(mockJoinPoint.getSignature()).thenReturn(mockSignature);
    }

    @Test
    void testLogRequest() {
        when(mockJoinPoint.getSignature().getDeclaringTypeName()).thenReturn("epam.controller.TestController");
        when(mockJoinPoint.getSignature().getName()).thenReturn("testMethod");
        when(mockJoinPoint.getArgs()).thenReturn(new Object[]{"arg1", 42});

        loggingAspect.logRequest(mockJoinPoint);

        verify(mockLogger).info(anyString(), any(), any(), any());
    }

    @Test
    void testLogServiceMethod() {
        when(mockJoinPoint.getSignature().getDeclaringTypeName()).thenReturn("epam.service.TestService");
        when(mockJoinPoint.getSignature().getName()).thenReturn("serviceMethod");
        when(mockJoinPoint.getArgs()).thenReturn(new Object[]{"arg1"});

        loggingAspect.logServiceMethod(mockJoinPoint);

        verify(mockLogger).info(anyString(), any(), any(), any());
    }

    @Test
    void testLogRepositoryMethod() {
        when(mockJoinPoint.getSignature().getDeclaringTypeName()).thenReturn("epam.repository.TestRepository");
        when(mockJoinPoint.getSignature().getName()).thenReturn("repositoryMethod");
        when(mockJoinPoint.getArgs()).thenReturn(new Object[]{"arg1"});

        loggingAspect.logRepositoryMethod(mockJoinPoint);

        verify(mockLogger).info(anyString(), any(), any(), any());
    }

    @Test
    void testLogAfterSuccess() {
        when(mockJoinPoint.getSignature().getDeclaringTypeName()).thenReturn("epam.controller.TestController");
        when(mockJoinPoint.getSignature().getName()).thenReturn("successfulMethod");

        loggingAspect.logAfterSuccess(mockJoinPoint, "Success Response");

        verify(mockLogger).info(eq("Executed REST: {0}.{1}(), Response: {2}, Status: 200"),
                eq("epam.controller.TestController"),
                eq("successfulMethod"),
                eq("Success Response"));
    }

    @Test
    void testLogExceptions() {
        when(mockJoinPoint.getSignature().getDeclaringTypeName()).thenReturn("epam.service.TestService");
        when(mockJoinPoint.getSignature().getName()).thenReturn("exceptionMethod");
        Throwable exception = new RuntimeException("Test Exception");

        loggingAspect.logExceptions(mockJoinPoint, exception);

        verify(mockLogger).error(anyString(), any(), any(), eq("Test Exception"));
    }
}
