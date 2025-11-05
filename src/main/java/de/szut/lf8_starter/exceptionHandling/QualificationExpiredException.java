package de.szut.lf8_starter.exceptionHandling;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Exception für abgelaufene Qualifikationen
 * HTTP Status: 422 Unprocessable Entity
 */
@Getter
public class QualificationExpiredException extends ResponseStatusException {
    
    private final String qualificationName;
    private final LocalDate validUntil;
    
    public QualificationExpiredException(String qualificationName, LocalDate validUntil) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, 
            String.format("Qualifikation %s ist abgelaufen (gültig bis %s).", 
                qualificationName, 
                validUntil.format(DateTimeFormatter.ISO_LOCAL_DATE)));
        this.qualificationName = qualificationName;
        this.validUntil = validUntil;
    }

}

