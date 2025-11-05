package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectMapper {

    public ProjectGetDto mapToGetDto(ProjectEntity entity) {
        return new ProjectGetDto(
            entity.getId(),
            entity.getDesignation(),
            entity.getResponsibleEmployeeId(),
            entity.getCustomerId(),
            entity.getCustomerContactPerson(),
            entity.getComment(),
            entity.getStartDate(),
            entity.getPlannedEndDate(),
            entity.getActualEndDate(),
            entity.getEmployeeIds()
        );
    }

    public ProjectEntity mapCreateDtoToEntity(ProjectCreateDto dto) {
        var entity = new ProjectEntity();
        entity.setDesignation(dto.getDesignation());
        entity.setResponsibleEmployeeId(dto.getResponsibleEmployeeId());
        entity.setCustomerId(dto.getCustomerId());
        entity.setCustomerContactPerson(dto.getCustomerContactPerson());
        entity.setComment(dto.getComment());
        entity.setStartDate(dto.getStartDate());
        entity.setPlannedEndDate(dto.getPlannedEndDate());
        entity.setActualEndDate(dto.getActualEndDate());
        return entity;
    }

    public ProjectEmployeesDto mapToProjectEmployeesDto(ProjectEntity entity) {
        List<ProjectEmployeesDto.EmployeeWithQualificationDto> employees =
            entity.getEmployeeIds().stream()
                .map(employeeId -> new ProjectEmployeesDto.EmployeeWithQualificationDto(
                    employeeId,
                    entity.getEmployeeQualifications().get(employeeId)
                ))
                .collect(Collectors.toList());

        return new ProjectEmployeesDto(entity.getId(), entity.getDesignation(), employees);
    }

    public EmployeeProjectsDto mapToEmployeeProjectsDto(Long employeeId, List<ProjectEntity> projects) {
        return getEmployeeProjectsDto(employeeId, projects);
    }

    @NotNull
    static EmployeeProjectsDto getEmployeeProjectsDto(Long employeeId, List<ProjectEntity> projects) {
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

    public EmployeeAssignmentResponseDto mapToEmployeeAssignmentResponseDto(ProjectEntity project, Long employeeId, String employeeName) {
        return new EmployeeAssignmentResponseDto(
            project.getId(),
            project.getDesignation(),
            employeeId,
            employeeName
        );
    }
}
