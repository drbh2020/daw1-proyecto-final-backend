package com.delivery.sistema.delivery.y.gestion.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Delivery y Gestión API")
                        .version("1.0.0")
                        .description("API REST para sistema de delivery y gestión de restaurantes desarrollado con Spring Boot")
                        .contact(new Contact()
                                .name("Equipo Desarrollo DAW1")
                                .email("contacto@delivery-system.com")
                                .url("https://github.com/equipo-daw1/sistema-delivery"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desarrollo local"),
                        new Server()
                                .url("https://api.delivery-system.com")
                                .description("Servidor de producción")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token para autenticación. Formato: 'Bearer {token}'")));
    }
}