package de.szut.lf8_starter.project.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.szut.lf8_starter.dto.UpdateProjectDTO;
import de.szut.lf8_starter.integration.employee.EmployeeValidationService;
import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectRepository;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class ProjectUpdateIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @MockBean
    private EmployeeValidationService employeeValidationService;

    private ProjectEntity testProject;

    private RequestPostProcessor createJwt() {
        return jwt().jwt(jwt -> jwt
                .claim("sub", "user123")
                .claim("preferred_username", "john.doe")
                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("hitec-employee")))
        ).authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_hitec-employee"));
    }

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();

        // Mock Employee Service - alle Employee IDs sind valid
        when(employeeValidationService.validateEmployee(anyLong())).thenReturn(true);

        testProject = new ProjectEntity(
                "Altes Projekt",
                1L,
                1L,
                "Alte Person",
                "Alter Kommentar",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(30)
        );
        testProject = projectRepository.save(testProject);
    }

    @Test
    void sollProjektErfolgreichAktualisieren() throws Exception {
        UpdateProjectDTO updateDTO = new UpdateProjectDTO();
        updateDTO.setDesignation("Neues Projekt");
        updateDTO.setResponsibleEmployeeId(2L);
        updateDTO.setCustomerId(2L);
        updateDTO.setCustomerContactPerson("Neue Person");
        updateDTO.setComment("Neuer Kommentar");
        updateDTO.setStartDate(LocalDate.now().plusDays(2));
        updateDTO.setPlannedEndDate(LocalDate.now().plusDays(60));

        mockMvc.perform(put("/projects/" + testProject.getId())
                        .with(createJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProject.getId()))
                .andExpect(jsonPath("$.designation").value("Neues Projekt"))
                .andExpect(jsonPath("$.responsibleEmployeeId").value(2))
                .andExpect(jsonPath("$.customerContactPerson").value("Neue Person"));
    }

    @Test
    void sollFehler404WerfenWennProjektNichtExistiert() throws Exception {
        UpdateProjectDTO updateDTO = new UpdateProjectDTO();
        updateDTO.setDesignation("Test");
        updateDTO.setResponsibleEmployeeId(1L);
        updateDTO.setCustomerId(1L);
        updateDTO.setStartDate(LocalDate.now());
        updateDTO.setPlannedEndDate(LocalDate.now().plusDays(30));

        mockMvc.perform(put("/projects/99999")
                        .with(createJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void sollFehler400WerfenBeiValidierungsfehler() throws Exception {
        UpdateProjectDTO updateDTO = new UpdateProjectDTO();
        updateDTO.setDesignation("");  // Ungültig: leere Bezeichnung
        updateDTO.setResponsibleEmployeeId(1L);
        updateDTO.setCustomerId(1L);
        updateDTO.setStartDate(LocalDate.now().plusDays(30));  // Enddatum vor Startdatum!
        updateDTO.setPlannedEndDate(LocalDate.now());

        mockMvc.perform(put("/projects/" + testProject.getId())
                        .with(createJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }
}