package de.szut.lf8_starter.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.szut.lf8_starter.dto.UpdateProjectDTO;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class ProjectUpdateTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    private ProjectEntity testProject;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();

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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void sollFehler400WerfenBeiValidierungsfehler() throws Exception {
        UpdateProjectDTO updateDTO = new UpdateProjectDTO();
        updateDTO.setDesignation("AB");  // Zu kurz!
        updateDTO.setResponsibleEmployeeId(-1L);  // Negativ!
        updateDTO.setCustomerId(1L);
        updateDTO.setStartDate(LocalDate.now());
        updateDTO.setPlannedEndDate(LocalDate.now().plusDays(30));

        mockMvc.perform(put("/projects/" + testProject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sollFehler400WerfenWennEnddatumVorStartdatum() throws Exception {
        UpdateProjectDTO updateDTO = new UpdateProjectDTO();
        updateDTO.setDesignation("Test Projekt");
        updateDTO.setResponsibleEmployeeId(1L);
        updateDTO.setCustomerId(1L);
        updateDTO.setStartDate(LocalDate.now().plusDays(30));
        updateDTO.setPlannedEndDate(LocalDate.now());  // Vor Startdatum!

        mockMvc.perform(put("/projects/" + testProject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }
}