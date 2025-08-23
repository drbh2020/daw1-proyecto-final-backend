package com.delivery.sistema.delivery.y.gestion.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {

    @Value("${app.file.upload.max-file-size:5MB}")
    private String maxFileSize;

    @Value("${app.file.upload.max-request-size:10MB}")
    private String maxRequestSize;

    @Value("${app.file.upload.upload-dir:uploads}")
    private String uploadDir;

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"),
                parseSize(maxFileSize),     // max file size
                parseSize(maxRequestSize),  // max request size
                1024 * 1024                 // file size threshold (1MB)
        );
    }

    private long parseSize(String size) {
        if (size.endsWith("KB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024;
        } else if (size.endsWith("MB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024;
        } else if (size.endsWith("GB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024 * 1024;
        }
        return Long.parseLong(size);
    }

    public String getUploadDir() {
        return uploadDir;
    }
}