package de.szut.lf8_starter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.szut.lf8_starter.project.dto.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

public class ServiceUnavailableFilter extends OncePerRequestFilter {

    private final KeycloakHealthService keycloakHealthService;
    private final ObjectMapper objectMapper;

    public ServiceUnavailableFilter(KeycloakHealthService keycloakHealthService) {
        this.keycloakHealthService = keycloakHealthService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!keycloakHealthService.isKeycloakUp()) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

            ApiErrorResponse errorResponse = new ApiErrorResponse(
                    LocalDateTime.now(),
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Service Unavailable",
                    "Authentifizierungsservice nicht erreichbar.",
                    request.getRequestURI(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }

        filterChain.doFilter(request, response);
    }
}

