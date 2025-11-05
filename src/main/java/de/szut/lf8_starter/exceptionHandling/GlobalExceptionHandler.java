package de.szut.lf8_starter.exceptionHandling;

import de.szut.lf8_starter.project.dto.ApiErrorResponse;
import de.szut.lf8_starter.project.dto.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""),
            null,
            null,
            null
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEmployeeNotFound(EmployeeNotFoundException ex, WebRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getReason(),
            request.getDescription(false).replace("uri=", ""),
            null,
            null,
            null
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmployeeQualificationException.class)
    public ResponseEntity<ApiErrorResponse> handleEmployeeQualification(EmployeeQualificationException ex, WebRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
            ex.getReason(),
            request.getDescription(false).replace("uri=", ""),
            null,
            null,
            null
        );
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(QualificationExpiredException.class)
    public ResponseEntity<ApiErrorResponse> handleQualificationExpired(QualificationExpiredException ex, WebRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
            ex.getReason(),
            request.getDescription(false).replace("uri=", ""),
            null,
            null,
            null
        );
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(EmployeeServiceTimeoutException.class)
    public ResponseEntity<ApiErrorResponse> handleEmployeeServiceTimeout(EmployeeServiceTimeoutException ex, WebRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.GATEWAY_TIMEOUT.value(),
            HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase(),
            ex.getReason(),
            request.getDescription(false).replace("uri=", ""),
            null,
            null,
            null
        );
        body.setService(ex.getService());
        return new ResponseEntity<>(body, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(EmployeeServiceUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleEmployeeServiceUnavailable(EmployeeServiceUnavailableException ex, WebRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_GATEWAY.value(),
            HttpStatus.BAD_GATEWAY.getReasonPhrase(),
            ex.getReason(),
            request.getDescription(false).replace("uri=", ""),
            null,
            null,
            null
        );
        body.setService(ex.getService());
        body.setUpstreamStatus(ex.getUpstreamStatus());
        return new ResponseEntity<>(body, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(CircuitBreakerOpenException.class)
    public ResponseEntity<ApiErrorResponse> handleCircuitBreakerOpen(CircuitBreakerOpenException ex, WebRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
            ex.getReason(),
            request.getDescription(false).replace("uri=", ""),
            null,
            null,
            null
        );
        body.setCircuitBreakerState(ex.getCircuitBreakerState());
        body.setRetryAfter(ex.getRetryAfter());
        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(TimeConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleTimeConflict(TimeConflictException ex, WebRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            ex.getReason(),
            request.getDescription(false).replace("uri=", ""),
            null,
            ex.getConflictingProjects(),
            null
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateAssignmentException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateAssignment(DuplicateAssignmentException ex, WebRequest request) {
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
        String assignedDate = ex.getAssignedDate() != null ? ex.getAssignedDate().format(df) : null;
        ApiErrorResponse.ExistingAssignmentDto existing = new ApiErrorResponse.ExistingAssignmentDto(assignedDate, ex.getRole());

        ApiErrorResponse body = new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""),
            null,
            null,
            existing
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        // Mappen von DB-Unique-Constraint-Verletzungen (z.B. parallele Anfragen) auf 409
        ApiErrorResponse body = new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            "Mitarbeiter ist bereits dem Projekt zugewiesen.",
            request.getDescription(false).replace("uri=", ""),
            null,
            null,
            null
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<ValidationError> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
            errors.add(new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
        );

        ApiErrorResponse body = new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Pflichtfeld fehlt oder ist ungültig.",
            request.getDescription(false).replace("uri=", ""),
            errors,
            null,
            null
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        // z.B. wenn PathVariable Long nicht parsebar ist
        ApiErrorResponse body = new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Mitarbeiternummer hat ein ungültiges Format.",
            request.getDescription(false).replace("uri=", ""),
            null,
            null,
            null
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(org.springframework.security.core.AuthenticationException ex, WebRequest request) {
        ApiErrorResponse body = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .message("JWT-Token ist ungültig oder abgelaufen.")
            .path(request.getDescription(false).replace("uri=", ""))
            .details(ex.getMessage())
            .build();
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        ApiErrorResponse body = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.FORBIDDEN.value())
            .error(HttpStatus.FORBIDDEN.getReasonPhrase())
            .message("Unzureichende Berechtigungen. Erforderliche Rolle: hitec-employee")
            .path(request.getDescription(false).replace("uri=", ""))
            .requiredRoles(List.of("hitec-employee"))
            .userRoles(List.of())
            .build();
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException ex, WebRequest request) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        ApiErrorResponse body = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(ex.getReason())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ApiErrorResponse body = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
