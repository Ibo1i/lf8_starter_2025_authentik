package de.szut.lf8_starter.hello;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class GetAllIT extends AbstractIntegrationTest {


    @Test
    void authorization() throws Exception {
        this.mockMvc.perform(get("/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findAll() throws Exception {

        helloRepository.save(new HelloEntity("Foo"));
        helloRepository.save(new HelloEntity("Bar"));

        final var contentAsString = this.mockMvc.perform(get("/hello")
                        .with(jwt().jwt(jwt -> jwt
                                .claim("sub", "user123")
                                .claim("preferred_username", "john.doe")
                                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("hitec-employee")))
                        ).authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_hitec-employee"))))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].message", is("Foo")))
                .andExpect(jsonPath("$[1].message", is("Bar")));
    }

}
