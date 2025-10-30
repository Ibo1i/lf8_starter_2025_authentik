package de.szut.lf8_starter.project;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.dto.ProjectGetDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@WithMockUser
@DisplayName("ProjectController Tests")
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ProjectMapper projectMapper;

    @MockBean
    private de.szut.lf8_starter.project.service.EmployeeService employeeService;

    private ProjectEntity testProject;
    private ProjectGetDto testProjectDto;

    @BeforeEach
    void setUp() {
        // Test-Projekt erstellen
        testProject = new ProjectEntity();
        testProject.setId(1L);
        testProject.setDesignation("Test Projekt");
        testProject.setCustomerId(100L);
        testProject.setResponsibleEmployeeId(50L);
        testProject.setComment("Test Kommentar");
        testProject.setStartDate(LocalDate.of(2025, 1, 1));
        testProject.setPlannedEndDate(LocalDate.of(2025, 12, 31));

        // Test-DTO erstellen
        testProjectDto = new ProjectGetDto(
            1L,
            "Test Projekt",
            50L,
            100L,
            null,
            "Test Kommentar",
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 12, 31),
            null,
            new HashSet<>()
        );
    }

    @Test
    @DisplayName("GET /projects/{projectId} - Erfolgreiches Abrufen eines existierenden Projekts")
    void getProjectById_ExistingProject_ReturnsProject() throws Exception {
        // Given
        Long projectId = 1L;
        when(projectService.readById(projectId)).thenReturn(testProject);
        when(projectMapper.mapToGetDto(testProject)).thenReturn(testProjectDto);

        // When & Then
        mockMvc.perform(get("/projects/{projectId}", projectId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.designation").value("Test Projekt"))
                .andExpect(jsonPath("$.customerId").value(100L))
                .andExpect(jsonPath("$.responsibleEmployeeId").value(50L))
                .andExpect(jsonPath("$.comment").value("Test Kommentar"))
                .andExpect(jsonPath("$.startDate").value("2025-01-01"))
                .andExpect(jsonPath("$.plannedEndDate").value("2025-12-31"));
    }

    @Test
    @DisplayName("GET /projects/{projectId} - Projekt nicht gefunden - HTTP 404")
    void getProjectById_ProjectNotFound_Returns404() throws Exception {
        // Given
        Long nonExistentId = 999L;
        when(projectService.readById(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Projekt mit der ID " + nonExistentId + " existiert nicht."));

        // When & Then
        mockMvc.perform(get("/projects/{projectId}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /projects/{projectId} - Ungültige ID (negative Zahl)")
    void getProjectById_InvalidNegativeId_ReturnsNotFound() throws Exception {
        // Given
        Long invalidId = -1L;
        when(projectService.readById(invalidId))
                .thenThrow(new ResourceNotFoundException("Projekt mit der ID " + invalidId + " existiert nicht."));

        // When & Then
        mockMvc.perform(get("/projects/{projectId}", invalidId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /projects/{projectId} - ID = 0")
    void getProjectById_ZeroId_ReturnsNotFound() throws Exception {
        // Given
        Long zeroId = 0L;
        when(projectService.readById(zeroId))
                .thenThrow(new ResourceNotFoundException("Projekt mit der ID " + zeroId + " existiert nicht."));

        // When & Then
        mockMvc.perform(get("/projects/{projectId}", zeroId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /projects - Alle Projekte abrufen")
    void getAllProjects_ReturnsListOfProjects() throws Exception {
        // Given
        List<ProjectEntity> projects = List.of(testProject);
        when(projectService.readAll()).thenReturn(projects);
        when(projectMapper.mapToGetDto(testProject)).thenReturn(testProjectDto);

        // When & Then
        mockMvc.perform(get("/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].designation").value("Test Projekt"));
    }

    @Test
    @DisplayName("DELETE /projects/{projectId} - Erfolgreiches Löschen (204)")
    void deleteProject_ExistingProject_ReturnsNoContent() throws Exception {
        Long projectId = 1L;
        doNothing().when(projectService).deleteById(projectId);

        mockMvc.perform(delete("/projects/{projectId}", projectId).with(csrf()).with(user("test").roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /projects/{projectId} - Projekt nicht gefunden (404)")
    void deleteProject_NotFound_ReturnsNotFound() throws Exception {
        Long nonExistentId = 999L;
        doThrow(new ResourceNotFoundException("Projekt mit der ID " + nonExistentId + " existiert nicht."))
                .when(projectService).deleteById(nonExistentId);

        mockMvc.perform(delete("/projects/{projectId}", nonExistentId).with(csrf()).with(user("test").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /projects/{projectId} - Konflikt bei Mitarbeiterzuordnungen (409)")
    void deleteProject_Conflict_ReturnsConflict() throws Exception {
        Long id = 2L;
        doThrow(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT))
                .when(projectService).deleteById(id);

        mockMvc.perform(delete("/projects/{projectId}", id).with(csrf()).with(user("test").roles("USER")))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /projects/{projektId}/employees - Erfolgreiches Abrufen der Projektmitarbeiter")
    void getProjectEmployees_ExistingProject_ReturnsEmployees() throws Exception {
        Long projectId = 1L;
        // DTO vorbereiten
        de.szut.lf8_starter.project.dto.ProjectEmployeesDto employeesDto = new de.szut.lf8_starter.project.dto.ProjectEmployeesDto(
            1L,
            "Test Projekt",
            java.util.List.of(new de.szut.lf8_starter.project.dto.ProjectEmployeesDto.EmployeeWithQualificationDto(10L, "Developer"))
        );

        when(projectService.getProjectEmployees(projectId)).thenReturn(employeesDto);

        mockMvc.perform(get("/projects/{projektId}/employees", projectId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.designation").value("Test Projekt"))
                .andExpect(jsonPath("$.employees[0].employeeId").value(10))
                .andExpect(jsonPath("$.employees[0].qualification").value("Developer"));
    }

    @Test
    @DisplayName("GET /projects/{projektId}/employees - Projekt nicht gefunden - HTTP 404")
    void getProjectEmployees_ProjectNotFound_Returns404() throws Exception {
        Long nonExistentId = 999L;
        when(projectService.getProjectEmployees(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Projekt mit der ID " + nonExistentId + " existiert nicht."));

        mockMvc.perform(get("/projects/{projektId}/employees", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
