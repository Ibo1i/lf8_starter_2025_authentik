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
 * Service für die Validierung von Mitarbeitern über den Employee-Service
 * Verwendet Circuit Breaker und Retry für Resilience
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
     * Validiert ob ein Mitarbeiter existiert und aktiv ist
     * @param employeeId Die Mitarbeiternummer als Long
     * @return true wenn Mitarbeiter existiert und aktiv ist
     * @throws EmployeeNotFoundException wenn Mitarbeiter nicht gefunden wurde
     * @throws CircuitBreakerOpenException wenn Circuit Breaker offen ist
     */
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "employeeService", fallbackMethod = "validateEmployeeFallback")
    @Retry(name = "employeeService")
    public boolean validateEmployee(Long employeeId) {
        log.debug("Validating employee with ID: {}", employeeId);

        EmployeeDto employee = employeeServiceClient.getEmployee(employeeId);

        // Prüfe ob Mitarbeiter aktiv ist
        if (!"ACTIVE".equalsIgnoreCase(employee.getStatus())) {
            log.warn("Employee {} is not active. Status: {}", employeeId, employee.getStatus());
            throw new EmployeeNotFoundException(employeeId);
        }

        log.info("Employee {} validated successfully", employeeId);
        return true;
    }

    /**
     * Validiert ob ein Mitarbeiter eine bestimmte Qualifikation besitzt
     * @param employeeId Die Mitarbeiternummer als Long
     * @param requiredQualification Die erforderliche Qualifikation
     * @return true wenn Mitarbeiter die Qualifikation besitzt und diese gültig ist
     * @throws EmployeeQualificationException wenn Qualifikation nicht vorhanden
     * @throws QualificationExpiredException wenn Qualifikation abgelaufen ist
     */
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "employeeService", fallbackMethod = "validateQualificationFallback")
    @Retry(name = "employeeService")
    public boolean validateQualification(Long employeeId, String requiredQualification) {
        log.debug("Validating qualification '{}' for employee {}", requiredQualification, employeeId);

        EmployeeQualificationsResponseDto response = employeeServiceClient.getQualifications(employeeId);

        // Prüfe ob Qualifikation vorhanden ist
        Optional<QualificationDto> qualification = response.getQualifications().stream()
            .filter(q -> q.getName().equalsIgnoreCase(requiredQualification))
            .findFirst();

        if (qualification.isEmpty()) {
            log.warn("Employee {} does not have qualification '{}'", employeeId, requiredQualification);
            throw new EmployeeQualificationException(requiredQualification);
        }

        // Prüfe ob Qualifikation abgelaufen ist
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
     * Fallback-Methode für validateEmployee bei Circuit Breaker OPEN
     */
    private boolean validateEmployeeFallback(Long employeeId, CallNotPermittedException ex) {
        log.error("Circuit Breaker is OPEN for employee validation. Employee ID: {}", employeeId);
        // Circuit Breaker ist OPEN - werfe spezifische Exception
        throw new CircuitBreakerOpenException(io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN, 60);
    }

    /**
     * Fallback-Methode für validateQualification bei Circuit Breaker OPEN
     */
    private boolean validateQualificationFallback(Long employeeId, String qualification, CallNotPermittedException ex) {
        log.error("Circuit Breaker is OPEN for qualification validation. Employee ID: {}, Qualification: {}",
                  employeeId, qualification);
        throw new CircuitBreakerOpenException(io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN, 60);
    }

    /**
     * Holt den Namen eines Mitarbeiters (für Logging/Display)
     */
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "employeeService", fallbackMethod = "getEmployeeNameFallback")
    public String getEmployeeName(Long employeeId) {
        try {
            EmployeeDto employee = employeeServiceClient.getEmployee(employeeId);
            return employee.getFirstName() + " " + employee.getLastName();
        } catch (Exception e) {
            log.debug("Could not fetch employee name for {}: {}", employeeId, e.getMessage());
            return "Mitarbeiter " + employeeId;
        }
    }

    /**
     * Fallback für getEmployeeName
     */
    private String getEmployeeNameFallback(Long employeeId, Exception ex) {
        return "Mitarbeiter " + employeeId;
    }
}

