package de.szut.lf8_starter.project.unittest;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.ProjectController;
import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectMapper;
import de.szut.lf8_starter.project.ProjectService;
import de.szut.lf8_starter.project.dto.ProjectEmployeesDto;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@WithMockUser
@DisplayName("ProjectController Get Project Employees Tests")
class ProjectControllerGetProjectEmployeesUnitTest {

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
    @DisplayName("GET /projects/{projektId}/employees - Successful retrieval of project staff")
    void getProjectEmployees_ExistingProject_ReturnsEmployees() throws Exception {
        Long projectId = 1L;
        ProjectEmployeesDto employeesDto = new ProjectEmployeesDto(
            1L,
            "Test Projekt",
            java.util.List.of(new ProjectEmployeesDto.EmployeeWithQualificationDto(10L, "Developer"))
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
    @DisplayName("GET /projects/{projektId}/employees - Project not found - HTTP 404")
    void getProjectEmployees_ProjectNotFound_Returns404() throws Exception {
        Long nonExistentId = 999L;
        when(projectService.getProjectEmployees(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Project with the ID " + nonExistentId + " does not exist."));

        mockMvc.perform(get("/projects/{projektId}/employees", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
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
