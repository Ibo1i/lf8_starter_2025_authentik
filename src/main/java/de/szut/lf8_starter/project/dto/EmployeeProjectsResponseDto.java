package de.szut.lf8_starter.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class EmployeeProjectsResponseDto {
    private Long employeeId;
    private List<ProjectSummaryDto> projects;

    public EmployeeProjectsResponseDto() {
    }

    public EmployeeProjectsResponseDto(Long employeeId, List<ProjectSummaryDto> projects) {
        this.employeeId = employeeId;
        this.projects = projects;
    }

}
