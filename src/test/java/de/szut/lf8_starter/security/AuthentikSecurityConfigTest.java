package de.szut.lf8_starter.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("AuthentikSecurityConfig Tests")
class AuthentikSecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("SecurityFilterChain Bean wird erstellt")
    void securityFilterChainBean_IsCreated() {
        // When
        SecurityFilterChain filterChain = context.getBean(SecurityFilterChain.class);

        // Then
        assertThat(filterChain).isNotNull();
    }

    @Test
    @DisplayName("JwtDecoder Bean wird erstellt")
    void jwtDecoderBean_IsCreated() {
        // When
        org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder = context.getBean(org.springframework.security.oauth2.jwt.JwtDecoder.class);

        // Then
        assertThat(jwtDecoder).isNotNull();
    }
}
