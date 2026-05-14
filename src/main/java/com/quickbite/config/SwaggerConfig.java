package com.quickbite.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // ── API Info ──────────────────────────────────────────
                .info(new Info()
                        .title("QuickBite API")
                        .description("Mini Food Ordering App — REST API Documentation")
                        .version("1.0.0"))

                // ── Security Scheme ───────────────────────────────────
                // Tells Swagger UI to show an Authorize button
                // where user can enter email + password (Basic Auth)
                // This populates authentication.getName() in controllers
                .addSecurityItem(new SecurityRequirement()
                        .addList("basicAuth"))
                .components(new Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .name("basicAuth")
                                        .type(Type.HTTP)
                                        .scheme("basic")
                                        // ✅ Basic Auth — enter email + password
                                        // In Step 8 we change this to Bearer JWT
                                        .description("Enter your email and password")));
    }
}
