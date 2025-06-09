package epam.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationEntryPointTest {

    private JwtAuthenticationEntryPoint entryPoint;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuthenticationException exception;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws Exception {
        entryPoint = new JwtAuthenticationEntryPoint();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        exception = mock(AuthenticationException.class);

        stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(printWriter);
        when(exception.getMessage()).thenReturn("Unauthorized access");
    }

    @Test
    void testCommence_ShouldSetUnauthorizedStatusAndJsonErrorMessage() throws Exception {

        entryPoint.commence(request, response, exception);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        String actualResponse = stringWriter.toString().trim();

        String expectedStatus = "\"status\": \"401 Unauthorized\"";
        String expectedError = "\"error\": \"Unauthorized access\"";

        assert actualResponse.contains(expectedStatus);
        assert actualResponse.contains(expectedError);

        String timestampRegex = "\"timestamp\":\\s*\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\"";
        assert actualResponse.matches("(?s).*" + timestampRegex + ".*") : "Timestamp format mismatch in: " + actualResponse;
    }

}
