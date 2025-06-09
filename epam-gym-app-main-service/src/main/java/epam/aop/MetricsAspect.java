package epam.aop;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MetricsAspect {

    private final Counter eventCounter;

    public MetricsAspect(MeterRegistry meterRegistry) {
        this.eventCounter = Counter.builder("event_counter")
                .description("A counter for tracking custom events")
                .register(meterRegistry);
    }

    @Before("execution(* epam.controller.*.*(..))")
    public void trackCustomEvent() {
        eventCounter.increment();
    }
}