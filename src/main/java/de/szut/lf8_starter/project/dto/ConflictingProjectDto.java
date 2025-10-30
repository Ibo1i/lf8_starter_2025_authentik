package de.szut.lf8_starter.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ConflictingProjectDto {
    private Long projectId;
    private String projectName;
    private LocalDate startDate;
    private LocalDate endDate;
}

