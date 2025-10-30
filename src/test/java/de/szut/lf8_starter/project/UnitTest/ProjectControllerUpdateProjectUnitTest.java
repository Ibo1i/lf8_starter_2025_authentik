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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@WithMockUser
@DisplayName("ProjectController Update Project Tests")
class ProjectControllerUpdateProjectUnitTest {

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

        ProjectGetDto testProjectDto = new ProjectGetDto(
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
    @DisplayName("PUT /projects/{projectId} - Erfolgreiches Aktualisieren eines Projekts")
    void updateProject_Success_ReturnsUpdatedProject() throws Exception {
        de.szut.lf8_starter.dto.UpdateProjectDTO updateDTO = new de.szut.lf8_starter.dto.UpdateProjectDTO("Updated Projekt", 60L, 200L, null, "Updated Kommentar", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 11, 30));
        ProjectEntity updatedEntity = new ProjectEntity();
        updatedEntity.setId(1L);
        updatedEntity.setDesignation("Updated Projekt");
        updatedEntity.setCustomerId(200L);
        updatedEntity.setResponsibleEmployeeId(60L);
        updatedEntity.setComment("Updated Kommentar");
        updatedEntity.setStartDate(LocalDate.of(2025, 2, 1));
        updatedEntity.setPlannedEndDate(LocalDate.of(2025, 11, 30));
        ProjectGetDto updatedDto = new ProjectGetDto(1L, "Updated Projekt", 60L, 200L, null, "Updated Kommentar", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 11, 30), null, new HashSet<>());
        when(projectService.updateFromDTO(1L, updateDTO)).thenReturn(updatedEntity);
        when(projectMapper.mapToGetDto(updatedEntity)).thenReturn(updatedDto);

        mockMvc.perform(put("/projects/{projectId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"designation\":\"Updated Projekt\",\"customerId\":200,\"responsibleEmployeeId\":60,\"comment\":\"Updated Kommentar\",\"startDate\":\"2025-02-01\",\"plannedEndDate\":\"2025-11-30\"}")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.designation").value("Updated Projekt"));
    }

    @Test
    @DisplayName("PUT /projects/{projectId} - Projekt nicht gefunden")
    void updateProject_NotFound_Returns404() throws Exception {
        de.szut.lf8_starter.dto.UpdateProjectDTO updateDTO = new de.szut.lf8_starter.dto.UpdateProjectDTO("Updated Projekt", 60L, 200L, null, "Updated Kommentar", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 11, 30));
        when(projectService.updateFromDTO(999L, updateDTO))
                .thenThrow(new ResourceNotFoundException("Projekt mit der ID 999 existiert nicht."));

        mockMvc.perform(put("/projects/{projectId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"designation\":\"Updated Projekt\",\"customerId\":200,\"responsibleEmployeeId\":60,\"comment\":\"Updated Kommentar\",\"startDate\":\"2025-02-01\",\"plannedEndDate\":\"2025-11-30\"}")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
}
