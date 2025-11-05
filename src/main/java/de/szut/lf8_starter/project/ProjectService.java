package de.szut.lf8_starter.project;

import de.szut.lf8_starter.exceptionHandling.*;
import de.szut.lf8_starter.project.dto.ConflictingProjectDto;
import de.szut.lf8_starter.project.dto.EmployeeProjectsDto;
import de.szut.lf8_starter.project.dto.ProjectEmployeesDto;
import de.szut.lf8_starter.project.service.CustomerService;
import de.szut.lf8_starter.project.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.szut.lf8_starter.project.ProjectMapper.getEmployeeProjectsDto;

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
        // Check if project exists
        readById(entity.getId());
        // Validate before update
        validateProject(entity);
        return this.repository.save(entity);
    }

    public void delete(ProjectEntity entity) {
        this.repository.delete(entity);
    }

    public void deleteById(Long id) {
        ProjectEntity project = readById(id);

        // Check dependencies: no employees should be assigned
        if (project.getEmployeeIds() != null && !project.getEmployeeIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Project still has employee assignments and cannot be deleted.");
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
     * Adds an employee with a specific qualification to a project
     */
    public ProjectEntity addEmployeeToProject(Long projectId, Long employeeId, String qualification) {
        ProjectEntity project = readById(projectId);

        // Check for existing assignment before further validations
        if (project.getEmployeeIds() != null && project.getEmployeeIds().contains(employeeId)) {
            LocalDate assigned = project.getEmployeeAssignedDates() != null ? project.getEmployeeAssignedDates().get(employeeId) : null;
            String role = project.getEmployeeQualifications() != null ? project.getEmployeeQualifications().get(employeeId) : null;
            throw new DuplicateAssignmentException(projectId, employeeId, assigned, role);
        }

        // Validation: Employee exists
        if (!employeeService.employeeExists(employeeId)) {
            throw new EmployeeNotFoundException(employeeId);
        }

        // Validation: Employee has the required qualification
        if (!employeeService.employeeHasQualification(employeeId, qualification)) {
            throw new EmployeeQualificationException(qualification);
        }

        // Validation: Check for time conflicts
        List<ProjectEntity> conflicting = repository.findProjectsInTimeRange(project.getStartDate(), project.getPlannedEndDate())
            .stream()
            .filter(p -> !p.getId().equals(projectId) && p.getEmployeeIds().contains(employeeId))
                .toList();

        if (!conflicting.isEmpty()) {
            DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
            List<ConflictingProjectDto> conflictingDtos = conflicting.stream()
                .map(p -> new ConflictingProjectDto(p.getId(), p.getDesignation(), p.getStartDate(), p.getActualEndDate() != null ? p.getActualEndDate() : p.getPlannedEndDate()))
                .collect(Collectors.toList());

            String firstStart = conflictingDtos.getFirst().getStartDate().format(df);
            String firstEnd = conflictingDtos.getFirst().getEndDate().format(df);
            throw new TimeConflictException(firstStart, firstEnd, conflictingDtos);
        }

        // Add employee
        if (project.getEmployeeIds() == null) {
            project.setEmployeeIds(new java.util.HashSet<>());
        }
        if (project.getEmployeeQualifications() == null) {
            project.setEmployeeQualifications(new java.util.HashMap<>());
        }
        if (project.getEmployeeAssignedDates() == null) {
            project.setEmployeeAssignedDates(new java.util.HashMap<>());
        }

        project.getEmployeeIds().add(employeeId);
        project.getEmployeeQualifications().put(employeeId, qualification);
        // set assigned date to today
        project.getEmployeeAssignedDates().put(employeeId, LocalDate.now());

        return this.repository.save(project);
    }

    /**
     * Removes an employee from a project
     */
    public ProjectEntity removeEmployeeFromProject(Long projectId, Long employeeId) {
        ProjectEntity project = readById(projectId);

        // Check if employee works on the project
        if (project.getEmployeeIds() == null || !project.getEmployeeIds().contains(employeeId)) {
            throw new ResourceNotFoundException("Mitarbeiter mit der Mitarbeiternummer " + employeeId +
                " arbeitet in dem Projekt mit der Projekt-ID " + projectId + " nicht.");
        }

        // Remove employee
        project.getEmployeeIds().remove(employeeId);
        project.getEmployeeQualifications().remove(employeeId);
        project.getEmployeeAssignedDates().remove(employeeId);

        return this.repository.save(project);
    }

    /**
     * Returns all employees of a project with their qualifications
     */
    public ProjectEmployeesDto getProjectEmployees(Long projectId) {
        ProjectEntity project = readById(projectId);
        return mapToProjectEmployeesDto(project);
    }

    /**
     * Returns all projects of an employee
     */
    public EmployeeProjectsDto getEmployeeProjects(Long employeeId) {
        // Validation: Employee exists
        if (!employeeService.employeeExists(employeeId)) {
            throw new ResourceNotFoundException("Employee with ID " + employeeId + " does not exist");
        }

        List<ProjectEntity> projects = findProjectsByEmployeeId(employeeId);
        return mapToEmployeeProjectsDto(employeeId, projects);
    }

    /**
     * Helper method for mapper access
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
     * Helper method for mapper access
     */
    private EmployeeProjectsDto mapToEmployeeProjectsDto(Long employeeId, List<ProjectEntity> projects) {
        return getEmployeeProjectsDto(employeeId, projects);
    }

    /**
     * Checks if an employee is already busy in a specific time range
     */
    private boolean isEmployeeBusyInTimeRange(Long employeeId, LocalDate startDate, LocalDate endDate, Long excludeProjectId) {
        List<ProjectEntity> projectsInTimeRange = repository.findProjectsInTimeRange(startDate, endDate);

        return projectsInTimeRange.stream()
            .filter(p -> !p.getId().equals(excludeProjectId)) // Exclude current project
            .anyMatch(p -> p.getEmployeeIds().contains(employeeId));
    }

    /**
     * Validates a project before creating/updating
     */
    private void validateProject(ProjectEntity entity) {
        // Validation: Responsible employee exists
        if (!employeeService.employeeExists(entity.getResponsibleEmployeeId())) {
            throw new ResourceNotFoundException("Employee with employee ID does not exist");
        }

        // Validation: Customer exists (dummy implementation)
        if (!customerService.customerExists(entity.getCustomerId())) {
            throw new ResourceNotFoundException("Customer with ID " + entity.getCustomerId() + " does not exist.");
        }

        // Validation: Start date before end date
        if (entity.getStartDate().isAfter(entity.getPlannedEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Start date cannot be after the planned end date");
        }

        // Validation: Actual end date not before start date
        if (entity.getActualEndDate() != null && entity.getActualEndDate().isBefore(entity.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Actual end date cannot be before the start date");
        }
    }

    /**
     * Updates a project with new data from the DTO
     */
    public ProjectEntity updateFromDTO(Long id, de.szut.lf8_starter.dto.UpdateProjectDTO updateDTO) {
        // Load project
        ProjectEntity projekt = readById(id);

        // Validation: End date after start date
        if (updateDTO.getPlannedEndDate().isBefore(updateDTO.getStartDate())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "End date must be after the start date"
            );
        }

        // Update data
        projekt.setDesignation(updateDTO.getDesignation());
        projekt.setResponsibleEmployeeId(updateDTO.getResponsibleEmployeeId());
        projekt.setCustomerId(updateDTO.getCustomerId());
        projekt.setCustomerContactPerson(updateDTO.getCustomerContactPerson());
        projekt.setComment(updateDTO.getComment());
        projekt.setStartDate(updateDTO.getStartDate());
        projekt.setPlannedEndDate(updateDTO.getPlannedEndDate());

        // Validate and save
        return update(projekt);
    }
}
