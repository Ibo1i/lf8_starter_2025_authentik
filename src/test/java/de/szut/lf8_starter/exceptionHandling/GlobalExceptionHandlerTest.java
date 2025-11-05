package de.szut.lf8_starter.exceptionHandling;

import de.szut.lf8_starter.project.dto.ApiErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private GlobalExceptionHandler exceptionHandler;

    @Test
    @DisplayName("ResourceNotFoundException - Gibt 404 zurück")
    void handleResourceNotFound_Returns404() {
        // Given
        ResourceNotFoundException ex = new ResourceNotFoundException("Test resource not found");
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"));

        // When
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleResourceNotFound(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("Test resource not found");
    }

    @Test
    @DisplayName("EmployeeNotFoundException - Returns 404")
    void handleEmployeeNotFound_Returns404() {
        // Given
        EmployeeNotFoundException ex = new EmployeeNotFoundException(1L);
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"));

        // When
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleEmployeeNotFound(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("Employee with ID 1 does not exist.");
    }

    @Test
    @DisplayName("EmployeeQualificationException - Returns 422")
    void handleEmployeeQualification_Returns422() {
        // Given
        EmployeeQualificationException ex = new EmployeeQualificationException("Test qualification error");
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"));

        // When
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleEmployeeQualification(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getStatus()).isEqualTo(422);
        assertThat(response.getBody().getError()).isEqualTo("Unprocessable Entity");
        assertThat(response.getBody().getMessage()).isEqualTo("Employee does not have the required qualification: Test qualification error.");
    }

    @Test
    @DisplayName("TimeConflictException - Returns 409")
    void handleTimeConflict_Returns409() {
        // Given
        TimeConflictException ex = new TimeConflictException("2025-01-01", "2025-12-31", null);
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"));

        // When
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleTimeConflict(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getError()).isEqualTo("Conflict");
        assertThat(response.getBody().getMessage()).isEqualTo("Employee is already assigned to another project during the period 2025-01-01 to 2025-12-31.");
    }

    @Test
    @DisplayName("DuplicateAssignmentException - Returns 409")
    void handleDuplicateAssignment_Returns409() {
        // Given
        DuplicateAssignmentException ex = new DuplicateAssignmentException(1L, 2L, LocalDate.now(), "Developer");
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"));

        // When
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleDuplicateAssignment(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getError()).isEqualTo("Conflict");
        assertThat(response.getBody().getMessage()).isEqualTo("Employee with ID 2 is already assigned to project with ID 1.");
    }
}
