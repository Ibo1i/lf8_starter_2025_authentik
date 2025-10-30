package de.szut.lf8_starter.project;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.exceptionHandling.EmployeeNotFoundException;
import de.szut.lf8_starter.exceptionHandling.EmployeeQualificationException;
import de.szut.lf8_starter.exceptionHandling.TimeConflictException;
import de.szut.lf8_starter.project.service.EmployeeService;
import de.szut.lf8_starter.project.service.CustomerService;
import de.szut.lf8_starter.project.dto.ProjectEmployeesDto;
import de.szut.lf8_starter.project.dto.EmployeeProjectsDto;
import de.szut.lf8_starter.project.dto.ConflictingProjectDto;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private final ProjectRepository repository;
    private final EmployeeService employeeService;
    private final CustomerService customerService;

    public ProjectService(ProjectRepository repository, EmployeeService employeeService,
                         CustomerService customerService) {
        this.repository = repository;
        this.employeeService = employeeService;
        this.customerService = customerService;
    }

    public ProjectEntity create(ProjectEntity entity) {
        // Validierung vor dem Erstellen
        validateProject(entity);
        return this.repository.save(entity);
    }

    public List<ProjectEntity> readAll() {
        return this.repository.findAll();
    }

    public ProjectEntity readById(Long id) {
        Optional<ProjectEntity> optionalProject = this.repository.findById(id);
        if (optionalProject.isEmpty()) {
            throw new ResourceNotFoundException("Projekt mit der ID " + id + " existiert nicht.");
        }
        return optionalProject.get();
    }

    public ProjectEntity update(ProjectEntity entity) {
        // Prüfen ob Projekt existiert
        readById(entity.getId());
        // Validierung vor dem Update
        validateProject(entity);
        return this.repository.save(entity);
    }

    public void delete(ProjectEntity entity) {
        this.repository.delete(entity);
    }

    public void deleteById(Long id) {
        ProjectEntity project = readById(id);

        // Abhängigkeiten prüfen: dürfen keine Mitarbeiter zugeordnet sein
        if (project.getEmployeeIds() != null && !project.getEmployeeIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Projekt hat noch Mitarbeiterzuordnungen und kann nicht gelöscht werden.");
        }

        this.repository.delete(project);
    }

    public List<ProjectEntity> findByResponsibleEmployeeId(Long employeeId) {
        return this.repository.findByResponsibleEmployeeId(employeeId);
    }

    public List<ProjectEntity> findProjectsByEmployeeId(Long employeeId) {
        return this.repository.findProjectsByEmployeeId(employeeId);
    }

    public List<ProjectEntity> findProjectsInTimeRange(LocalDate startDate, LocalDate endDate) {
        return this.repository.findProjectsInTimeRange(startDate, endDate);
    }

    /**
     * Fügt einen Mitarbeiter mit einer bestimmten Qualifikation zu einem Projekt hinzu
     */
    public ProjectEntity addEmployeeToProject(Long projectId, Long employeeId, String qualification) {
        ProjectEntity project = readById(projectId);

        // Validierung: Mitarbeiter existiert
        if (!employeeService.employeeExists(employeeId)) {
            throw new EmployeeNotFoundException(employeeId);
        }

        // Validierung: Mitarbeiter hat die erforderliche Qualifikation
        if (!employeeService.employeeHasQualification(employeeId, qualification)) {
            throw new EmployeeQualificationException(qualification);
        }

        // Validierung: Zeitkonflikt prüfen
        List<ProjectEntity> conflicting = repository.findProjectsInTimeRange(project.getStartDate(), project.getPlannedEndDate())
            .stream()
            .filter(p -> !p.getId().equals(projectId) && p.getEmployeeIds().contains(employeeId))
            .collect(Collectors.toList());

        if (!conflicting.isEmpty()) {
            DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
            List<ConflictingProjectDto> conflictingDtos = conflicting.stream()
                .map(p -> new ConflictingProjectDto(p.getId(), p.getDesignation(), p.getStartDate(), p.getActualEndDate() != null ? p.getActualEndDate() : p.getPlannedEndDate()))
                .collect(Collectors.toList());

            String firstStart = conflictingDtos.get(0).getStartDate().format(df).toString();
            String firstEnd = conflictingDtos.get(0).getEndDate().format(df).toString();
            throw new TimeConflictException(firstStart, firstEnd, conflictingDtos);
        }

        // Mitarbeiter hinzufügen
        if (project.getEmployeeIds() == null) {
            project.setEmployeeIds(new java.util.HashSet<>());
        }
        if (project.getEmployeeQualifications() == null) {
            project.setEmployeeQualifications(new java.util.HashMap<>());
        }

        project.getEmployeeIds().add(employeeId);
        project.getEmployeeQualifications().put(employeeId, qualification);

        return this.repository.save(project);
    }

    /**
     * Entfernt einen Mitarbeiter aus einem Projekt
     */
    public ProjectEntity removeEmployeeFromProject(Long projectId, Long employeeId) {
        ProjectEntity project = readById(projectId);

        // Prüfen ob Mitarbeiter im Projekt arbeitet
        if (!project.getEmployeeIds().contains(employeeId)) {
            throw new ResourceNotFoundException("Mitarbeiter mit der Mitarbeiternummer (\"" + employeeId +
                "\") arbeitet in dem angegebenen Projekt mit der Projekt-ID (" + projectId + ") nicht");
        }

        // Mitarbeiter entfernen
        project.getEmployeeIds().remove(employeeId);
        project.getEmployeeQualifications().remove(employeeId);

        return this.repository.save(project);
    }

    /**
     * Gibt alle Mitarbeiter eines Projekts mit ihren Qualifikationen zurück
     */
    public ProjectEmployeesDto getProjectEmployees(Long projectId) {
        ProjectEntity project = readById(projectId);
        return mapToProjectEmployeesDto(project);
    }

    /**
     * Gibt alle Projekte eines Mitarbeiters zurück
     */
    public EmployeeProjectsDto getEmployeeProjects(Long employeeId) {
        // Validierung: Mitarbeiter existiert
        if (!employeeService.employeeExists(employeeId)) {
            throw new ResourceNotFoundException("Mitarbeiter mit der Mitarbeiternummer existiert nicht");
        }

        List<ProjectEntity> projects = findProjectsByEmployeeId(employeeId);
        return mapToEmployeeProjectsDto(employeeId, projects);
    }

    /**
     * Hilfsmethode für Mapper-Zugriff
     */
    private ProjectEmployeesDto mapToProjectEmployeesDto(ProjectEntity entity) {
        List<ProjectEmployeesDto.EmployeeWithQualificationDto> employees =
            entity.getEmployeeIds().stream()
                .map(employeeId -> new ProjectEmployeesDto.EmployeeWithQualificationDto(
                    employeeId,
                    entity.getEmployeeQualifications().get(employeeId)
                ))
                .collect(Collectors.toList());

        return new ProjectEmployeesDto(entity.getId(), entity.getDesignation(), employees);
    }

    /**
     * Hilfsmethode für Mapper-Zugriff
     */
    private EmployeeProjectsDto mapToEmployeeProjectsDto(Long employeeId, List<ProjectEntity> projects) {
        List<EmployeeProjectsDto.ProjectWithRoleDto> projectDtos =
            projects.stream()
                .map(project -> new EmployeeProjectsDto.ProjectWithRoleDto(
                    project.getId(),
                    project.getDesignation(),
                    project.getStartDate(),
                    project.getActualEndDate() != null ? project.getActualEndDate() : project.getPlannedEndDate(),
                    project.getEmployeeQualifications().get(employeeId)
                ))
                .collect(Collectors.toList());

        return new EmployeeProjectsDto(employeeId, projectDtos);
    }

    /**
     * Prüft ob ein Mitarbeiter in einem bestimmten Zeitraum bereits verplant ist
     */
    private boolean isEmployeeBusyInTimeRange(Long employeeId, LocalDate startDate, LocalDate endDate, Long excludeProjectId) {
        List<ProjectEntity> projectsInTimeRange = repository.findProjectsInTimeRange(startDate, endDate);

        return projectsInTimeRange.stream()
            .filter(p -> !p.getId().equals(excludeProjectId)) // Aktuelles Projekt ausschließen
            .anyMatch(p -> p.getEmployeeIds().contains(employeeId));
    }

    /**
     * Validiert ein Projekt vor dem Erstellen/Aktualisieren
     */
    private void validateProject(ProjectEntity entity) {
        // Validierung: Verantwortlicher Mitarbeiter existiert
        if (!employeeService.employeeExists(entity.getResponsibleEmployeeId())) {
            throw new ResourceNotFoundException("Mitarbeiter mit der Mitarbeiternummer existiert nicht");
        }

        // Validierung: Kunde existiert (Dummy-Implementierung)
        if (!customerService.customerExists(entity.getCustomerId())) {
            throw new ResourceNotFoundException("Kunde mit der ID " + entity.getCustomerId() + " existiert nicht.");
        }

        // Validierung: Start- vor Enddatum
        if (entity.getStartDate().isAfter(entity.getPlannedEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Startdatum darf nicht nach dem geplanten Enddatum liegen");
        }

        // Validierung: Tatsächliches Enddatum nicht vor Startdatum
        if (entity.getActualEndDate() != null && entity.getActualEndDate().isBefore(entity.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Tatsächliches Enddatum darf nicht vor dem Startdatum liegen");
        }
    }

    /**
     * Aktualisiert ein Projekt mit neuen Daten aus dem DTO
     */
    public ProjectEntity updateFromDTO(Long id, de.szut.lf8_starter.dto.UpdateProjectDTO updateDTO) {
        // Projekt laden
        ProjectEntity projekt = readById(id);

        // Validierung: Enddatum nach Startdatum
        if (updateDTO.getPlannedEndDate().isBefore(updateDTO.getStartDate())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Enddatum muss nach dem Startdatum liegen"
            );
        }

        // Daten aktualisieren
        projekt.setDesignation(updateDTO.getDesignation());
        projekt.setResponsibleEmployeeId(updateDTO.getResponsibleEmployeeId());
        projekt.setCustomerId(updateDTO.getCustomerId());
        projekt.setCustomerContactPerson(updateDTO.getCustomerContactPerson());
        projekt.setComment(updateDTO.getComment());
        projekt.setStartDate(updateDTO.getStartDate());
        projekt.setPlannedEndDate(updateDTO.getPlannedEndDate());

        // Validierung und Speichern
        return update(projekt);
    }
}
