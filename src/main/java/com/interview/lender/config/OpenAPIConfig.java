package com.interview.lender.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {

    private final AuthConfig authConfig;



    @Bean
    public OpenAPI customOpenAPI() {
        StringBuilder userDescriptions = new StringBuilder("Basic Authentication\n\n**Available Users:**\n");
        for (AuthConfig.User user : authConfig.getUsers()) {
            userDescriptions.append("- **")
                    .append(user.getUsername())
                    .append("**: `")
                    .append(user.getUsername())
                    .append("` / `")
                    .append(user.getPassword())
                    .append("` (Roles: ")
                    .append(String.join(", ", user.getRoles()))
                    .append(")\n");
        }

        return new OpenAPI()
                .info(new Info()
                        .title("Lending Management Service APIs")
                        .description("Digital Lending Platform for micro-loan processing")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LMS Development Team")
                                .email("support@lendingplatform.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description(userDescriptions.toString())
                        ));
    }
}
