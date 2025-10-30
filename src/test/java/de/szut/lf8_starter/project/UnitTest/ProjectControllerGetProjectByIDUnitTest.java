package de.szut.lf8_starter.project.UnitTest;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.ProjectController;
import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectMapper;
import de.szut.lf8_starter.project.ProjectService;
import de.szut.lf8_starter.project.dto.ProjectGetDto;
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

import java.time.LocalDate;
import java.util.HashSet;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@WithMockUser
@DisplayName("ProjectController Get Project By ID Tests")
class ProjectControllerGetProjectByIDUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ProjectMapper projectMapper;

    @MockBean
    private EmployeeService employeeService;

    private ProjectEntity testProject;
    private ProjectGetDto testProjectDto;

    @BeforeEach
    void setUp() {
        testProject = new ProjectEntity();
        testProject.setId(1L);
        testProject.setDesignation("Test Projekt");
        testProject.setCustomerId(100L);
        testProject.setResponsibleEmployeeId(50L);
        testProject.setComment("Test Kommentar");
        testProject.setStartDate(LocalDate.of(2025, 1, 1));
        testProject.setPlannedEndDate(LocalDate.of(2025, 12, 31));

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
        Long projectId = 1L;
        when(projectService.readById(projectId)).thenReturn(testProject);
        when(projectMapper.mapToGetDto(testProject)).thenReturn(testProjectDto);

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
        Long nonExistentId = 999L;
        when(projectService.readById(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Projekt mit der ID " + nonExistentId + " existiert nicht."));

        mockMvc.perform(get("/projects/{projectId}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /projects/{projectId} - Ungültige ID (negative Zahl)")
    void getProjectById_InvalidNegativeId_ReturnsNotFound() throws Exception {
        Long invalidId = -1L;
        when(projectService.readById(invalidId))
                .thenThrow(new ResourceNotFoundException("Projekt mit der ID " + invalidId + " existiert nicht."));

        mockMvc.perform(get("/projects/{projectId}", invalidId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /projects/{projectId} - ID = 0")
    void getProjectById_ZeroId_ReturnsNotFound() throws Exception {
        Long zeroId = 0L;
        when(projectService.readById(zeroId))
                .thenThrow(new ResourceNotFoundException("Projekt mit der ID " + zeroId + " existiert nicht."));

        mockMvc.perform(get("/projects/{projectId}", zeroId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
}
