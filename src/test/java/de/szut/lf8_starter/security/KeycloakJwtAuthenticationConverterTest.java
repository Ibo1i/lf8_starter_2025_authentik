package de.szut.lf8_starter.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit Tests for KeycloakJwtAuthenticationConverter
 */
@DisplayName("KeycloakJwtAuthenticationConverter Tests")
class KeycloakJwtAuthenticationConverterTest {

    private KeycloakJwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        converter = new KeycloakJwtAuthenticationConverter();
    }

    @Test
    @DisplayName("Extracts the hitec-employee role correctly from realm_access")
    void whenJwtContainsHitecEmployeeRole_thenExtractsCorrectly() {
        // Given
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("hitec-employee", "offline_access"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("preferred_username", "john.doe");
        claims.put("realm_access", realmAccess);

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authToken = converter.convert(jwt);

        // Then
        assertThat(authToken).isNotNull();
        assertThat(authToken.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_hitec-employee", "ROLE_offline_access");
    }

    @Test
    @DisplayName("Returns empty authorities if realm_access is missing")
    void whenRealmAccessMissing_thenReturnsEmptyAuthorities() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("preferred_username", "john.doe");

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authToken = converter.convert(jwt);

        // Then
        assertThat(authToken).isNotNull();
        assertThat(authToken.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Returns empty authorities if roles is missing")
    void whenRolesMissing_thenReturnsEmptyAuthorities() {
        // Given
        Map<String, Object> realmAccess = new HashMap<>();
        // roles key missing

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("realm_access", realmAccess);

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authToken = converter.convert(jwt);

        // Then
        assertThat(authToken).isNotNull();
        assertThat(authToken.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Converts all roles with the ROLE_ prefix")
    void whenMultipleRoles_thenConvertsAllWithRolePrefix() {
        // Given
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("hitec-employee", "admin", "user"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("realm_access", realmAccess);

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authToken = converter.convert(jwt);

        // Then
        assertThat(authToken.getAuthorities())
                .hasSize(3)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_hitec-employee", "ROLE_admin", "ROLE_user");
    }

    @Test
    @DisplayName("JWT Principal wird korrekt gesetzt")
    void whenConvert_thenJwtPrincipalIsSet() {
        // Given
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("hitec-employee"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("preferred_username", "john.doe");
        claims.put("realm_access", realmAccess);

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authToken = converter.convert(jwt);

        // Then
        assertThat(authToken).isNotNull();
        assertThat(authToken.getName()).isEqualTo("user123");
    }

    private Jwt createJwt(Map<String, Object> claims) {
        return new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                claims
        );
    }
}

