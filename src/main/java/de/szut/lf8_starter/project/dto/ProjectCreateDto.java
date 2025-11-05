package de.szut.lf8_starter.project.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Request payload for creating a new project")
public class ProjectCreateDto {

    @NotBlank(message = "Designation cannot be blank")
    @Schema(description = "Project name or designation", example = "Cloud Migration Project Alpha")
    private String designation;

    @NotNull(message = "Responsible employee ID is required")
    @Schema(description = "Employee ID of the project manager responsible for this project", example = "1")
    private Long responsibleEmployeeId;

    @NotNull(message = "Customer ID is required")
    @Schema(description = "Unique identifier of the customer for whom the project is executed", example = "42")
    private Long customerId;

    @Schema(description = "Name of the primary contact person at the customer's organization", example = "John Miller")
    private String customerContactPerson;

    @Schema(description = "Additional notes or comments about the project", example = "Migration of legacy systems to AWS infrastructure")
    private String comment;

    @NotNull(message = "Start date is required")
    @Schema(description = "Official start date of the project", example = "2025-01-15")
    private LocalDate startDate;

    @NotNull(message = "Planned end date is required")
    @Schema(description = "Initially planned end date for project completion", example = "2025-06-30")
    private LocalDate plannedEndDate;

    @Schema(description = "Actual completion date (null if project is still ongoing)", example = "2025-06-28")
    private LocalDate actualEndDate;

    @JsonCreator
    public ProjectCreateDto(String designation, Long responsibleEmployeeId, Long customerId,
                           String customerContactPerson, String comment, LocalDate startDate,
                           LocalDate plannedEndDate, LocalDate actualEndDate) {
        this.designation = designation;
        this.responsibleEmployeeId = responsibleEmployeeId;
        this.customerId = customerId;
        this.customerContactPerson = customerContactPerson;
        this.comment = comment;
        this.startDate = startDate;
        this.plannedEndDate = plannedEndDate;
        this.actualEndDate = actualEndDate;
    }
}
