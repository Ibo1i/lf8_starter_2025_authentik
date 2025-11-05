package de.szut.lf8_starter.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmployeeNotFoundException extends ResponseStatusException {
    public EmployeeNotFoundException(Long employeeId) {
        super(HttpStatus.NOT_FOUND, "Employee with ID " + employeeId + " does not exist.");
    }
}

