package de.szut.lf8_starter.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Standardized error response with detailed context information")
public class ApiErrorResponse {

    @Schema(description = "Timestamp when the error occurred", example = "2025-01-15T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "409")
    private int status;

    @Schema(description = "HTTP status reason phrase", example = "Conflict")
    private String error;

    @Schema(description = "Human-readable error message", example = "Employee is already assigned to another project during this time period")
    private String message;

    @Schema(description = "Request path that caused the error", example = "/projects/1001/employees")
    private String path;

    @Schema(description = "List of validation errors (only for 400 Bad Request)")
    private List<ValidationError> validationErrors;

    @Schema(description = "List of conflicting projects (only for 409 time conflicts)")
    private List<ConflictingProjectDto> conflictingProjects;

    @Schema(description = "Existing assignment details (only for duplicate assignment errors)")
    private ExistingAssignmentDto existingAssignment;

    @Schema(description = "Additional error details")
    private String details;

    @Schema(description = "Required roles for access (only for 403 Forbidden)")
    private List<String> requiredRoles;

    @Schema(description = "User's current roles (only for 403 Forbidden)")
    private List<String> userRoles;

    @Schema(description = "Name of the external service that failed (only for 502/503/504)", example = "Employee Service")
    private String service;

    @Schema(description = "HTTP status code from the upstream service (only for 502)", example = "500")
    private Integer upstreamStatus;

    @Schema(description = "Current circuit breaker state (only for 503)", example = "OPEN")
    private String circuitBreakerState;

    @Schema(description = "Seconds until retry is possible (only for 503)", example = "60")
    private Integer retryAfter;

    // ...existing code...

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
