package de.szut.lf8_starter.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response after successfully assigning an employee to a project")
public class EmployeeAssignmentResponseDto {

    @Schema(description = "Project identifier", example = "1001")
    private Long projectId;

    @Schema(description = "Name of the project", example = "Cloud Migration Project Alpha")
    private String projectName;

    @Schema(description = "Assigned employee's ID", example = "2")
    private Long employeeId;

    @Schema(description = "Full name of the assigned employee", example = "Jane Doe")
    private String employeeName;

    public EmployeeAssignmentResponseDto(Long projectId, String projectName, Long employeeId, String employeeName) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }
}
