package de.szut.lf8_starter.integration.employee;

import de.szut.lf8_starter.integration.employee.dto.EmployeeDto;
import de.szut.lf8_starter.integration.employee.dto.EmployeeQualificationsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for Employee Service
 * Base URL: http://employee-service:8080
 */
@FeignClient(
    name = "employee-service",
    url = "${employee.service.url}",
    configuration = FeignClientConfiguration.class
)
public interface EmployeeServiceClient {

    /**
     * Fetches employee details from the Employee Service
     * @param employeeId The employee ID as Long (e.g., 12345)
     * @return EmployeeDto with employee data
     */
    @GetMapping("/employees/{employeeId}")
    EmployeeDto getEmployee(@PathVariable("employeeId") Long employeeId);

    /**
     * Fetches qualifications of an employee from the Employee Service
     * @param employeeId The employee ID as Long (e.g., 12345)
     * @return EmployeeQualificationsResponseDto with all qualifications
     */
    @GetMapping("/employees/{employeeId}/qualifications")
    EmployeeQualificationsResponseDto getQualifications(@PathVariable("employeeId") Long employeeId);
}

