package de.szut.lf8_starter.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class ProjectGetDto {

    private Long id;
    private String designation;
    private Long responsibleEmployeeId;
    private Long customerId;
    private String customerContactPerson;
    private String comment;
    private LocalDate startDate;
    private LocalDate plannedEndDate;
    private LocalDate actualEndDate;
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
