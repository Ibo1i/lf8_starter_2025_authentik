package de.szut.lf8_starter.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Details of a project that conflicts with the requested time period")
public class ConflictingProjectDto {

    @Schema(description = "ID of the conflicting project", example = "1002")
    private Long projectId;

    @Schema(description = "Name of the conflicting project", example = "Database Optimization Project")
    private String projectName;

    @Schema(description = "Start date of the conflicting project", example = "2025-02-01")
    private LocalDate startDate;

    @Schema(description = "End date of the conflicting project", example = "2025-05-31")
    private LocalDate endDate;
}

