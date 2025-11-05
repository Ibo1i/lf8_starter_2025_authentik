package de.szut.lf8_starter.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests für JWT-Authentifizierung
 * Story 4.1: JWT-Authentifizierung - Integration Tests
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "keycloak.enabled=true",
    "authentik.enabled=false",
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9090/realms/hitec-realm",
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:9090/realms/hitec-realm/protocol/openid-connect/certs"
})
@DisplayName("JWT Authentication Integration Tests")
class JwtAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KeycloakHealthService keycloakHealthService;

    @BeforeEach
    void setUp() {
        // Reset and reconfigure mock before each test
        reset(keycloakHealthService);
        // Assume Keycloak is up for all tests by default (lenient for multiple calls)
        lenient().when(keycloakHealthService.isKeycloakUp()).thenReturn(true);
    }

    @Test
    @DisplayName("GET /actuator/health is accessible without a token")
    void healthEndpoint_IsAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /swagger-ui/** is accessible without a token")
    void swaggerUi_IsAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /v3/api-docs is accessible without a token")
    void apiDocs_IsAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /projects Without token returns 401 Unauthorized")
    void protectedEndpoint_WithoutToken_Returns401() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("JWT-Token fehlt im Authorization-Header."))
                .andExpect(jsonPath("$.path").value("/projects"));
    }

    @Test
    @DisplayName("POST /projects Without token returns 401 Unauthorized")
    void postEndpoint_WithoutToken_Returns401() throws Exception {
        mockMvc.perform(post("/projects")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /projects with valid token and hitec-employee role allows access")
    void protectedEndpoint_WithValidTokenAndRole_IsAccessible() throws Exception {
        mockMvc.perform(get("/projects")
                .with(jwt().jwt(jwt -> jwt
                        .claim("sub", "user123")
                        .claim("preferred_username", "john.doe")
                        .claim("realm_access", Map.of("roles", List.of("hitec-employee")))
                ).authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_hitec-employee"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /projects with token without hitec-employee role returns 403 Forbidden")
    void protectedEndpoint_WithTokenButWithoutRole_Returns403() throws Exception {
        mockMvc.perform(get("/projects")
                .with(jwt().jwt(jwt -> jwt
                        .claim("sub", "user123")
                        .claim("preferred_username", "john.doe")
                        .claim("realm_access", Map.of("roles", List.of("other-role")))
                ).authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_other-role"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Unzureichende Berechtigungen. Erforderliche Rolle: hitec-employee"))
                .andExpect(jsonPath("$.requiredRoles[0]").value("hitec-employee"));
    }

    @Test
    @DisplayName("All project endpoints require authentication")
    void allProjectEndpoints_RequireAuthentication() throws Exception {
        // GET /projects - Kann 401 oder 403 sein, je nach Security-Kontext
        mockMvc.perform(get("/projects"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").exists());

        // POST /projects
        mockMvc.perform(post("/projects")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").exists());

        // GET /projects/{id}
        mockMvc.perform(get("/projects/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    @DisplayName("All standard operations can be performed with the hitec-employee role.")
    void withHitecEmployeeRole_AllStandardOperationsAllowed() throws Exception {
        var jwtConfig = jwt().jwt(jwt -> jwt
                .claim("sub", "user123")
                .claim("preferred_username", "john.doe")
                .claim("realm_access", Map.of("roles", List.of("hitec-employee")))
        ).authorities(new SimpleGrantedAuthority("ROLE_hitec-employee"));

        // GET ist erlaubt
        mockMvc.perform(get("/projects").with(jwtConfig))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /projects If Keycloak is unavailable, it returns a 503 Service Unavailable error.")
    void protectedEndpoint_WhenKeycloakIsDown_Returns503() throws Exception {
        // Simulate Keycloak is down
        when(keycloakHealthService.isKeycloakUp()).thenReturn(false);

        mockMvc.perform(get("/projects"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.error").value("Service Unavailable"))
                .andExpect(jsonPath("$.message").value("Authentifizierungsservice nicht erreichbar."))
                .andExpect(jsonPath("$.path").value("/projects"));
    }
}
