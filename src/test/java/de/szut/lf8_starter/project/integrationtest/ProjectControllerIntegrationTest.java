package de.szut.lf8_starter.project.integrationtest;

import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectRepository;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class ProjectControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
    }

    private RequestPostProcessor createJwt() {
        return jwt().jwt(jwt -> jwt
                .claim("sub", "user123")
                .claim("preferred_username", "john.doe")
                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("hitec-employee")))
        ).authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_hitec-employee"));
    }

    @Test
    void getAllProjects_whenNoProjects_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/projects")
                        .with(createJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllProjects_whenProjectsExist_returnsListOfProjects() throws Exception {
        // Arrange: create a project entity and save
        ProjectEntity p = new ProjectEntity();
        p.setDesignation("Integration Test Projekt");
        p.setCustomerId(123L);
        p.setResponsibleEmployeeId(321L);
        p.setCustomerContactPerson("Max Mustermann");
        p.setComment("Integration Test");
        p.setStartDate(LocalDate.of(2025, 1, 1));
        p.setPlannedEndDate(LocalDate.of(2025, 12, 31));

        projectRepository.save(p);

        mockMvc.perform(get("/projects")
                        .with(createJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].designation", is("Integration Test Projekt")))
                .andExpect(jsonPath("$[0].customerId", is(123)))
                .andExpect(jsonPath("$[0].responsibleEmployeeId", is(321)));
    }
}
