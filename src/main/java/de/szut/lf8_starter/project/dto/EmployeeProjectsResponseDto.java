package de.szut.lf8_starter.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Schema(description = "List of all projects an employee is assigned to")
public class EmployeeProjectsResponseDto {

    @Schema(description = "Employee identifier", example = "2")
    private Long employeeId;

    @Schema(description = "List of projects the employee is working on")
    private List<ProjectSummaryDto> projects;

    public EmployeeProjectsResponseDto() {
    }

    public EmployeeProjectsResponseDto(Long employeeId, List<ProjectSummaryDto> projects) {
        this.employeeId = employeeId;
        this.projects = projects;
    }

}
