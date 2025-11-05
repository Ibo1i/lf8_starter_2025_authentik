package de.szut.lf8_starter.exceptionHandling;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception für geöffneten Circuit Breaker
 * HTTP Status: 503 Service Unavailable
 */
@Getter
public class CircuitBreakerOpenException extends ResponseStatusException {

    private final String circuitBreakerState;
    private final Integer retryAfter;

    public CircuitBreakerOpenException(CircuitBreaker.State state, Integer retryAfter) {
        super(HttpStatus.SERVICE_UNAVAILABLE,
            "Employee-Service ist derzeit nicht verfügbar. Bitte später erneut versuchen.");
        this.circuitBreakerState = state.name();
        this.retryAfter = retryAfter;
    }

    public CircuitBreakerOpenException() {
        this(CircuitBreaker.State.OPEN, 60);
    }

    public String getService() {
        String service = "employee-service";
        return service;
    }
}

