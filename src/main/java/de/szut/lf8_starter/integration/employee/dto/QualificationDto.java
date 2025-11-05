package de.szut.lf8_starter.integration.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO für Qualifikation eines Mitarbeiters
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualificationDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("level")
    private String level;

    @JsonProperty("validUntil")
    private LocalDate validUntil;
}

