package de.szut.lf8_starter.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("OpenAPIConfiguration Tests")
class OpenAPIConfigurationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("OpenAPI Bean wird erstellt")
    void openAPIBean_IsCreated() {
        // When
        io.swagger.v3.oas.models.OpenAPI openAPI = context.getBean(io.swagger.v3.oas.models.OpenAPI.class);

        // Then
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("HiTec Project Management API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearerAuth");
    }
}
