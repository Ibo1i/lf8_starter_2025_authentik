package de.szut.lf8_starter.integration.employee;

import de.szut.lf8_starter.integration.employee.dto.EmployeeDto;
import de.szut.lf8_starter.integration.employee.dto.EmployeeQualificationsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client für Employee-Service
 * Base-URL: http://employee-service:8080
 */
@FeignClient(
    name = "employee-service",
    url = "${employee.service.url}",
    configuration = FeignClientConfiguration.class
)
public interface EmployeeServiceClient {

    /**
     * Holt Mitarbeiter-Details vom Employee-Service
     * @param employeeId Die Mitarbeiternummer als Long (z.B. 12345)
     * @return EmployeeDto mit Mitarbeiter-Daten
     */
    @GetMapping("/employees/{employeeId}")
    EmployeeDto getEmployee(@PathVariable("employeeId") Long employeeId);

    /**
     * Holt Qualifikationen eines Mitarbeiters vom Employee-Service
     * @param employeeId Die Mitarbeiternummer als Long (z.B. 12345)
     * @return EmployeeQualificationsResponseDto mit allen Qualifikationen
     */
    @GetMapping("/employees/{employeeId}/qualifications")
    EmployeeQualificationsResponseDto getQualifications(@PathVariable("employeeId") Long employeeId);
}

