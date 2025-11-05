package de.szut.lf8_starter.exceptionHandling;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception für Employee-Service Timeout
 * HTTP Status: 504 Gateway Timeout
 */
@Getter
public class EmployeeServiceTimeoutException extends ResponseStatusException {

    public EmployeeServiceTimeoutException() {
        super(HttpStatus.GATEWAY_TIMEOUT, "Employee-Service antwortet nicht innerhalb von 3 Sekunden.");
    }

    public String getService() {
        return "employee-service";
    }
}

