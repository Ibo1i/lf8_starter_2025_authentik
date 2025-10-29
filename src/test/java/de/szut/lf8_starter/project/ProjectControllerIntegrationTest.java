package de.szut.lf8_starter.project;

import de.szut.lf8_starter.Lf8StarterApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Lf8StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
    }

    @Test
    void getAllProjects_whenNoProjects_returnsEmptyList() {
        String url = "http://localhost:" + port + "/projects";

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getAllProjects_whenProjectsExist_returnsListOfProjects() {
        // Arrange: create a project entity and save
        ProjectEntity p = new ProjectEntity();
        p.setDesignation("Integration Test Projekt");
        p.setCustomerId(123L);
        p.setResponsibleEmployeeId(321L);
        p.setCustomerContactPerson("Max Mustermann");
        p.setComment("Integration Test");
        p.setStartDate(LocalDate.of(2025,1,1));
        p.setPlannedEndDate(LocalDate.of(2025,12,31));

        projectRepository.save(p);

        String url = "http://localhost:" + port + "/projects";

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(1);

        Map<?,?> map = response.getBody().get(0);
        assertThat(map.get("designation")).isEqualTo("Integration Test Projekt");
        // Jackson deserializes numbers to Integer by default when values fit
        assertThat(((Number) map.get("customerId")).longValue()).isEqualTo(123L);
        assertThat(((Number) map.get("responsibleEmployeeId")).longValue()).isEqualTo(321L);
    }
}
