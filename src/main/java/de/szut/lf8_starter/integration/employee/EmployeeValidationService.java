package de.szut.lf8_starter.integration.employee;

import de.szut.lf8_starter.exceptionHandling.CircuitBreakerOpenException;
import de.szut.lf8_starter.exceptionHandling.EmployeeNotFoundException;
import de.szut.lf8_starter.exceptionHandling.EmployeeQualificationException;
import de.szut.lf8_starter.exceptionHandling.QualificationExpiredException;
import de.szut.lf8_starter.integration.employee.dto.EmployeeDto;
import de.szut.lf8_starter.integration.employee.dto.EmployeeQualificationsResponseDto;
import de.szut.lf8_starter.integration.employee.dto.QualificationDto;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Service for validating employees via the Employee Service
 * Uses Circuit Breaker and Retry for resilience
 */
@Service
@Slf4j
public class EmployeeValidationService {

    private final EmployeeServiceClient employeeServiceClient;

    public EmployeeValidationService(EmployeeServiceClient employeeServiceClient,
                                    CircuitBreakerRegistry circuitBreakerRegistry) {
        this.employeeServiceClient = employeeServiceClient;
    }

    /**
     * Validates if an employee exists and is active
     * @param employeeId The employee ID as Long
     * @return true if employee exists and is active
     * @throws EmployeeNotFoundException if employee was not found
     * @throws CircuitBreakerOpenException if Circuit Breaker is open
     */
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "employeeService", fallbackMethod = "validateEmployeeFallback")
    @Retry(name = "employeeService")
    public boolean validateEmployee(Long employeeId) {
        log.debug("Validating employee with ID: {}", employeeId);

        EmployeeDto employee = employeeServiceClient.getEmployee(employeeId);

        // Check if employee is active
        if (!"ACTIVE".equalsIgnoreCase(employee.getStatus())) {
            log.warn("Employee {} is not active. Status: {}", employeeId, employee.getStatus());
            throw new EmployeeNotFoundException(employeeId);
        }

        log.info("Employee {} validated successfully", employeeId);
        return true;
    }

    /**
     * Validates if an employee has a specific qualification
     * @param employeeId The employee ID as Long
     * @param requiredQualification The required qualification
     * @return true if employee has the qualification and it is valid
     * @throws EmployeeQualificationException if qualification is not present
     * @throws QualificationExpiredException if qualification is expired
     */
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "employeeService", fallbackMethod = "validateQualificationFallback")
    @Retry(name = "employeeService")
    public boolean validateQualification(Long employeeId, String requiredQualification) {
        log.debug("Validating qualification '{}' for employee {}", requiredQualification, employeeId);

        EmployeeQualificationsResponseDto response = employeeServiceClient.getQualifications(employeeId);

        // Check if qualification is present
        Optional<QualificationDto> qualification = response.getQualifications().stream()
            .filter(q -> q.getName().equalsIgnoreCase(requiredQualification))
            .findFirst();

        if (qualification.isEmpty()) {
            log.warn("Employee {} does not have qualification '{}'", employeeId, requiredQualification);
            throw new EmployeeQualificationException(requiredQualification);
        }

        // Check if qualification is expired
        QualificationDto qual = qualification.get();
        if (qual.getValidUntil() != null && qual.getValidUntil().isBefore(LocalDate.now())) {
            log.warn("Qualification '{}' of employee {} is expired (valid until: {})",
                     qual.getName(), employeeId, qual.getValidUntil());
            throw new QualificationExpiredException(qual.getName(), qual.getValidUntil());
        }

        log.info("Employee {} has valid qualification '{}'", employeeId, requiredQualification);
        return true;
    }

    /**
     * Fallback method for validateEmployee when Circuit Breaker is OPEN
     */
    private boolean validateEmployeeFallback(Long employeeId, CallNotPermittedException ex) {
        log.error("Circuit Breaker is OPEN for employee validation. Employee ID: {}", employeeId);
        // Circuit Breaker is OPEN - throw specific exception
        throw new CircuitBreakerOpenException(io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN, 60);
    }

    /**
     * Fallback method for validateQualification when Circuit Breaker is OPEN
     */
    private boolean validateQualificationFallback(Long employeeId, String qualification, CallNotPermittedException ex) {
        log.error("Circuit Breaker is OPEN for qualification validation. Employee ID: {}, Qualification: {}",
                  employeeId, qualification);
        throw new CircuitBreakerOpenException(io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN, 60);
    }

    /**
     * Gets the name of an employee (for logging/display)
     */
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "employeeService", fallbackMethod = "getEmployeeNameFallback")
    public String getEmployeeName(Long employeeId) {
        try {
            EmployeeDto employee = employeeServiceClient.getEmployee(employeeId);
            return employee.getFirstName() + " " + employee.getLastName();
        } catch (Exception e) {
            log.debug("Could not fetch employee name for {}: {}", employeeId, e.getMessage());
            return "Employee " + employeeId;
        }
    }

    /**
     * Fallback for getEmployeeName
     */
    private String getEmployeeNameFallback(Long employeeId, Exception ex) {
        return "Employee " + employeeId;
    }
}

