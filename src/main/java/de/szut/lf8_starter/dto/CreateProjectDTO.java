package de.szut.lf8_starter.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectDTO {

    @NotBlank(message = "Designation cannot be blank")
    @Size(min = 3, max = 100, message = "Designation must be between 3 and 100 characters")
    private String designation;

    @NotNull(message = "Responsible employee must be specified")
    @Positive(message = "Employee ID must be positive")
    private Long responsibleEmployeeId;

    @NotNull(message = "Customer must be specified")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    @NotBlank(message = "Contact person must be specified")
    private String customerContactPerson;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;

    @NotNull(message = "Start date must be specified")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "Planned end date must be specified")
    @Future(message = "End date must be in the future")
    private LocalDate plannedEndDate;
}