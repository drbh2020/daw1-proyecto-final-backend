package com.delivery.sistema.delivery.y.gestion.shared.util;

import com.delivery.sistema.delivery.y.gestion.shared.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileUploadUtil {

    @Value("${app.file.upload.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.file.upload.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);
        
        try {
            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir, folder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único para el archivo
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID() + "." + fileExtension;

            // Guardar archivo
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Archivo subido exitosamente: {}", filePath);

            // Retornar URL pública del archivo
            return baseUrl + "/uploads/" + folder + "/" + uniqueFilename;

        } catch (IOException e) {
            log.error("Error al subir archivo: ", e);
            throw new BusinessException("Error al guardar el archivo");
        }
    }

    public boolean deleteFile(String fileUrl) {
        try {
            // Extraer la ruta del archivo de la URL
            String relativePath = fileUrl.replace(baseUrl, "");
            Path filePath = Paths.get(uploadDir).resolve(relativePath.substring(1)); // Quitar el '/' inicial

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Archivo eliminado exitosamente: {}", filePath);
                return true;
            } else {
                log.warn("Archivo no encontrado para eliminar: {}", filePath);
                return false;
            }
        } catch (IOException e) {
            log.error("Error al eliminar archivo: ", e);
            return false;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("El archivo está vacío");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("El archivo excede el tamaño máximo permitido (5MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("Tipo de archivo no permitido. Solo se permiten: " + 
                String.join(", ", ALLOWED_IMAGE_TYPES));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException("Nombre de archivo inválido");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new BusinessException("Extensión de archivo no permitida. Solo se permiten: " + 
                String.join(", ", ALLOWED_IMAGE_EXTENSIONS));
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        
        return filename.substring(lastDotIndex + 1);
    }
}