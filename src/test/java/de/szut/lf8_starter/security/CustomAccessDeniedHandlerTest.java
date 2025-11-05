package de.szut.lf8_starter.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for CustomAccessDeniedHandler
 * Story 4.1: JWT-Authentifizierung - 403 Forbidden Tests
 */
@DisplayName("CustomAccessDeniedHandler Tests")
class CustomAccessDeniedHandlerTest {

    private CustomAccessDeniedHandler accessDeniedHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AccessDeniedException accessDeniedException;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        accessDeniedHandler = new CustomAccessDeniedHandler();

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(printWriter);
        when(request.getRequestURI()).thenReturn("/projects");

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Missing role - returns correct 403 response")
    void whenRoleMissing_thenReturns403WithRoleInfo() throws Exception {
        // Given
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_other-role")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // When
        accessDeniedHandler.handle(request, response, accessDeniedException);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json;charset=UTF-8");

        printWriter.flush();
        String jsonResponse = stringWriter.toString();

        assertThat(jsonResponse).contains("\"status\":403");
        assertThat(jsonResponse).contains("\"error\":\"Forbidden\"");
        assertThat(jsonResponse).contains("Unzureichende Berechtigungen. Erforderliche Rolle: hitec-employee");
        assertThat(jsonResponse).contains("\"requiredRoles\":[\"hitec-employee\"]");
        assertThat(jsonResponse).contains("\"userRoles\":[\"other-role\"]");
    }

    @Test
    @DisplayName("No authentication - returns empty userRoles")
    void whenNoAuthentication_thenReturnsEmptyUserRoles() throws Exception {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        accessDeniedHandler.handle(request, response, accessDeniedException);

        // Then
        printWriter.flush();
        String jsonResponse = stringWriter.toString();

        assertThat(jsonResponse).contains("\"status\":403");
        assertThat(jsonResponse).contains("\"userRoles\":[]");
        assertThat(jsonResponse).contains("\"requiredRoles\":[\"hitec-employee\"]");
    }

    @Test
    @DisplayName("Multiple roles - extracts all userRoles correctly")
    void whenMultipleRoles_thenExtractsAllUserRoles() throws Exception {
        // Given
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_user"),
                new SimpleGrantedAuthority("ROLE_admin")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // When
        accessDeniedHandler.handle(request, response, accessDeniedException);

        // Then
        printWriter.flush();
        String jsonResponse = stringWriter.toString();

        assertThat(jsonResponse).contains("\"userRoles\":[\"user\",\"admin\"]");
    }
}

