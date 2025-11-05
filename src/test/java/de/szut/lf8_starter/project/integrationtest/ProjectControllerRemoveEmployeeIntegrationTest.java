package de.szut.lf8_starter.project.integrationtest;

import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectRepository;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerRemoveEmployeeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
    }

    private RequestPostProcessor createJwt() {
        return jwt().jwt(jwt -> jwt
                .claim("sub", "user123")
                .claim("preferred_username", "john.doe")
                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("hitec-employee")))
        ).authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_hitec-employee"));
    }

    @Test
    @DisplayName("DELETE /projects/{projectId}/employees/{employeeId} - Erfolg")
    void removeEmployee_Success() throws Exception {
        Long employeeId = 10L;

        ProjectEntity project = new ProjectEntity();
        project.setDesignation("Test Project");
        project.setResponsibleEmployeeId(1L);
        project.setCustomerId(1L);
        project.setStartDate(java.time.LocalDate.now());
        project.setPlannedEndDate(java.time.LocalDate.now().plusDays(30));
        project.setEmployeeIds(new java.util.HashSet<>(java.util.List.of(employeeId)));
        project.setEmployeeQualifications(new java.util.HashMap<>());
        project.getEmployeeQualifications().put(employeeId, "DEV");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        mockMvc.perform(delete("/projects/{projectId}/employees/{employeeId}", projectId, employeeId)
                        .with(createJwt())
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Employee successfully removed from project."))
            .andExpect(jsonPath("$.projectId").value(projectId))
            .andExpect(jsonPath("$.employeeId").value(employeeId));
    }

    @Test
    @DisplayName("DELETE /projects/{projectId}/employees/{employeeId} - Project not found")
    void removeEmployee_ProjectNotFound() throws Exception {
        Long projectId = 999L;
        Long employeeId = 10L;

        mockMvc.perform(delete("/projects/{projectId}/employees/{employeeId}", projectId, employeeId)
                        .with(createJwt())
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Projekt mit der ID 999 existiert nicht."));
    }

    @Test
    @DisplayName("DELETE /projects/{projectId}/employees/{employeeId} - Mitarbeiter nicht im Projekt")
    void removeEmployee_EmployeeNotInProject() throws Exception {
        Long employeeId = 999L;

        ProjectEntity project = new ProjectEntity();
        project.setDesignation("Test Project");
        project.setResponsibleEmployeeId(1L);
        project.setCustomerId(1L);
        project.setStartDate(java.time.LocalDate.now());
        project.setPlannedEndDate(java.time.LocalDate.now().plusDays(30));
        project.setEmployeeIds(new java.util.HashSet<>());
        project.setEmployeeQualifications(new java.util.HashMap<>());
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        mockMvc.perform(delete("/projects/{projectId}/employees/{employeeId}", projectId, employeeId)
                        .with(createJwt())
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Mitarbeiter mit der Mitarbeiternummer 999 arbeitet in dem Projekt mit der Projekt-ID " + projectId + " nicht."));
    }

    @Test
    @DisplayName("DELETE /projects/{projectId}/employees/{employeeId} - Ungültiges Format der Mitarbeiternummer")
    void removeEmployee_InvalidEmployeeIdFormat() throws Exception {
        Long projectId = 1L;
        String invalidEmployeeId = "abc";

        mockMvc.perform(delete("/projects/{projectId}/employees/{employeeId}", projectId, invalidEmployeeId)
                        .with(createJwt())
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Mitarbeiternummer hat ein ungültiges Format."));
    }
}
