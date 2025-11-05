package de.szut.lf8_starter.integration.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO für Employee-Service Response
 * GET /employees/{employeeId}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    @JsonProperty("employeeId")
    private Long employeeId;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("status")
    private String status;
}

