package de.szut.lf8_starter.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for assigning an employee to a project")
public class EmployeeAssignmentDto {

    @NotNull(message = "Employee ID is required")
    @Schema(description = "Unique identifier of the employee to be assigned", example = "2")
    private Long employeeId;

    @NotBlank(message = "Qualification is required")
    @Schema(description = "Required qualification for the project (must match employee's qualifications)", example = "Java Developer")
    private String qualification;

    public EmployeeAssignmentDto(Long employeeId, String qualification) {
        this.employeeId = employeeId;
        this.qualification = qualification;
    }
}
