package de.szut.lf8_starter.project.unittest;

import de.szut.lf8_starter.exceptionHandling.DuplicateAssignmentException;
import de.szut.lf8_starter.exceptionHandling.EmployeeNotFoundException;
import de.szut.lf8_starter.exceptionHandling.EmployeeQualificationException;
import de.szut.lf8_starter.exceptionHandling.TimeConflictException;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.ProjectController;
import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectMapper;
import de.szut.lf8_starter.project.ProjectService;
import de.szut.lf8_starter.project.service.EmployeeService;
import org.springframework.dao.DataIntegrityViolationException;
import de.szut.lf8_starter.project.dto.EmployeeAssignmentDto;
import de.szut.lf8_starter.project.dto.ConflictingProjectDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("unused")
@WebMvcTest(ProjectController.class)
@WithMockUser
@DisplayName("ProjectController Assign Employee Tests")
public class ProjectControllerAssignEmployeeToProjectUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ProjectMapper projectMapper;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /projects/{projectId}/employees - success returns 200 with assignment DTO")
    void assignEmployee_success_returns200() throws Exception {
        Long projectId = 1L;
        Long employeeId = 100L;
        String role = "JAVA";

        EmployeeAssignmentDto request = new EmployeeAssignmentDto(employeeId, role);

        // Mock service to return a ProjectEntity (we'll just construct minimal response DTO in controller path)
        // Controller returns EmployeeAssignmentResponseDto, so we can mock projectService.addEmployeeToProject to return a ProjectEntity
        ProjectEntity returned = new ProjectEntity();
        returned.setId(projectId);
        returned.setDesignation("TestProjekt");

        when(projectService.addEmployeeToProject(projectId, employeeId, role)).thenReturn(returned);
        when(employeeService.getEmployeeName(employeeId)).thenReturn("Mitarbeiter " + employeeId);

        mockMvc.perform(post("/projects/{projectId}/employees", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(projectId))
                .andExpect(jsonPath("$.projectName").value("TestProjekt"))
                .andExpect(jsonPath("$.employeeId").value(employeeId))
                .andExpect(jsonPath("$.employeeName").value("Mitarbeiter " + employeeId));
    }

    @Test
    @DisplayName("POST /projects/{projectId}/employees - duplicate assignment returns 409 with existingAssignment")
    void assignEmployee_duplicate_returns409() throws Exception {
        Long projectId = 1L;
        Long employeeId = 500L;
        LocalDate assignedDate = LocalDate.of(2025,1,15);
        String role = "Backend Developer";

        // Mock service to throw DuplicateAssignmentException
        when(projectService.addEmployeeToProject(projectId, employeeId, role))
            .thenThrow(new DuplicateAssignmentException(projectId, employeeId, assignedDate, role));

        EmployeeAssignmentDto request = new EmployeeAssignmentDto(employeeId, role);

        mockMvc.perform(post("/projects/{projectId}/employees", projectId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Mitarbeiter mit der Mitarbeiternummer " + employeeId + " ist bereits dem Projekt mit der ID " + projectId + " zugewiesen."))
            .andExpect(jsonPath("$.existingAssignment.assignedDate").value("2025-01-15"))
            .andExpect(jsonPath("$.existingAssignment.role").value(role));
    }

    @Test
    @DisplayName("POST /projects/{projectId}/employees - employee not found returns 404")
    void assignEmployee_employeeNotFound_returns404() throws Exception {
        Long projectId = 1L;
        Long employeeId = 200L;
        String role = "JAVA";

        EmployeeAssignmentDto request = new EmployeeAssignmentDto(employeeId, role);
        when(projectService.addEmployeeToProject(projectId, employeeId, role)).thenThrow(new EmployeeNotFoundException(employeeId));

        mockMvc.perform(post("/projects/{projectId}/employees", projectId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Mitarbeiter mit der Mitarbeiternummer " + employeeId + " existiert nicht."));
    }

    @Test
    @DisplayName("POST /projects/{projectId}/employees - missing qualification returns 422")
    void assignEmployee_missingQualification_returns422() throws Exception {
        Long projectId = 1L;
        Long employeeId = 300L;
        String role = "PYTHON";

        EmployeeAssignmentDto request = new EmployeeAssignmentDto(employeeId, role);
        when(projectService.addEmployeeToProject(projectId, employeeId, role)).thenThrow(new EmployeeQualificationException(role));

        mockMvc.perform(post("/projects/{projectId}/employees", projectId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message").value("Mitarbeiter hat die Qualifikation " + role + " nicht."));
    }

    @Test
    @DisplayName("POST /projects/{projectId}/employees - time conflict returns 409 with conflictingProjects")
    void assignEmployee_timeConflict_returns409() throws Exception {
        Long projectId = 1L;
        Long employeeId = 400L;
        String role = "DEV";

        EmployeeAssignmentDto request = new EmployeeAssignmentDto(employeeId, role);

        ConflictingProjectDto cp = new ConflictingProjectDto(2L, "Other", LocalDate.of(2025,1,1), LocalDate.of(2025,12,31));
        when(projectService.addEmployeeToProject(projectId, employeeId, role)).thenThrow(new TimeConflictException("2025-01-01", "2025-12-31", List.of(cp)));

        mockMvc.perform(post("/projects/{projectId}/employees", projectId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.conflictingProjects").isArray())
            .andExpect(jsonPath("$.conflictingProjects[0].projectId").value(2))
            .andExpect(jsonPath("$.conflictingProjects[0].projectName").value("Other"));
    }

    @Test
    @DisplayName("POST /projects/{projectId}/employees - invalid projectId format returns 400")
    void assignEmployee_invalidProjectId_returns400() throws Exception {
        String projectId = "abc";
        Long employeeId = 100L;
        String role = "JAVA";

        EmployeeAssignmentDto request = new EmployeeAssignmentDto(employeeId, role);

        mockMvc.perform(post("/projects/{projectId}/employees", projectId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /projects/{projectId}/employees - project not found returns 404")
    void assignEmployee_projectNotFound_returns404() throws Exception {
        Long projectId = 99L;
        Long employeeId = 100L;
        String role = "JAVA";

        EmployeeAssignmentDto request = new EmployeeAssignmentDto(employeeId, role);
        when(projectService.addEmployeeToProject(projectId, employeeId, role))
            .thenThrow(new ResourceNotFoundException("Projekt mit der ID " + projectId + " existiert nicht."));

        mockMvc.perform(post("/projects/{projectId}/employees", projectId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Projekt mit der ID " + projectId + " existiert nicht."));
    }

    @Test
    @DisplayName("POST /projects/{projectId}/employees - db unique constraint returns 409")
    void assignEmployee_dbUniqueConstraint_returns409() throws Exception {
        Long projectId = 1L;
        Long employeeId = 600L;
        String role = "DEV";

        EmployeeAssignmentDto request = new EmployeeAssignmentDto(employeeId, role);
        when(projectService.addEmployeeToProject(projectId, employeeId, role))
            .thenThrow(new DataIntegrityViolationException("unique constraint"));

        mockMvc.perform(post("/projects/{projectId}/employees", projectId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Mitarbeiter ist bereits dem Projekt zugewiesen."));
    }

    @Test
    @DisplayName("POST /projects/{projectId}/employees - empty body returns 404 (employee null)")
    void assignEmployee_emptyBody_returns404() throws Exception {
        Long projectId = 1L;

        // Mock service to handle null employeeId/qualification
        when(projectService.addEmployeeToProject(eq(projectId), isNull(), isNull()))
            .thenThrow(new EmployeeNotFoundException(null));

        mockMvc.perform(post("/projects/{projectId}/employees", projectId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isNotFound());
    }

    public ProjectMapper getProjectMapper() {
        return projectMapper;
    }

    public void setProjectMapper(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }
}
