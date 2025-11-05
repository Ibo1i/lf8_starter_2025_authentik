package de.szut.lf8_starter.config;



import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.ServletContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenAPIConfiguration {

    private final ServletContext context;

    public OpenAPIConfiguration(ServletContext context) {
        this.context = context;
    }


    @Bean
    public OpenAPI springShopOpenAPI(
    ) {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addServersItem(new Server().url(this.context.getContextPath()))
                .info(new Info()
                        .title("LF8 project starter")
                        .description("""
                                
                                ## Auth
                                
                                ## Authentication
                                
                                This Hello service uses JWTs to authenticate requests. You will receive a bearer token by making a POST-Request in IntelliJ on:
                                
                                
                                ```
                                POST http://keycloak.szut.dev/auth/realms/szut/protocol/openid-connect/token
                                Content-Type: application/x-www-form-urlencoded
                                grant_type=password&client_id=employee-management-service&username=user&password=test
                                ```
                                
                                
                                or by CURL
                                ```
                                curl -X POST 'http://keycloak.szut.dev/auth/realms/szut/protocol/openid-connect/token'
                                --header 'Content-Type: application/x-www-form-urlencoded'
                                --data-urlencode 'grant_type=password'
                                --data-urlencode 'client_id=employee-management-service'
                                --data-urlencode 'username=user'
                                --data-urlencode 'password=test'
                                ```
                                
                                To get a bearer-token in Postman, you have to follow the instructions in\s
                                 [Postman-Documentation](https://documenter.getpostman.com/view/7294517/SzmfZHnd).""")

                        .version("0.1"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }


}