package com.delivery.sistema.delivery.y.gestion.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir orígenes específicos (en producción, cambiar por dominios específicos)
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:4200",  // Angular dev server
            "http://localhost:3000",  // React dev server
            "http://127.0.0.1:*",     // Local development
            "https://*.vercel.app",   // Vercel deployments
            "https://*.netlify.app"   // Netlify deployments
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "authorization", "content-type", "x-auth-token", "x-requested-with",
            "accept", "origin", "access-control-request-method", 
            "access-control-request-headers"
        ));
        
        // Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList(
            "x-auth-token", "authorization", "access-control-allow-origin",
            "access-control-allow-credentials"
        ));
        
        // Permitir credenciales (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Tiempo de cache para preflight requests (en segundos)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
}