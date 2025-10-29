package de.szut.lf8_starter.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectDTO {

    @NotBlank(message = "Bezeichnung darf nicht leer sein")
    @Size(min = 3, max = 100, message = "Bezeichnung muss zwischen 3 und 100 Zeichen lang sein")
    private String designation;

    @NotNull(message = "Verantwortlicher Mitarbeiter muss angegeben werden")
    @Positive(message = "Mitarbeiter-ID muss positiv sein")
    private Long responsibleEmployeeId;

    @NotNull(message = "Kunde muss angegeben werden")
    @Positive(message = "Kunden-ID muss positiv sein")
    private Long customerId;

    private String customerContactPerson;

    @Size(max = 500, message = "Kommentar darf maximal 500 Zeichen haben")
    private String comment;

    @NotNull(message = "Startdatum muss angegeben werden")
    private LocalDate startDate;

    @NotNull(message = "Geplantes Enddatum muss angegeben werden")
    private LocalDate plannedEndDate;
}