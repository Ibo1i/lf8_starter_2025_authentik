package de.szut.lf8_starter.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Schema(description = "Project details including assigned employees")
public class ProjectGetDto {

    @Schema(description = "Unique project identifier", example = "1001")
    private Long id;

    @Schema(description = "Project name or designation", example = "Cloud Migration Project Alpha")
    private String designation;

    @Schema(description = "Employee ID of the responsible project manager", example = "1")
    private Long responsibleEmployeeId;

    @Schema(description = "Customer identifier", example = "42")
    private Long customerId;

    @Schema(description = "Name of the customer's contact person", example = "John Miller")
    private String customerContactPerson;

    @Schema(description = "Additional notes or comments", example = "Migration of legacy systems to AWS infrastructure")
    private String comment;

    @Schema(description = "Project start date", example = "2025-01-15")
    private LocalDate startDate;

    @Schema(description = "Planned end date", example = "2025-06-30")
    private LocalDate plannedEndDate;

    @Schema(description = "Actual completion date (null if ongoing)", example = "2025-06-28")
    private LocalDate actualEndDate;

    @Schema(description = "Set of employee IDs assigned to this project", example = "[1, 2, 5]")
    private Set<Long> employeeIds;

    public ProjectGetDto(Long id, String designation, Long responsibleEmployeeId, Long customerId,
                        String customerContactPerson, String comment, LocalDate startDate,
                        LocalDate plannedEndDate, LocalDate actualEndDate, Set<Long> employeeIds) {
        this.id = id;
        this.designation = designation;
        this.responsibleEmployeeId = responsibleEmployeeId;
        this.customerId = customerId;
        this.customerContactPerson = customerContactPerson;
        this.comment = comment;
        this.startDate = startDate;
        this.plannedEndDate = plannedEndDate;
        this.actualEndDate = actualEndDate;
        this.employeeIds = employeeIds;
    }
}
