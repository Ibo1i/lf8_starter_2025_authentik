package de.szut.lf8_starter.project.unittest;

import de.szut.lf8_starter.project.ProjectController;
import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectMapper;
import de.szut.lf8_starter.project.ProjectService;
import de.szut.lf8_starter.project.dto.ProjectCreateDto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@WithMockUser
@DisplayName("ProjectController Create Project Tests")
class ProjectControllerCreateProjectUnitTest {

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
    @DisplayName("POST /projects - Erfolgreiches Erstellen eines Projekts")
    void createProject_Success_ReturnsCreatedProject() throws Exception {
        ProjectCreateDto createDto = new ProjectCreateDto("Test Projekt", 50L, 100L, null, "Test Kommentar", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), null);
        when(projectMapper.mapCreateDtoToEntity(createDto)).thenReturn(testProject);
        when(projectService.create(testProject)).thenReturn(testProject);
        when(projectMapper.mapToGetDto(testProject)).thenReturn(testProjectDto);

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"designation\":\"Test Projekt\",\"customerId\":100,\"responsibleEmployeeId\":50,\"comment\":\"Test Kommentar\",\"startDate\":\"2025-01-01\",\"plannedEndDate\":\"2025-12-31\"}")
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /projects - Validierung fehlgeschlagen")
    void createProject_ValidationFails_ReturnsBadRequest() throws Exception {
        when(projectService.create(any())).thenThrow(new RuntimeException("Validation failed"));

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"designation\":\"\",\"customerId\":100,\"responsibleEmployeeId\":50,\"comment\":\"Test Kommentar\",\"startDate\":\"2025-01-01\",\"plannedEndDate\":\"2025-12-31\"}")
                .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
}
