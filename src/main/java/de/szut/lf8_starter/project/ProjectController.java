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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAuthority('user')")
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
}
