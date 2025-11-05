package de.szut.lf8_starter.integration.employee;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import de.szut.lf8_starter.exceptionHandling.CircuitBreakerOpenException;
import de.szut.lf8_starter.exceptionHandling.EmployeeServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests für Circuit Breaker Funktionalität
 * Testet: Öffnen nach Fehlern, Half-Open State, Schließen nach Erfolg
 */
@SpringBootTest
@WireMockTest(httpPort = 8082)
@TestPropertySource(properties = {
    "employee.service.url=http://localhost:8082",
    "resilience4j.circuitbreaker.instances.employeeService.sliding-window-size=5",
    "resilience4j.circuitbreaker.instances.employeeService.minimum-number-of-calls=3",
    "resilience4j.circuitbreaker.instances.employeeService.failure-rate-threshold=50",
    "resilience4j.circuitbreaker.instances.employeeService.wait-duration-in-open-state=2s",
    "resilience4j.circuitbreaker.instances.employeeService.permitted-number-of-calls-in-half-open-state=2",
    "resilience4j.timelimiter.instances.employeeService.timeout-duration=1s",
    "resilience4j.retry.instances.employeeService.max-attempts=1"
})
class CircuitBreakerIntegrationTest {

    @Autowired
    private EmployeeValidationService employeeValidationService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        WireMock.reset();

        // Reset Circuit Breaker to CLOSED state
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("employeeService");
        cb.transitionToClosedState();
        cb.reset();
    }

    @Test
    void circuitBreaker_OpensAfterFailures() throws InterruptedException {
        // Arrange: Mock service returns 500 errors
        stubFor(get(urlMatching("/employees/.*"))
            .willReturn(aResponse()
                .withStatus(500)));

        Long employeeId = 12345L;
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("employeeService");

        assertEquals(CircuitBreaker.State.CLOSED, cb.getState());

        // Act: Make multiple failing calls to trigger circuit breaker
        for (int i = 0; i < 5; i++) {
            try {
                employeeValidationService.validateEmployee(employeeId);
            } catch (EmployeeServiceUnavailableException | CircuitBreakerOpenException e) {
                // Expected - Circuit Breaker may open after 3 calls
            }
        }

        // Assert: Circuit Breaker should be OPEN
        assertEquals(CircuitBreaker.State.OPEN, cb.getState());

        // Next call should fail immediately with CircuitBreakerOpenException
        assertThrows(CircuitBreakerOpenException.class, () -> employeeValidationService.validateEmployee(employeeId));
    }

    @Test
    void circuitBreaker_HalfOpenStateAfterWaitDuration() throws InterruptedException {
        // Arrange: Trigger circuit breaker to open
        stubFor(get(urlMatching("/employees/.*"))
            .willReturn(aResponse()
                .withStatus(500)));

        Long employeeId = 12345L;
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("employeeService");

        // Open the circuit breaker
        for (int i = 0; i < 5; i++) {
            try {
                employeeValidationService.validateEmployee(employeeId);
            } catch (EmployeeServiceUnavailableException | CircuitBreakerOpenException e) {
                // Expected - Circuit Breaker will open after 3 calls
            }
        }

        assertEquals(CircuitBreaker.State.OPEN, cb.getState());

        // Wait for wait-duration (2 seconds)
        Thread.sleep(2500);

        // Act: Now return successful responses
        stubFor(get(urlMatching("/employees/.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "employeeId": 12345,
                        "firstName": "Max",
                        "lastName": "Mustermann",
                        "email": "max@hitec.de",
                        "status": "ACTIVE"
                    }
                    """)));

        // Try call - should transition to HALF_OPEN
        boolean result = employeeValidationService.validateEmployee(employeeId);
        assertTrue(result);

        // Make another successful call to close the circuit breaker
        // (permitted-number-of-calls-in-half-open-state: 2)
        boolean result2 = employeeValidationService.validateEmployee(employeeId);
        assertTrue(result2);

        // Assert: After 2 successful calls in HALF_OPEN, should transition to CLOSED
        Thread.sleep(500);
        assertEquals(CircuitBreaker.State.CLOSED, cb.getState());
    }

    @Test
    void circuitBreaker_ClosesAfterSuccessfulCalls() throws InterruptedException {
        // Arrange: Initially return success
        stubFor(get(urlMatching("/employees/.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "employeeId": 12345,
                        "firstName": "Max",
                        "lastName": "Mustermann",
                        "email": "max@hitec.de",
                        "status": "ACTIVE"
                    }
                    """)));

        Long employeeId = 12345L;
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("employeeService");

        // Act: Make successful calls
        for (int i = 0; i < 3; i++) {
            boolean result = employeeValidationService.validateEmployee(employeeId);
            assertTrue(result);
        }

        // Assert: Circuit Breaker should remain CLOSED
        assertEquals(CircuitBreaker.State.CLOSED, cb.getState());
    }

    @Test
    void circuitBreaker_MetricsTracking() {
        // Arrange
        stubFor(get(urlMatching("/employees/12345"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "employeeId": 12345,
                        "firstName": "Max",
                        "lastName": "Mustermann",
                        "email": "max@hitec.de",
                        "status": "ACTIVE"
                    }
                    """)));

        stubFor(get(urlMatching("/employees/99999"))
            .willReturn(aResponse()
                .withStatus(500)));

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("employeeService");

        // Act: Mix of successful and failed calls
        try {
            employeeValidationService.validateEmployee(12345L);
        } catch (Exception e) {
            // ignore
        }

        try {
            employeeValidationService.validateEmployee(99999L);
        } catch (Exception e) {
            // ignore
        }

        try {
            employeeValidationService.validateEmployee(12345L);
        } catch (Exception e) {
            // ignore
        }

        // Assert: Metrics should be tracked
        CircuitBreaker.Metrics metrics = cb.getMetrics();
        assertNotNull(metrics);
        assertTrue(metrics.getNumberOfSuccessfulCalls() > 0 || metrics.getNumberOfFailedCalls() > 0);
    }
}

