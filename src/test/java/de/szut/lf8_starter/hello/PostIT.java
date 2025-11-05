package de.szut.lf8_starter.hello;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostIT extends AbstractIntegrationTest {


    @Test
    void authorization() throws Exception {
        final String content = """
                {
                    "message": "Foo"
                }
                """;

        final var contentAsString = this.mockMvc.perform(post("/hello")
                        .content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void storeAndFind() throws Exception {
        final String content = """
                {
                    "message": "Foo"
                }
                """;

        final var contentAsString = this.mockMvc.perform(post("/hello").content(content).contentType(MediaType.APPLICATION_JSON)
                .with(jwt().jwt(jwt -> jwt
                        .claim("sub", "user123")
                        .claim("preferred_username", "john.doe")
                        .claim("realm_access", java.util.Map.of("roles", java.util.List.of("hitec-employee")))
                ).authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_hitec-employee"))))
        .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("message", is("Foo")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var id = Long.parseLong(new JSONObject(contentAsString).get("id").toString());

        final var loadedEntity = helloRepository.findById(id);

        assertThat(loadedEntity).isPresent();
        assertThat(loadedEntity.get().getId()).isEqualTo(id);
        assertThat(loadedEntity.get().getMessage()).isEqualTo("Foo");
    }
}