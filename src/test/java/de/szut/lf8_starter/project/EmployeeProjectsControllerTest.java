package de.szut.lf8_starter.project;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.dto.EmployeeProjectsDto;
import de.szut.lf8_starter.project.dto.EmployeeProjectsDto.ProjectWithRoleDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeProjectsController.class)
@WithMockUser
@DisplayName("EmployeeProjectsController Tests")
class EmployeeProjectsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    @DisplayName("GET /employees/{employeeId}/projects - Erfolgreiches Abrufen")
    void getEmployeeProjects_Success_ReturnsProjects() throws Exception {
        Long employeeId = 10L;

        EmployeeProjectsDto dto = new EmployeeProjectsDto(employeeId, List.of(
                new ProjectWithRoleDto(1L, "Projekt A", LocalDate.of(2025,1,1), LocalDate.of(2025,12,31), "Developer")
        ));

        when(projectService.getEmployeeProjects(employeeId)).thenReturn(dto);

        mockMvc.perform(get("/employees/{employeeId}/projects", employeeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.employeeId").value(10))
                .andExpect(jsonPath("$.projects[0].id").value(1))
                .andExpect(jsonPath("$.projects[0].designation").value("Projekt A"))
                .andExpect(jsonPath("$.projects[0].role").value("Developer"));
    }

    @Test
    @DisplayName("GET /employees/{employeeId}/projects - Mitarbeiter nicht gefunden -> 404")
    void getEmployeeProjects_EmployeeNotFound_Returns404() throws Exception {
        Long employeeId = 999L;
        when(projectService.getEmployeeProjects(employeeId)).thenThrow(new ResourceNotFoundException("Mitarbeiter nicht gefunden"));

        mockMvc.perform(get("/employees/{employeeId}/projects", employeeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
