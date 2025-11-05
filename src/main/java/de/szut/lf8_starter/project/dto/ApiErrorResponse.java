package de.szut.lf8_starter.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<ValidationError> validationErrors;
    private List<ConflictingProjectDto> conflictingProjects;
    private ExistingAssignmentDto existingAssignment;
    // Story 4.1: Additional fields for security exceptions
    private String details;
    private List<String> requiredRoles;
    private List<String> userRoles;
    // Story 4.2: Additional fields for Employee-Service integration
    private String service;
    private Integer upstreamStatus;
    private String circuitBreakerState;
    private Integer retryAfter;

    // Constructor for backward compatibility
    public ApiErrorResponse(LocalDateTime timestamp, int status, String error, String message,
                           String path, List<ValidationError> validationErrors,
                           List<ConflictingProjectDto> conflictingProjects,
                           ExistingAssignmentDto existingAssignment) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
        this.conflictingProjects = conflictingProjects;
        this.existingAssignment = existingAssignment;
        this.details = null;
        this.requiredRoles = null;
        this.userRoles = null;
        this.service = null;
        this.upstreamStatus = null;
        this.circuitBreakerState = null;
        this.retryAfter = null;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ExistingAssignmentDto {
        private String assignedDate; // ISO date as string
        private String role;
    }
}
