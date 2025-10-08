package de.szut.lf8_starter.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeAssignmentResponseDto {
    private Long projectId;
    private String projectName;
    private Long employeeId;
    private String employeeName;

    public EmployeeAssignmentResponseDto(Long projectId, String projectName, Long employeeId, String employeeName) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }
}
