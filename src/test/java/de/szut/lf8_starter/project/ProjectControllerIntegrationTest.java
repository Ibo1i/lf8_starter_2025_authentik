package de.szut.lf8_starter.project;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ProjectController Integration Tests")
class ProjectControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    private ProjectEntity testProject;

    @BeforeEach
    void setUp() {
        // Alle Projekte löschen
        projectRepository.deleteAll();

        // Test-Projekt in der Datenbank erstellen
        testProject = new ProjectEntity();
        testProject.setDesignation("Integration Test Projekt");
        testProject.setCustomerId(200L);
        testProject.setResponsibleEmployeeId(75L);
        testProject.setComment("Integration Test Kommentar");
        testProject.setStartDate(LocalDate.of(2025, 2, 1));
        testProject.setPlannedEndDate(LocalDate.of(2025, 11, 30));
        testProject = projectRepository.save(testProject);
    }

    @Test
    @DisplayName("GET /projects/{id} - Integration Test - Erfolgreiches Abrufen")
    void getProjectById_IntegrationTest_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/projects/{id}", testProject.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testProject.getId()))
                .andExpect(jsonPath("$.designation").value("Integration Test Projekt"))
                .andExpect(jsonPath("$.customerId").value(200L))
                .andExpect(jsonPath("$.responsibleEmployeeId").value(75L))
                .andExpect(jsonPath("$.comment").value("Integration Test Kommentar"))
                .andExpect(jsonPath("$.startDate").value("2025-02-01"))
                .andExpect(jsonPath("$.plannedEndDate").value("2025-11-30"));
    }

    @Test
    @DisplayName("GET /projects/{id} - Integration Test - Projekt nicht gefunden")
    void getProjectById_IntegrationTest_NotFound() throws Exception {
        // Given
        Long nonExistentId = 99999L;

        // When & Then
        mockMvc.perform(get("/projects/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /projects/{id} - Integration Test - Mehrere Projekte, spezifisches abrufen")
    void getProjectById_IntegrationTest_MultipleProjects() throws Exception {
        // Given - Zusätzliches Projekt erstellen
        ProjectEntity secondProject = new ProjectEntity();
        secondProject.setDesignation("Zweites Test Projekt");
        secondProject.setCustomerId(300L);
        secondProject.setResponsibleEmployeeId(85L);
        secondProject.setComment("Zweites Test Kommentar");
        secondProject.setStartDate(LocalDate.of(2025, 3, 1));
        secondProject.setPlannedEndDate(LocalDate.of(2025, 10, 31));
        secondProject = projectRepository.save(secondProject);

        // When & Then - Erstes Projekt abrufen
        mockMvc.perform(get("/projects/{id}", testProject.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProject.getId()))
                .andExpect(jsonPath("$.designation").value("Integration Test Projekt"));

        // When & Then - Zweites Projekt abrufen
        mockMvc.perform(get("/projects/{id}", secondProject.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(secondProject.getId()))
                .andExpect(jsonPath("$.designation").value("Zweites Test Projekt"));
    }
}
