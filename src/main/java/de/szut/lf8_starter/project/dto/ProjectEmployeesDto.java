package de.szut.lf8_starter.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectEmployeesDto {
    private Long projectId;
    private String designation;
    private List<EmployeeWithQualificationDto> employees;

    public ProjectEmployeesDto(Long projectId, String designation, List<EmployeeWithQualificationDto> employees) {
        this.projectId = projectId;
        this.designation = designation;
        this.employees = employees;
    }

    @Getter
    @Setter
    public static class EmployeeWithQualificationDto {
        private Long employeeId;
        private String qualification;

        public EmployeeWithQualificationDto(Long employeeId, String qualification) {
            this.employeeId = employeeId;
            this.qualification = qualification;
        }
    }
}
