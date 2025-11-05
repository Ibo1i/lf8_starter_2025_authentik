package de.szut.lf8_starter.project.service;

import de.szut.lf8_starter.integration.employee.EmployeeValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service für Mitarbeiter-Validierung
 * Verwendet den EmployeeValidationService für externe API-Aufrufe
 */
@Service
@Slf4j
public class EmployeeService {

    private final EmployeeValidationService employeeValidationService;

    public EmployeeService(EmployeeValidationService employeeValidationService) {
        this.employeeValidationService = employeeValidationService;
    }

    /**
     * Prüft ob ein Mitarbeiter existiert und aktiv ist
     * @param employeeId Die Mitarbeiternummer
     * @return true wenn Mitarbeiter existiert und aktiv ist
     */
    public boolean employeeExists(Long employeeId) {
        if (employeeId == null || employeeId <= 0) {
            log.warn("Invalid employee ID: {}", employeeId);
            return false;
        }

        try {
            return employeeValidationService.validateEmployee(employeeId);
        } catch (Exception e) {
            log.error("Error validating employee {}: {}", employeeId, e.getMessage());
            throw e;
        }
    }

    /**
     * Prüft ob ein Mitarbeiter eine bestimmte Qualifikation besitzt
     * @param employeeId Die Mitarbeiternummer
     * @param qualification Die erforderliche Qualifikation
     * @return true wenn Mitarbeiter die Qualifikation besitzt
     */
    public boolean employeeHasQualification(Long employeeId, String qualification) {
        if (employeeId == null || employeeId <= 0) {
            log.warn("Invalid employee ID: {}", employeeId);
            return false;
        }

        if (qualification == null || qualification.trim().isEmpty()) {
            log.warn("Invalid qualification: {}", qualification);
            return false;
        }

        try {
            return employeeValidationService.validateQualification(employeeId, qualification);
        } catch (Exception e) {
            log.error("Error validating qualification '{}' for employee {}: {}",
                     qualification, employeeId, e.getMessage());
            throw e;
        }
    }

    /**
     * Holt den Namen eines Mitarbeiters
     * @param employeeId Die Mitarbeiternummer
     * @return Der Name des Mitarbeiters oder "Mitarbeiter {ID}" als Fallback
     */
    public String getEmployeeName(Long employeeId) {
        if (employeeId == null) {
            return "";
        }

        try {
            return employeeValidationService.getEmployeeName(employeeId);
        } catch (Exception e) {
            log.debug("Could not fetch employee name for {}: {}", employeeId, e.getMessage());
            return "Mitarbeiter " + employeeId;
        }
    }
}
