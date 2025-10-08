package de.szut.lf8_starter.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class EmployeeProjectsDto {
    private Long employeeId;
    private List<ProjectWithRoleDto> projects;

    public EmployeeProjectsDto(Long employeeId, List<ProjectWithRoleDto> projects) {
        this.employeeId = employeeId;
        this.projects = projects;
    }

    @Getter
    @Setter
    public static class ProjectWithRoleDto {
        private Long projectId;
        private String designation;
        private LocalDate startDate;
        private LocalDate endDate;
        private String qualification;

        public ProjectWithRoleDto(Long projectId, String designation, LocalDate startDate,
                                 LocalDate endDate, String qualification) {
            this.projectId = projectId;
            this.designation = designation;
            this.startDate = startDate;
            this.endDate = endDate;
            this.qualification = qualification;
        }
    }
}
