package com.delivery.sistema.delivery.y.gestion.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    
    private final HttpStatus status;
    
    public BusinessException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }
    
    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }
    
    public BusinessException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
    
    // Métodos de fábrica para casos comunes
    public static BusinessException notFound(String message) {
        return new BusinessException(message, HttpStatus.NOT_FOUND);
    }
    
    public static BusinessException conflict(String message) {
        return new BusinessException(message, HttpStatus.CONFLICT);
    }
    
    public static BusinessException unauthorized(String message) {
        return new BusinessException(message, HttpStatus.UNAUTHORIZED);
    }
    
    public static BusinessException forbidden(String message) {
        return new BusinessException(message, HttpStatus.FORBIDDEN);
    }
}