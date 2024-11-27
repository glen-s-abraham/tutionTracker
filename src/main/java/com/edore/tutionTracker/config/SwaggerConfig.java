package com.edore.tutionTracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(
                new Info()
                        .title("Tution Tracker API")
                        .version("1.0")
                        .description("API for Tution Tracker"))
                .addSecurityItem(
                        new SecurityRequirement().addList("Bearer Authentication"))
                .components(
                        new Components().addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .name("Bearer Authentication").type(SecurityScheme.Type.HTTP).scheme("bearer")
                                .bearerFormat("jwt")));
    }

}
