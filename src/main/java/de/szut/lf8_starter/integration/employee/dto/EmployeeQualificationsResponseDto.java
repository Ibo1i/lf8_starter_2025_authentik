package de.szut.lf8_starter.integration.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO für Employee-Service Qualifications Response
 * GET /employees/{employeeId}/qualifications
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeQualificationsResponseDto {

    @JsonProperty("employeeId")
    private Long employeeId;

    @JsonProperty("qualifications")
    private List<QualificationDto> qualifications;
}

