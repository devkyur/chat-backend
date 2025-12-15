package com.dating.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components()
                .addSecuritySchemes(jwt, new SecurityScheme()
                        .name(jwt)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .description("JWT 토큰을 입력해주세요. 'Bearer ' 접두사는 자동으로 추가됩니다.")
                );

        return new OpenAPI()
                .info(new Info()
                        .title("Dating App API")
                        .version("v1.0")
                        .description("Dating App Backend API 문서입니다.")
                        .contact(new Contact()
                                .name("Dating App Team")
                                .email("support@dating.com")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Server"),
                        new Server()
                                .url("https://your-production-domain.com")
                                .description("Production Server")
                ))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
