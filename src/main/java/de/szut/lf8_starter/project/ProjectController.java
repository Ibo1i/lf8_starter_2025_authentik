package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.EmployeeAssignmentDto;
import de.szut.lf8_starter.project.dto.EmployeeAssignmentResponseDto;
import de.szut.lf8_starter.project.dto.ProjectCreateDto;
import de.szut.lf8_starter.project.dto.ProjectGetDto;
import de.szut.lf8_starter.project.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final EmployeeService employeeService;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper, EmployeeService employeeService) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.employeeService = employeeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Project created successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ProjectGetDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content)
    })
    public ProjectGetDto createProject(@RequestBody @Valid ProjectCreateDto projectCreateDto) {
        ProjectEntity projectEntity = this.projectMapper.mapCreateDtoToEntity(projectCreateDto);
        projectEntity = this.projectService.create(projectEntity);
        return this.projectMapper.mapToGetDto(projectEntity);
    }

    @GetMapping
    @Operation(summary = "Get all projects")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of projects returned successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ProjectGetDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content)
    })
    public List<ProjectGetDto> getAllProjects() {
        List<ProjectEntity> projects = this.projectService.readAll();
        return projects.stream()
                .map(this.projectMapper::mapToGetDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a project by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Project found successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ProjectGetDto.class))),
        @ApiResponse(responseCode = "404", description = "Project not found",
            content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content)
    })
    public ProjectGetDto getProjectById(@PathVariable Long id) {
        ProjectEntity projectEntity = this.projectService.readById(id);
        return this.projectMapper.mapToGetDto(projectEntity);
    }
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectGetDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    public ProjectGetDto updateProject(
            @PathVariable Long id,
            @RequestBody @Valid de.szut.lf8_starter.dto.UpdateProjectDTO updateDTO
    ) {
        ProjectEntity updatedProject = this.projectService.updateFromDTO(id, updateDTO);
        return this.projectMapper.mapToGetDto(updatedProject);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a project by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Project has employee assignments and cannot be deleted", content = @Content)
    })
    public void deleteProject(@PathVariable Long id) {
        this.projectService.deleteById(id);
    }

    @PostMapping("/{projectId}/employees")
    @Operation(summary = "Assign an employee to a project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee assigned successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = EmployeeAssignmentResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Project or employee not found", content = @Content),
        @ApiResponse(responseCode = "409", description = "Time conflict", content = @Content),
        @ApiResponse(responseCode = "422", description = "Missing qualification", content = @Content)
    })
    public EmployeeAssignmentResponseDto assignEmployeeToProject(
            @PathVariable Long projectId,
            @RequestBody @Valid EmployeeAssignmentDto request
    ) {
        ProjectEntity updated = this.projectService.addEmployeeToProject(projectId, request.getEmployeeId(), request.getQualification());

        String employeeName = this.employeeService.getEmployeeName(request.getEmployeeId());

        return new EmployeeAssignmentResponseDto(updated.getId(), updated.getDesignation(), request.getEmployeeId(), employeeName);
    }

    @DeleteMapping("/{projectId}/employees/{employeeId}")
    @Operation(summary = "Remove an employee from a project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee removed successfully",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Ungültiges Format der Mitarbeiternummer", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Project or assignment not found", content = @Content)
    })
    public java.util.Map<String, Object> removeEmployeeFromProject(
            @PathVariable Long projectId,
            @PathVariable Long employeeId
    ) {
        this.projectService.removeEmployeeFromProject(projectId, employeeId);
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("message", "Mitarbeiter erfolgreich aus Projekt entfernt.");
        body.put("projectId", projectId);
        body.put("employeeId", employeeId);
        return body;
    }
}
