package de.szut.lf8_starter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating an existing project")
public class UpdateProjectDTO {

    @NotBlank(message = "Designation cannot be blank")
    @Size(min = 3, max = 100, message = "Designation must be between 3 and 100 characters")
    @Schema(description = "Project designation or name", example = "Cloud Migration Project Alpha", minLength = 3, maxLength = 100)
    private String designation;

    @NotNull(message = "Responsible employee must be specified")
    @Positive(message = "Employee ID must be positive")
    @Schema(description = "Employee ID of the responsible project manager", example = "1")
    private Long responsibleEmployeeId;

    @NotNull(message = "Customer must be specified")
    @Positive(message = "Customer ID must be positive")
    @Schema(description = "Customer identifier", example = "42")
    private Long customerId;

    @Schema(description = "Primary contact person at the customer's organization", example = "John Miller")
    private String customerContactPerson;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    @Schema(description = "Additional comments or notes about the project", example = "Migration to AWS cloud infrastructure", maxLength = 500)
    private String comment;

    @NotNull(message = "Start date must be specified")
    @Schema(description = "Project start date", example = "2025-01-15")
    private LocalDate startDate;

    @NotNull(message = "Planned end date must be specified")
    @Schema(description = "Planned project end date", example = "2025-06-30")
    private LocalDate plannedEndDate;
}