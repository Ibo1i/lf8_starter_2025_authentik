package de.szut.lf8_starter.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Schema(description = "Summarized project information for employee assignment listings")
public class ProjectSummaryDto {

    @Schema(description = "Project identifier", example = "1001")
    private Long id;

    @Schema(description = "Project name", example = "Cloud Migration Project Alpha")
    private String designation;

    @Schema(description = "Project start date", example = "2025-01-15")
    private LocalDate startDate;

    @Schema(description = "Project end date (planned or actual)", example = "2025-06-30")
    private LocalDate endDate;

    @Schema(description = "Employee's role/qualification in this project", example = "Java Developer")
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
