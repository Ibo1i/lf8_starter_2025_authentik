package de.szut.lf8_starter.project;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
class ProjectControllerRemoveEmployeeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
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

        mockMvc.perform(MockMvcRequestBuilders.delete("/projects/{projectId}/employees/{employeeId}", projectId, employeeId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Mitarbeiter erfolgreich aus Projekt entfernt."))
            .andExpect(jsonPath("$.projectId").value(projectId))
            .andExpect(jsonPath("$.employeeId").value(employeeId));
    }

    @Test
    @DisplayName("DELETE /projects/{projectId}/employees/{employeeId} - Projekt nicht gefunden")
    void removeEmployee_ProjectNotFound() throws Exception {
        Long projectId = 999L;
        Long employeeId = 10L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/projects/{projectId}/employees/{employeeId}", projectId, employeeId)
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

        mockMvc.perform(MockMvcRequestBuilders.delete("/projects/{projectId}/employees/{employeeId}", projectId, employeeId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Mitarbeiter mit der Mitarbeiternummer 999 arbeitet in dem Projekt mit der Projekt-ID " + projectId + " nicht."));
    }

    @Test
    @DisplayName("DELETE /projects/{projectId}/employees/{employeeId} - Ungültiges Format der Mitarbeiternummer")
    void removeEmployee_InvalidEmployeeIdFormat() throws Exception {
        Long projectId = 1L;
        String invalidEmployeeId = "abc";

        mockMvc.perform(MockMvcRequestBuilders.delete("/projects/{projectId}/employees/{employeeId}", projectId, invalidEmployeeId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Mitarbeiternummer hat ein ungültiges Format."));
    }

    // @Test
    // @DisplayName("DELETE /projects/{projectId}/employees/{employeeId} - Ungültiger oder fehlender JWT-Token")
    // @WithAnonymousUser
    // void removeEmployee_Unauthorized() throws Exception {
    //     Long employeeId = 10L;

    //     ProjectEntity project = new ProjectEntity();
    //     project.setDesignation("Test Project");
    //     project.setResponsibleEmployeeId(1L);
    //     project.setCustomerId(1L);
    //     project.setStartDate(java.time.LocalDate.now());
    //     project.setPlannedEndDate(java.time.LocalDate.now().plusDays(30));
    //     project.setEmployeeIds(new java.util.HashSet<>(java.util.List.of(employeeId)));
    //     project.setEmployeeQualifications(new java.util.HashMap<>());
    //     project.getEmployeeQualifications().put(employeeId, "DEV");
    //     ProjectEntity savedProject = projectRepository.save(project);
    //     Long projectId = savedProject.getId();

    //     mockMvc.perform(MockMvcRequestBuilders.delete("/projects/{projectId}/employees/{employeeId}", projectId, employeeId)
    //             .contentType(MediaType.APPLICATION_JSON))
    //         .andExpect(status().isUnauthorized())
    //         .andExpect(jsonPath("$.message").value("JWT-Token ist ungültig oder fehlt."));
    // }
}
