package de.szut.lf8_starter.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Project details with list of assigned employees and their qualifications")
public class ProjectEmployeesDto {

    @Schema(description = "Project identifier", example = "1001")
    private Long projectId;

    @Schema(description = "Project name", example = "Cloud Migration Project Alpha")
    private String designation;

    @Schema(description = "List of employees assigned to this project")
    private List<EmployeeWithQualificationDto> employees;

    public ProjectEmployeesDto(Long projectId, String designation, List<EmployeeWithQualificationDto> employees) {
        this.projectId = projectId;
        this.designation = designation;
        this.employees = employees;
    }

    @Getter
    @Setter
    @Schema(description = "Employee assignment details including their qualification for the project")
    public static class EmployeeWithQualificationDto {

        @Schema(description = "Employee identifier", example = "2")
        private Long employeeId;

        @Schema(description = "Required qualification/role for this project", example = "Java Developer")
        private String qualification;

        public EmployeeWithQualificationDto(Long employeeId, String qualification) {
            this.employeeId = employeeId;
            this.qualification = qualification;
        }
    }
}
