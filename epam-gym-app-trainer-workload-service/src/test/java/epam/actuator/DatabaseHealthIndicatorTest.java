package epam.actuator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DatabaseHealthIndicatorTest {

    private JdbcTemplate jdbcTemplate;
    private DatabaseHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        healthIndicator = new DatabaseHealthIndicator(jdbcTemplate);
    }

    @Test
    void testHealth_Up() {
        doNothing().when(jdbcTemplate).execute("SELECT 1");

        Health health = healthIndicator.health();

        assertEquals("UP", health.getStatus().getCode());
        assertEquals("Database is healthy", health.getDetails().get("Database"));
        verify(jdbcTemplate, times(1)).execute("SELECT 1");
    }

    @Test
    void testHealth_Down() {
        doThrow(new RuntimeException("DB down")).when(jdbcTemplate).execute("SELECT 1");

        Health health = healthIndicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertEquals("Database is down", health.getDetails().get("Database"));
        verify(jdbcTemplate, times(1)).execute("SELECT 1");
    }
}
