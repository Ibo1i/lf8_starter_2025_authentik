package de.szut.lf8_starter.project.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RemoveEmployeeResponseDto {
    private String message;
    private Long projectId;
    private Long employeeId;

    public RemoveEmployeeResponseDto() {
    }

    public RemoveEmployeeResponseDto(String message, Long projectId, Long employeeId) {
        this.message = message;
        this.projectId = projectId;
        this.employeeId = employeeId;
    }

}
