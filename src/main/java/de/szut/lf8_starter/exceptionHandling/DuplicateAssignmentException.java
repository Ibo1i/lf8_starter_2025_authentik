package de.szut.lf8_starter.exceptionHandling;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public class DuplicateAssignmentException extends RuntimeException {
    private final Long projectId;
    private final Long employeeId;
    private final LocalDate assignedDate;
    private final String role;

    public DuplicateAssignmentException(Long projectId, Long employeeId, LocalDate assignedDate, String role) {
        super("Employee with ID " + employeeId + " is already assigned to project with ID " + projectId + ".");
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.assignedDate = assignedDate;
        this.role = role;
    }
}

