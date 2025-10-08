package de.szut.lf8_starter.project.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectCreateDto {

    @NotBlank(message = "Designation cannot be blank")
    private String designation;

    @NotNull(message = "Responsible employee ID is required")
    private Long responsibleEmployeeId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private String customerContactPerson;

    private String comment;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Planned end date is required")
    private LocalDate plannedEndDate;

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
