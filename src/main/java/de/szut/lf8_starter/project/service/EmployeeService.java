package de.szut.lf8_starter.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmployeeService {

    private final RestTemplate restTemplate;

    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean employeeExists(Long employeeId) {
        // Für Entwicklungszwecke: Einfache Validierung ohne externe API-Aufrufe
        // TODO: Externe API-Validierung aktivieren wenn verfügbar
        return employeeId != null && employeeId > 0;
    }

    public boolean employeeHasQualification(Long employeeId, String qualification) {
        // Für Entwicklungszwecke: Nehmen wir an, dass alle Mitarbeiter alle Qualifikationen haben
        // TODO: Externe API-Validierung aktivieren wenn verfügbar
        return employeeId != null && employeeId > 0 && qualification != null && !qualification.trim().isEmpty();
    }

    public String getEmployeeName(Long employeeId) {
        if (employeeId == null) return "";
        return "Mitarbeiter " + employeeId;
    }
}
