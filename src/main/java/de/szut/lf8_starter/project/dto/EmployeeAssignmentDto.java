package de.szut.lf8_starter.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeAssignmentDto {
    private Long employeeId;
    private String qualification;

    public EmployeeAssignmentDto(Long employeeId, String qualification) {
        this.employeeId = employeeId;
        this.qualification = qualification;
    }
}
