package de.szut.lf8_starter.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> errors;  // Feld → Fehlermeldung
}