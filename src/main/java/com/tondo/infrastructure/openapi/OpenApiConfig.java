package com.tondo.infrastructure.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tondoOpenApi() {
        final String scheme = "BearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Tondo API")
                        .description("情绪成长社区 — REST 接口文档（企业级：JWT、限流、审计、缓存）")
                        .version("v0.1")
                        .contact(new Contact().name("Tondo").email("dev@tondo.local")))
                .addSecurityItem(new SecurityRequirement().addList(scheme))
                .components(new Components().addSecuritySchemes(scheme,
                        new SecurityScheme()
                                .name(scheme)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
