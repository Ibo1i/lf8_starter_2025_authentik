package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.ApiErrorResponse;
import de.szut.lf8_starter.project.dto.EmployeeAssignmentDto;
import de.szut.lf8_starter.project.dto.EmployeeAssignmentResponseDto;
import de.szut.lf8_starter.project.dto.ProjectCreateDto;
import de.szut.lf8_starter.project.dto.ProjectGetDto;
import de.szut.lf8_starter.project.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/projects")
@Tag(name = "Projects", description = "Project management operations including CRUD and employee assignments")
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
    @Operation(
        summary = "Create a new project",
        description = "Creates a new project with the specified details. The responsible employee must exist in the system."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Project created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectGetDto.class),
                examples = @ExampleObject(value = """
                    {
                      "id": 1001,
                      "designation": "Cloud Migration Project Alpha",
                      "responsibleEmployeeId": 1,
                      "customerId": 42,
                      "customerContactPerson": "John Miller",
                      "comment": "Migration of legacy systems to AWS infrastructure",
                      "startDate": "2025-01-15",
                      "plannedEndDate": "2025-06-30",
                      "actualEndDate": null,
                      "employeeIds": []
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data - validation failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-01-15T14:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Required field is missing or invalid.",
                      "path": "/projects",
                      "validationErrors": [
                        {
                          "field": "designation",
                          "message": "Designation cannot be blank"
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
            responseCode = "403",
            description = "Forbidden - insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ProjectGetDto createProject(@RequestBody @Valid ProjectCreateDto projectCreateDto) {
        // ...existing code...
        ProjectEntity projectEntity = this.projectMapper.mapCreateDtoToEntity(projectCreateDto);
        projectEntity = this.projectService.create(projectEntity);
        return this.projectMapper.mapToGetDto(projectEntity);
    }

    @GetMapping
    @Operation(
        summary = "Get all projects",
        description = "Retrieves a list of all projects in the system with their basic details and assigned employee IDs."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of projects returned successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectGetDto.class),
                examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1001,
                        "designation": "Cloud Migration Project Alpha",
                        "responsibleEmployeeId": 1,
                        "customerId": 42,
                        "customerContactPerson": "John Miller",
                        "comment": "Migration of legacy systems to AWS",
                        "startDate": "2025-01-15",
                        "plannedEndDate": "2025-06-30",
                        "actualEndDate": null,
                        "employeeIds": [1, 2, 5]
                      }
                    ]
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
        )
    })
    public List<ProjectGetDto> getAllProjects() {
        // ...existing code...
        List<ProjectEntity> projects = this.projectService.readAll();
        return projects.stream()
                .map(this.projectMapper::mapToGetDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{projectId}")
    @Operation(
        summary = "Get a project by ID",
        description = "Retrieves detailed information about a specific project including all assigned employees."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Project found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectGetDto.class),
                examples = @ExampleObject(value = """
                    {
                      "id": 1001,
                      "designation": "Cloud Migration Project Alpha",
                      "responsibleEmployeeId": 1,
                      "customerId": 42,
                      "customerContactPerson": "John Miller",
                      "comment": "Migration of legacy systems to AWS infrastructure",
                      "startDate": "2025-01-15",
                      "plannedEndDate": "2025-06-30",
                      "actualEndDate": null,
                      "employeeIds": [1, 2, 5]
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-01-15T14:30:00",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Project with ID 9999 not found",
                      "path": "/projects/9999"
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
        )
    })
    public ProjectGetDto getProjectById(
        @Parameter(description = "Unique project identifier", example = "1001", required = true)
        @PathVariable("projectId") Long id
    ) {
        // ...existing code...
        ProjectEntity projectEntity = this.projectService.readById(id);
        return this.projectMapper.mapToGetDto(projectEntity);
    }
    @PutMapping("/{projectId}")
    @Operation(
        summary = "Update an existing project",
        description = "Updates project details. Only provided fields will be modified."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Project updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectGetDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - missing or invalid JWT token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ProjectGetDto updateProject(
        @Parameter(description = "Unique project identifier", example = "1001", required = true)
        @PathVariable("projectId") Long id,
        @RequestBody @Valid de.szut.lf8_starter.dto.UpdateProjectDTO updateDTO
    ) {
        // ...existing code...
        ProjectEntity updatedProject = this.projectService.updateFromDTO(id, updateDTO);
        return this.projectMapper.mapToGetDto(updatedProject);
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a project by ID",
        description = "Permanently deletes a project. Projects with assigned employees cannot be deleted."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Project deleted successfully",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Project has employee assignments and cannot be deleted",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-01-15T14:30:00",
                      "status": 409,
                      "error": "Conflict",
                      "message": "Cannot delete project with active employee assignments",
                      "path": "/projects/1001"
                    }
                    """)
            )
        )
    })
    public void deleteProject(
        @Parameter(description = "Unique project identifier", example = "1001", required = true)
        @PathVariable("projectId") Long id
    ) {
        // ...existing code...
        this.projectService.deleteById(id);
    }

    @PostMapping("/{projectId}/employees")
    @Operation(
        summary = "Assign an employee to a project",
        description = """
            Assigns an employee to a project with qualification validation.
            
            **Validation steps:**
            1. Project must exist
            2. Employee must exist (checked via Employee Service)
            3. Employee must possess the required qualification
            4. Qualification must not be expired
            5. No time conflicts with other project assignments
            
            **Note:** This endpoint integrates with the external Employee Service and may fail with 502/503/504 if the service is unavailable.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Employee assigned successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EmployeeAssignmentResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "projectId": 1001,
                      "projectName": "Cloud Migration Project Alpha",
                      "employeeId": 2,
                      "employeeName": "Jane Doe"
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data - validation failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-01-15T14:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Required field is missing or invalid.",
                      "path": "/projects/1001/employees",
                      "validationErrors": [
                        {
                          "field": "qualification",
                          "message": "Qualification is required"
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
            description = "Project or employee not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-01-15T14:30:00",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Employee with ID 999 not found",
                      "path": "/projects/1001/employees"
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Time conflict - employee is already assigned to another project in the same time period",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-01-15T14:30:00",
                      "status": 409,
                      "error": "Conflict",
                      "message": "Employee 2 is already assigned to another project during this time period",
                      "path": "/projects/1001/employees",
                      "conflictingProjects": [
                        {
                          "projectId": 1002,
                          "projectName": "Database Optimization Project",
                          "startDate": "2025-02-01",
                          "endDate": "2025-05-31"
                        }
                      ]
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Missing or expired qualification",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-01-15T14:30:00",
                      "status": 422,
                      "error": "Unprocessable Entity",
                      "message": "Employee does not have the required qualification: Java Developer",
                      "path": "/projects/1001/employees"
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "502",
            description = "Bad Gateway - Employee Service returned an error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-01-15T14:30:00",
                      "status": 502,
                      "error": "Bad Gateway",
                      "message": "Employee Service returned an error",
                      "path": "/projects/1001/employees",
                      "service": "Employee Service",
                      "upstreamStatus": 500
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Service Unavailable - Circuit Breaker is OPEN due to repeated failures",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-01-15T14:30:00",
                      "status": 503,
                      "error": "Service Unavailable",
                      "message": "Employee Service is currently unavailable (Circuit Breaker OPEN)",
                      "path": "/projects/1001/employees",
                      "circuitBreakerState": "OPEN",
                      "retryAfter": 60
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "504",
            description = "Gateway Timeout - Employee Service did not respond in time",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-01-15T14:30:00",
                      "status": 504,
                      "error": "Gateway Timeout",
                      "message": "Employee Service request timed out after 3000ms",
                      "path": "/projects/1001/employees",
                      "service": "Employee Service"
                    }
                    """)
            )
        )
    })
    public EmployeeAssignmentResponseDto assignEmployeeToProject(
        @Parameter(description = "Unique project identifier", example = "1001", required = true)
        @PathVariable Long projectId,
        @RequestBody @Valid EmployeeAssignmentDto request
    ) {
        // ...existing code...
        ProjectEntity updated = this.projectService.addEmployeeToProject(projectId, request.getEmployeeId(), request.getQualification());

        String employeeName = this.employeeService.getEmployeeName(request.getEmployeeId());

        return new EmployeeAssignmentResponseDto(updated.getId(), updated.getDesignation(), request.getEmployeeId(), employeeName);
    }

    @DeleteMapping("/{projectId}/employees/{employeeId}")
    @Operation(
        summary = "Remove an employee from a project",
        description = "Removes an existing employee assignment from a project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Employee removed successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "message": "Employee successfully removed from project.",
                      "projectId": 1001,
                      "employeeId": 2
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid employee ID format",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
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
            description = "Project or assignment not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-01-15T14:30:00",
                      "status": 404,
                      "error": "Not Found",
                      "message": "Employee 2 is not assigned to project 1001",
                      "path": "/projects/1001/employees/2"
                    }
                    """)
            )
        )
    })
    public java.util.Map<String, Object> removeEmployeeFromProject(
        @Parameter(description = "Unique project identifier", example = "1001", required = true)
        @PathVariable Long projectId,
        @Parameter(description = "Unique employee identifier", example = "2", required = true)
        @PathVariable Long employeeId
    ) {
        // ...existing code...
        this.projectService.removeEmployeeFromProject(projectId, employeeId);
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("message", "Employee successfully removed from project.");
        body.put("projectId", projectId);
        body.put("employeeId", employeeId);
        return body;
    }

    @GetMapping("/{projectId}/employees")
    @Operation(
        summary = "Get all employees assigned to a project",
        description = "Retrieves detailed information about all employees currently assigned to the specified project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Employees returned successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = de.szut.lf8_starter.project.dto.ProjectEmployeesDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - missing or invalid JWT token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public de.szut.lf8_starter.project.dto.ProjectEmployeesDto getProjectEmployees(
        @Parameter(description = "Unique project identifier", example = "1001", required = true)
        @PathVariable("projectId") Long id
    ) {
        // ...existing code...
        return this.projectService.getProjectEmployees(id);
    }
}
