package de.szut.lf8_starter.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Validation error for a specific field")
public class ValidationError {

    @Schema(description = "Name of the field that failed validation", example = "designation")
    private String field;

    @Schema(description = "Validation error message", example = "Designation cannot be blank")
    private String message;
}

