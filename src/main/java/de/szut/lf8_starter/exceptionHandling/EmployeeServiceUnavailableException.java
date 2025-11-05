package de.szut.lf8_starter.exceptionHandling;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception für Employee-Service Fehler (5xx)
 * HTTP Status: 502 Bad Gateway
 */
@Getter
public class EmployeeServiceUnavailableException extends ResponseStatusException {

    private final Integer upstreamStatus;

    public EmployeeServiceUnavailableException(Integer upstreamStatus) {
        super(HttpStatus.BAD_GATEWAY, "Employee-Service ist temporär nicht verfügbar.");
        this.upstreamStatus = upstreamStatus;
    }

    public String getService() {
        return "employee-service";
    }

}

