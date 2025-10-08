package de.szut.lf8_starter.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;

@Service
public class EmployeeService {

    private final RestTemplate restTemplate;
    private static final String EMPLOYEE_API_BASE_URL = "https://employee-api.szut.dev";

    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean employeeExists(Long employeeId) {
        try {
            String url = EMPLOYEE_API_BASE_URL + "/employees/" + employeeId;
            restTemplate.getForObject(url, Object.class);
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw new RuntimeException("Error validating employee: " + e.getMessage(), e);
        }
    }

    public boolean employeeHasQualification(Long employeeId, String qualification) {
        try {
            String url = EMPLOYEE_API_BASE_URL + "/employees/" + employeeId + "/qualifications";
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
    }
}
