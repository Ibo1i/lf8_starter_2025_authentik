package de.szut.lf8_starter.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmployeeQualificationException extends ResponseStatusException {
    public EmployeeQualificationException(String qualification) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Employee does not have the required qualification: " + qualification + ".");
    }
}

