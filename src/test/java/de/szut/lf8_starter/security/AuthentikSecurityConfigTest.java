package de.szut.lf8_starter.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "authentik.enabled=true",
    "keycloak.enabled=false",
    "authentik.jwk-set-uri=http://localhost:9000/application/o/hitec/jwks/",
    "authentik.issuer-uri=http://localhost:9000/application/o/hitec/"
})
@DisplayName("AuthentikSecurityConfig Tests")
class AuthentikSecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("SecurityFilterChain Bean wird erstellt")
    void securityFilterChainBean_IsCreated() {
        // When
        SecurityFilterChain filterChain = context.getBean("authentikFilterChain", SecurityFilterChain.class);

        // Then
        assertThat(filterChain).isNotNull();
    }

    @Test
    @DisplayName("JwtDecoder Bean wird erstellt")
    void jwtDecoderBean_IsCreated() {
        // When
        JwtDecoder jwtDecoder = context.getBean(JwtDecoder.class);

        // Then
        assertThat(jwtDecoder).isNotNull();
    }
}
