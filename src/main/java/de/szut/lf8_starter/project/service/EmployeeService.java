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

        /* Original-Code mit externer API:
        try {
            String url = "https://employee-api.szut.dev/employees/" + employeeId;
            restTemplate.getForObject(url, Object.class);
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw new RuntimeException("Error validating employee: " + e.getMessage(), e);
        }
        */
    }

    public boolean employeeHasQualification(Long employeeId, String qualification) {
        // Für Entwicklungszwecke: Nehmen wir an, dass alle Mitarbeiter alle Qualifikationen haben
        // TODO: Externe API-Validierung aktivieren wenn verfügbar
        return employeeId != null && employeeId > 0 && qualification != null && !qualification.trim().isEmpty();

        /* Original-Code mit externer API:
        try {
            String url = "https://employee-api.szut.dev/employees/" + employeeId + "/qualifications";
            // API call to check qualifications - implementation depends on actual API structure
            Object[] qualifications = restTemplate.getForObject(url, Object[].class);
            if (qualifications != null) {
                for (Object qual : qualifications) {
                    // This would need to be adjusted based on actual API response structure
                    if (qual.toString().contains(qualification)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw new RuntimeException("Error validating employee qualification: " + e.getMessage(), e);
        }
        */
    }
}
