package de.szut.lf8_starter.hello;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public class GetByMessageIT extends AbstractIntegrationTest {


    @Test
    void authorization() throws Exception {
        this.mockMvc.perform(get("/hello/findByMessage?message=Foo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void happyPath() throws Exception {
        helloRepository.save(new HelloEntity("Foo"));
        helloRepository.save(new HelloEntity("Bar"));
        helloRepository.save(new HelloEntity("Foo"));
        helloRepository.save(new HelloEntity("FooBar"));
        helloRepository.save(new HelloEntity("Foo"));

        final var content = this.mockMvc.perform(get("/hello/findByMessage?message=Foo")
                        .with(jwt().jwt(jwt -> jwt
                                .claim("sub", "user123")
                                .claim("preferred_username", "john.doe")
                                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("hitec-employee")))
                        ).authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_hitec-employee"))))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].message", is("Foo")))
                .andExpect(jsonPath("$[1].message", is("Foo")))
                .andExpect(jsonPath("$[2].message", is("Foo")));
    }

    @Test
    void messageDoesntExists() throws Exception {
        helloRepository.save(new HelloEntity("Foo"));

        final var content = this.mockMvc.perform(get("/hello/findByMessage?message=Bar")
                        .with(jwt().jwt(jwt -> jwt
                                .claim("sub", "user123")
                                .claim("preferred_username", "john.doe")
                                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("hitec-employee")))
                        ).authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_hitec-employee"))))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(0)));


    }


}
