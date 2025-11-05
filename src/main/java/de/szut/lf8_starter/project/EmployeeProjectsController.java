package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.ApiErrorResponse;
import de.szut.lf8_starter.project.dto.EmployeeProjectsDto;
import de.szut.lf8_starter.project.dto.EmployeeProjectsResponseDto;
import de.szut.lf8_starter.project.dto.ProjectSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping
@Tag(name = "Employee-Projects", description = "Retrieve projects assigned to specific employees")
public class EmployeeProjectsController {

    private final ProjectService projectService;

    public EmployeeProjectsController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping(path = "/employees/{employeeId}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get projects for a specific employee",
        description = "Retrieves all projects that a specific employee is currently assigned to, including their role in each project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Projects retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EmployeeProjectsResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "employeeId": 2,
                      "projects": [
                        {
                          "id": 1001,
                          "designation": "Cloud Migration Project Alpha",
                          "startDate": "2025-01-15",
                          "endDate": "2025-06-30",
                          "role": "Java Developer"
                        },
                        {
                          "id": 1003,
                          "designation": "Mobile App Development",
                          "startDate": "2025-03-01",
                          "endDate": "2025-08-31",
                          "role": "Frontend Developer"
                        }
                      ]
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - missing or invalid JWT token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Employee not found or has no project assignments",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public EmployeeProjectsResponseDto getProjectsForEmployee(
        @Parameter(description = "Unique employee identifier", example = "2", required = true)
        @PathVariable("employeeId") Long employeeId
    ) {
        // ...existing code...
        EmployeeProjectsDto dto = this.projectService.getEmployeeProjects(employeeId);

        EmployeeProjectsResponseDto response = new EmployeeProjectsResponseDto();
        response.setEmployeeId(dto.getEmployeeId());
        response.setProjects(dto.getProjects().stream().map(p -> new ProjectSummaryDto(
                p.getProjectId(),
                p.getDesignation(),
                p.getStartDate(),
                p.getEndDate(),
                p.getQualification()
        )).collect(Collectors.toList()));

        return response;
    }
}

