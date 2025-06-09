package epam.security;

import epam.aop.Logging;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        if (request.getAttribute("AUTH_ERROR_HANDLED") != null || response.isCommitted()) {
            return;
        }

        request.setAttribute("AUTH_ERROR_HANDLED", true);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format("""
                {
                    "timestamp": "%s",
                    "status": "401 Unauthorized",
                    "transactionId": "%s",
                    "error": "Unauthorized access"
                }
                """, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Logging.getTransactionId()));
    }
}

