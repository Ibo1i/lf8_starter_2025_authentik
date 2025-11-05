package de.szut.lf8_starter.project.unittest;

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
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@WithMockUser
@DisplayName("ProjectController Get All Projects Tests")
class ProjectControllerGetAllProjectsUnitTest {

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
    @DisplayName("GET /projects - Alle Projekte abrufen")
    void getAllProjects_ReturnsListOfProjects() throws Exception {
        List<ProjectEntity> projects = List.of(testProject);
        when(projectService.readAll()).thenReturn(projects);
        when(projectMapper.mapToGetDto(testProject)).thenReturn(testProjectDto);

        mockMvc.perform(get("/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].designation").value("Test Projekt"));
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
}
