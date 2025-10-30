package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.EmployeeProjectsDto;
import de.szut.lf8_starter.project.dto.EmployeeProjectsResponseDto;
import de.szut.lf8_starter.project.dto.ProjectSummaryDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping
public class EmployeeProjectsController {
    private final ProjectService projectService;

    public EmployeeProjectsController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping(path = "/employees/{employeeId}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmployeeProjectsResponseDto getProjectsForEmployee(@PathVariable("employeeId") Long employeeId) {
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

