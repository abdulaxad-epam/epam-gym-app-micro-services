package epam.aop;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MetricsAspectTest {


    @Test
    public void testCounter() {
        MeterRegistry meterRegistry = mock(MeterRegistry.class);
        Counter counter = mock(Counter.class);

        lenient().when(meterRegistry.counter("event_counter")).thenReturn(counter);

        lenient().when(meterRegistry.get("event_counter")).thenReturn(null);
        try (MockedStatic<Counter> mockedStaticCounter = mockStatic(Counter.class)) {
            Counter.Builder builder = mock(Counter.Builder.class);
            when(builder.description(anyString())).thenReturn(builder);
            when(builder.register(meterRegistry)).thenReturn(counter);
            mockedStaticCounter.when(() -> Counter.builder("event_counter")).thenReturn(builder);

            MetricsAspect metricsAspect = new MetricsAspect(meterRegistry);

            metricsAspect.trackCustomEvent();

            verify(counter, times(1)).increment();
        }
    }
}
