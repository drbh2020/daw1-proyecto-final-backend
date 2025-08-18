-- Crear base de datos
CREATE DATABASE IF NOT EXISTS deliverydb;
USE deliverydb;

-- ==========================
-- Tabla ROL
-- ==========================
DROP TABLE IF EXISTS cliente_rol;
DROP TABLE IF EXISTS rol;

CREATE TABLE rol (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

-- Insertar roles básicos
INSERT INTO rol (nombre) VALUES ('ROL_CLIENTE');
INSERT INTO rol (nombre) VALUES ('ROL_ADMIN');
INSERT INTO rol (nombre) VALUES ('ROL_RESTAURANTE');

-- ==========================
-- Tabla CLIENTE
-- ==========================
DROP TABLE IF EXISTS cliente;

CREATE TABLE cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    direccion VARCHAR(255)
);

-- ==========================
-- Tabla intermedia CLIENTE_ROL
-- ==========================
CREATE TABLE cliente_rol (
    cliente_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (cliente_id, rol_id),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES rol(id) ON DELETE CASCADE
);

-- ==========================
-- Insertar usuarios de prueba
-- Contraseña encriptada de "123456" con BCrypt
-- ==========================
INSERT INTO cliente (nombre, email, password, direccion)
VALUES ('Cliente Demo', 'cliente@example.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Av. Cliente 123');

INSERT INTO cliente (nombre, email, password, direccion)
VALUES ('Admin Demo', 'admin@example.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Av. Admin 456');

INSERT INTO cliente (nombre, email, password, direccion)
VALUES ('Restaurante Demo', 'restaurante@example.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Av. Restaurante 789');

-- ==========================
-- Asignar roles a usuarios
-- ==========================
-- Cliente Demo → ROL_CLIENTE
INSERT INTO cliente_rol (cliente_id, rol_id) VALUES (1, 1);

-- Admin Demo → ROL_ADMIN
INSERT INTO cliente_rol (cliente_id, rol_id) VALUES (2, 2);

-- Restaurante Demo → ROL_RESTAURANTE
INSERT INTO cliente_rol (cliente_id, rol_id) VALUES (3, 3);

