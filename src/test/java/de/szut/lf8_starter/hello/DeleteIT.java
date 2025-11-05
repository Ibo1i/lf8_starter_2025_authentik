package de.szut.lf8_starter.hello;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public class DeleteIT extends AbstractIntegrationTest {


    @Test
    void authorization() throws Exception {
        HelloEntity stored = helloRepository.save(new HelloEntity("Foo"));
        this.mockMvc.perform(delete("/hello/" + stored.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void happyPath() throws Exception {
        HelloEntity stored = helloRepository.save(new HelloEntity("Foo"));

        final var content = this.mockMvc.perform(
                        delete("/hello/" + stored.getId())
                                .with(jwt().jwt(jwt -> jwt
                                        .claim("sub", "user123")
                                        .claim("preferred_username", "john.doe")
                                        .claim("realm_access", java.util.Map.of("roles", java.util.List.of("hitec-employee")))
                                ).authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_hitec-employee"))))
                .andExpect(status().isNoContent());
        assertThat(helloRepository.findById(stored.getId()).isPresent()).isFalse();
    }


    @Test
    void idDoesNotExist() throws Exception {
        final var contentAsString = this.mockMvc.perform(delete("/hello/5")
                .with(jwt().jwt(jwt -> jwt
                        .claim("sub", "user123")
                        .claim("preferred_username", "john.doe")
                        .claim("realm_access", java.util.Map.of("roles", java.util.List.of("hitec-employee")))
                ).authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_hitec-employee"))))
                .andExpect(content().string(containsString("HelloEntity not found on id = 5")))
                .andExpect(status().isNotFound());
    }


}
