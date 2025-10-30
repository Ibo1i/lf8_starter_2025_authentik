package de.szut.lf8_starter.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ProjectSummaryDto {
    private Long id;
    private String designation;
    private LocalDate startDate;
    private LocalDate endDate;
    private String role;

    public ProjectSummaryDto() {
    }

    public ProjectSummaryDto(Long id, String designation, LocalDate startDate, LocalDate endDate, String role) {
        this.id = id;
        this.designation = designation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.role = role;
    }

}
