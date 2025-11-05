package de.szut.lf8_starter.project.unittest;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.ProjectController;
import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectMapper;
import de.szut.lf8_starter.project.ProjectService;
import de.szut.lf8_starter.project.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@WithMockUser
@DisplayName("ProjectController Delete Project Tests")
class ProjectControllerDeleteProjectUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ProjectMapper projectMapper;

    @MockBean
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        ProjectEntity testProject = new ProjectEntity();
        testProject.setId(1L);
        testProject.setDesignation("Test Projekt");
        testProject.setCustomerId(100L);
        testProject.setResponsibleEmployeeId(50L);
        testProject.setComment("Test Kommentar");
        testProject.setStartDate(LocalDate.of(2025, 1, 1));
        testProject.setPlannedEndDate(LocalDate.of(2025, 12, 31));
    }

    @Test
    @DisplayName("DELETE /projects/{projectId} - Successful deletion (204)")
    void deleteProject_ExistingProject_ReturnsNoContent() throws Exception {
        Long projectId = 1L;
        doNothing().when(projectService).deleteById(projectId);

        mockMvc.perform(delete("/projects/{projectId}", projectId).with(csrf()).with(user("test").roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /projects/{projectId} - Project not found (404)")
    void deleteProject_NotFound_ReturnsNotFound() throws Exception {
        Long nonExistentId = 999L;
        doThrow(new ResourceNotFoundException("Project with the ID " + nonExistentId + " does not exist."))
                .when(projectService).deleteById(nonExistentId);

        mockMvc.perform(delete("/projects/{projectId}", nonExistentId).with(csrf()).with(user("test").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /projects/{projectId} - Conflict in employee assignments (409)")
    void deleteProject_Conflict_ReturnsConflict() throws Exception {
        Long id = 2L;
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT))
                .when(projectService).deleteById(id);

        mockMvc.perform(delete("/projects/{projectId}", id).with(csrf()).with(user("test").roles("USER")))
                .andExpect(status().isConflict());
    }

    public ProjectMapper getProjectMapper() {
        return projectMapper;
    }

    public void setProjectMapper(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
}
