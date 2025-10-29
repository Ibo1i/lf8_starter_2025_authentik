package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.ProjectCreateDto;
import de.szut.lf8_starter.project.dto.ProjectGetDto;
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

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
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
}
