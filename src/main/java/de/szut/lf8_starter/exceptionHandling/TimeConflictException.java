package de.szut.lf8_starter.exceptionHandling;

import de.szut.lf8_starter.project.dto.ConflictingProjectDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Getter
public class TimeConflictException extends ResponseStatusException {
    private final List<ConflictingProjectDto> conflictingProjects;

    public TimeConflictException(String startDate, String endDate, List<ConflictingProjectDto> conflictingProjects) {
        super(HttpStatus.CONFLICT, "Mitarbeiter ist im Zeitraum " + startDate + " bis " + endDate + " bereits verplant.");
        this.conflictingProjects = conflictingProjects;
    }

}
