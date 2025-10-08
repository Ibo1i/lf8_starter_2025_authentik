package de.szut.lf8_starter.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {
    private Long id;
    private String designation;
    private Long responsibleEmployeeId;
    private Long customerId;
    private String customerContactPerson;
    private String comment;
    private LocalDate startDate;
    private LocalDate plannedEndDate;
}