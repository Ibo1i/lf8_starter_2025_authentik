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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(ProjectController.class)
class ProjectControllerRemoveEmployeeUnitTest {

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
    }

    @Test
    @DisplayName("DELETE /projects/{projectId}/employees/{employeeId} - Erfolg")
    void deleteEmployee_Success() throws Exception {
        Long projectId = 1L;
        Long employeeId = 5L;

        // Service-Methode liefert ein ProjectEntity zurück, daher -> doReturn
        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);
        when(projectService.removeEmployeeFromProject(eq(projectId), eq(employeeId))).thenReturn(project);

        mockMvc.perform(delete("/projects/{projectId}/employees/{employeeId}", projectId, employeeId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Mitarbeiter erfolgreich aus Projekt entfernt."))
            .andExpect(jsonPath("$.projectId").value(projectId))
            .andExpect(jsonPath("$.employeeId").value(employeeId));

        verify(projectService).removeEmployeeFromProject(projectId, employeeId);
    }

    @Test
    @DisplayName("DELETE /projects/{projectId}/employees/{employeeId} - Projekt nicht gefunden -> 404")
    void deleteEmployee_ProjectNotFound() throws Exception {
        Long projectId = 99L;
        Long employeeId = 5L;

        when(projectService.removeEmployeeFromProject(eq(projectId), eq(employeeId))).thenThrow(new ResourceNotFoundException("Projekt mit der ID " + projectId + " existiert nicht."));

        mockMvc.perform(delete("/projects/{projectId}/employees/{employeeId}", projectId, employeeId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Projekt mit der ID " + projectId + " existiert nicht."));

        verify(projectService).removeEmployeeFromProject(projectId, employeeId);
    }

    @Test
    @DisplayName("DELETE /projects/{projectId}/employees/{employeeId} - Ungültige employeeId -> 400")
    void deleteEmployee_InvalidEmployeeId() throws Exception {
        mockMvc.perform(delete("/projects/{projectId}/employees/{employeeId}", 1, "abc")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Mitarbeiternummer hat ein ungültiges Format."));
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
