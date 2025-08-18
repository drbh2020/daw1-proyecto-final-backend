CREATE DATABASE IF NOT EXISTS deliverydb;
USE deliverydb;

-- CREAR TABLAS PRINCIPALES
DROP TABLE IF EXISTS cliente_rol;
DROP TABLE IF EXISTS rol;

CREATE TABLE rol (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

INSERT INTO rol (nombre) VALUES 
('ROL_CLIENTE'), ('ROL_ADMIN'), ('ROL_RESTAURANTE'), ('ROL_REPARTIDOR');

DROP TABLE IF EXISTS cliente;
CREATE TABLE cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    direccion VARCHAR(255),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cliente_rol (
    cliente_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (cliente_id, rol_id),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES rol(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS categoria;
CREATE TABLE categoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    orden_mostrar INT DEFAULT 0
);

DROP TABLE IF EXISTS restaurante;
CREATE TABLE restaurante (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    activo BOOLEAN DEFAULT TRUE,
    hora_apertura TIME NOT NULL,
    hora_cierre TIME NOT NULL,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS menu;
CREATE TABLE menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(8,2) NOT NULL CHECK (precio > 0),
    imagen_url VARCHAR(500),
    categoria_id BIGINT NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    restaurante_id BIGINT NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurante_id) REFERENCES restaurante(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id) ON DELETE RESTRICT
);

DROP TABLE IF EXISTS pedido;
CREATE TABLE pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    restaurante_id BIGINT NOT NULL,
    fecha_pedido DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL CHECK (total >= 0),
    estado ENUM('PENDIENTE', 'CONFIRMADO', 'EN_PREPARACION', 'LISTO', 'EN_TRANSITO', 'ENTREGADO', 'CANCELADO') DEFAULT 'PENDIENTE',
    direccion_entrega VARCHAR(255) NOT NULL,
    notas TEXT,
    tiempo_estimado INT CHECK (tiempo_estimado > 0),
    costo_delivery DECIMAL(6,2) DEFAULT 5.00,
    metodo_pago ENUM('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'YAPE', 'PLIN') DEFAULT 'EFECTIVO',
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    FOREIGN KEY (restaurante_id) REFERENCES restaurante(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS detalle_pedido;
CREATE TABLE detalle_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(8,2) NOT NULL CHECK (precio_unitario > 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal > 0),
    notas_especiales TEXT,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE RESTRICT,
    CONSTRAINT chk_subtotal_calculo CHECK (ABS(subtotal - (cantidad * precio_unitario)) < 0.01)
);

DROP TABLE IF EXISTS repartidor;
CREATE TABLE repartidor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    vehiculo VARCHAR(50),
    disponible BOOLEAN DEFAULT TRUE,
    estado ENUM('LIBRE', 'OCUPADO', 'INACTIVO') DEFAULT 'LIBRE',
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS entrega;
CREATE TABLE entrega (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    repartidor_id BIGINT NOT NULL,
    fecha_asignacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega DATETIME,
    estado ENUM('ASIGNADO', 'EN_CAMINO', 'ENTREGADO', 'FALLIDO') DEFAULT 'ASIGNADO',
    comentarios TEXT,
    latitud DECIMAL(10,8),
    longitud DECIMAL(11,8),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (repartidor_id) REFERENCES repartidor(id) ON DELETE RESTRICT
);

DROP TABLE IF EXISTS calificacion;
CREATE TABLE calificacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    cliente_id BIGINT NOT NULL,
    restaurante_id BIGINT NOT NULL,
    puntuacion INT NOT NULL CHECK (puntuacion >= 1 AND puntuacion <= 5),
    comentario TEXT,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    FOREIGN KEY (restaurante_id) REFERENCES restaurante(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS promocion;
CREATE TABLE promocion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurante_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo ENUM('DESCUENTO_PORCENTAJE', 'DESCUENTO_FIJO', 'ENVIO_GRATIS') NOT NULL,
    valor DECIMAL(8,2) NOT NULL CHECK (valor >= 0),
    codigo VARCHAR(20) UNIQUE,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    monto_minimo DECIMAL(10,2) DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE,
    usos_maximos INT DEFAULT NULL,
    usos_actuales INT DEFAULT 0,
    FOREIGN KEY (restaurante_id) REFERENCES restaurante(id) ON DELETE CASCADE,
    CHECK (fecha_fin > fecha_inicio),
    CHECK (
        (tipo = 'DESCUENTO_PORCENTAJE' AND valor <= 100) OR 
        (tipo = 'DESCUENTO_FIJO' AND valor >= 0) OR 
        (tipo = 'ENVIO_GRATIS' AND valor = 0)
    )
);

DROP TABLE IF EXISTS configuracion;
CREATE TABLE configuracion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clave VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT NOT NULL,
    descripcion TEXT,
    tipo ENUM('STRING', 'NUMBER', 'BOOLEAN', 'JSON') DEFAULT 'STRING',
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- INSERTAR DATOS DE PRUEBA
INSERT INTO categoria (nombre, descripcion, orden_mostrar) VALUES
('PIZZAS', 'Pizzas artesanales y tradicionales', 1),
('PASTAS', 'Pastas frescas y caseras', 2),
('HAMBURGUESAS', 'Hamburguesas gourmet y clásicas', 3),
('ACOMPAÑAMIENTOS', 'Guarniciones y acompañamientos', 4),
('BEBIDAS', 'Bebidas frías y calientes', 5),
('POSTRES', 'Postres y dulces', 6),
('SASHIMI', 'Pescado fresco estilo japonés', 7),
('MAKIS', 'Rollos de sushi variados', 8),
('TEMPURA', 'Frituras estilo japonés', 9),
('SOPAS', 'Sopas tradicionales y especiales', 10),
('POLLOS', 'Pollo a la brasa y preparaciones', 11),
('PARRILLA', 'Carnes y parrillas', 12),
('CAFES', 'Café de especialidad', 13),
('PANADERIA', 'Productos de panadería', 14);

INSERT INTO cliente (nombre, email, password, direccion) VALUES
('Cliente Demo', 'cliente@example.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Av. Cliente 123'),
('Admin Demo', 'admin@example.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Av. Admin 456'),
('Restaurante Demo', 'restaurante@example.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Av. Restaurante 789'),
('Ana García', 'ana.garcia@email.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Av. Arequipa 2847, Lima'),
('Carlos Mendoza', 'carlos.mendoza@email.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Jr. Callao 456, Lima Centro'),
('María Rodríguez', 'maria.rodriguez@email.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Av. Javier Prado 1234, San Isidro'),
('Luis Fernández', 'luis.fernandez@email.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Calle Las Flores 789, Miraflores'),
('Sofia Vargas', 'sofia.vargas@email.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Av. Universitaria 2456, Los Olivos'),
('Juan Pérez', 'juan.perez@delivery.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Av. Repartidor 111'),
('Pedro Sánchez', 'pedro.sanchez@delivery.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Av. Repartidor 222'),
('Miguel Torres', 'miguel.torres@delivery.com', '$2a$10$Rihz5Fy7y7hVkDJ7pDUNdO0uYI6SgTvlK1Rdz7gQSPpMLv6a0nDlm', 'Av. Repartidor 333');

INSERT INTO cliente_rol (cliente_id, rol_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1), (9, 4), (10, 4), (11, 4);

INSERT INTO restaurante (cliente_id, nombre, descripcion, direccion, telefono, hora_apertura, hora_cierre) VALUES
(3, 'Pizzería Mario', 'Las mejores pizzas artesanales de la ciudad', 'Av. Larco 123, Miraflores', '01-234-5678', '11:00:00', '23:00:00'),
(3, 'Burger Palace', 'Hamburguesas gourmet con ingredientes frescos', 'Jr. Unión 456, Lima Centro', '01-345-6789', '12:00:00', '22:00:00'),
(3, 'Sushi Zen', 'Auténtica comida japonesa', 'Av. Conquistadores 789, San Isidro', '01-456-7890', '18:00:00', '00:00:00'),
(3, 'Pollos El Súper', 'Pollo a la brasa tradicional peruano', 'Av. Brasil 321, Breña', '01-567-8901', '10:00:00', '22:00:00'),
(3, 'Café Lima', 'Café de especialidad y postres caseros', 'Calle Tarata 147, Miraflores', '01-678-9012', '07:00:00', '20:00:00');

INSERT INTO menu (nombre, descripcion, precio, categoria_id, restaurante_id, imagen_url) VALUES
('Pizza Margherita', 'Clásica pizza con tomate, mozzarella y albahaca fresca', 35.90, 1, 1, 'https://example.com/margherita.jpg'),
('Pizza Pepperoni', 'Pizza con pepperoni italiano y queso mozzarella', 42.90, 1, 1, 'https://example.com/pepperoni.jpg'),
('Pizza Cuatro Quesos', 'Mezcla de mozzarella, parmesano, gorgonzola y ricotta', 48.90, 1, 1, 'https://example.com/cuatroquesos.jpg'),
('Lasaña Bolognesa', 'Lasaña casera con salsa bolognesa y bechamel', 38.90, 2, 1, 'https://example.com/lasagna.jpg'),
('Tiramisu', 'Postre italiano tradicional con café y mascarpone', 18.90, 6, 1, 'https://example.com/tiramisu.jpg'),
('Classic Burger', 'Hamburguesa clásica con carne 100% res, lechuga, tomate y cebolla', 28.90, 3, 2, 'https://example.com/classic.jpg'),
('BBQ Bacon Burger', 'Hamburguesa con bacon, salsa BBQ y cebolla caramelizada', 34.90, 3, 2, 'https://example.com/bbq.jpg'),
('Chicken Crispy', 'Hamburguesa de pollo crujiente con mayonesa especial', 26.90, 3, 2, 'https://example.com/chicken.jpg'),
('Papas Fritas', 'Porción de papas fritas crujientes', 12.90, 4, 2, 'https://example.com/fries.jpg'),
('Milkshake Vainilla', 'Batido cremoso de vainilla con crema chantilly', 15.90, 5, 2, 'https://example.com/milkshake.jpg'),
('Sashimi Mix', 'Selección de sashimi fresco: salmón, atún y pez mantequilla', 65.90, 7, 3, 'https://example.com/sashimi.jpg'),
('California Roll', 'Maki con cangrejo, palta y pepino, cubierto con ajonjolí', 32.90, 8, 3, 'https://example.com/california.jpg'),
('Salmon Roll', 'Maki de salmón fresco con palta y pepino', 38.90, 8, 3, 'https://example.com/salmon.jpg'),
('Tempura Mixto', 'Selección de vegetales y langostinos en tempura', 42.90, 9, 3, 'https://example.com/tempura.jpg'),
('Miso Soup', 'Sopa tradicional japonesa con tofu y algas', 12.90, 10, 3, 'https://example.com/miso.jpg'),
('Pollo Entero', 'Pollo a la brasa entero con papas y ensalada', 45.90, 11, 4, 'https://example.com/pollo-entero.jpg'),
('Medio Pollo', 'Medio pollo a la brasa con papas fritas', 28.90, 11, 4, 'https://example.com/medio-pollo.jpg'),
('Cuarto de Pollo', 'Cuarto de pollo con papas y ensalada', 18.90, 11, 4, 'https://example.com/cuarto-pollo.jpg'),
('Anticuchos', 'Brochetas de corazón marinadas con ají panca', 22.90, 12, 4, 'https://example.com/anticuchos.jpg'),
('Chicha Morada', 'Bebida tradicional peruana de maíz morado', 8.90, 5, 4, 'https://example.com/chicha.jpg'),
('Espresso', 'Café espresso de grano selecto', 8.90, 13, 5, 'https://example.com/espresso.jpg'),
('Cappuccino', 'Café con leche espumosa y canela', 12.90, 13, 5, 'https://example.com/cappuccino.jpg'),
('Americano', 'Café americano suave y aromático', 9.90, 13, 5, 'https://example.com/americano.jpg'),
('Cheesecake', 'Tarta de queso con frutos rojos', 16.90, 6, 5, 'https://example.com/cheesecake.jpg'),
('Croissant', 'Croissant francés recién horneado', 8.90, 14, 5, 'https://example.com/croissant.jpg');

INSERT INTO repartidor (cliente_id, telefono, vehiculo) VALUES
(9, '987654321', 'Moto Honda'),
(10, '987654322', 'Bicicleta'),
(11, '987654323', 'Moto Yamaha');

INSERT INTO pedido (cliente_id, restaurante_id, total, estado, direccion_entrega, notas, tiempo_estimado, costo_delivery, metodo_pago) VALUES
(4, 1, 89.80, 'ENTREGADO', 'Av. Arequipa 2847, Lima', 'Sin cebolla en la pizza', 35, 5.00, 'YAPE'),
(5, 2, 68.80, 'ENTREGADO', 'Jr. Callao 456, Lima Centro', 'Punto medio la hamburguesa', 25, 5.00, 'TARJETA'),
(6, 3, 103.80, 'EN_TRANSITO', 'Av. Javier Prado 1234, San Isidro', 'Wasabi aparte', 40, 5.00, 'EFECTIVO'),
(7, 4, 79.80, 'LISTO', 'Calle Las Flores 789, Miraflores', 'Pollo bien dorado', 30, 5.00, 'PLIN'),
(8, 5, 56.60, 'EN_PREPARACION', 'Av. Universitaria 2456, Los Olivos', 'Café sin azúcar', 15, 5.00, 'TRANSFERENCIA'),
(4, 2, 60.70, 'CONFIRMADO', 'Av. Arequipa 2847, Lima', 'Papas extra crujientes', 20, 5.00, 'YAPE');

INSERT INTO detalle_pedido (pedido_id, menu_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 1, 35.90, 35.90), (1, 3, 1, 48.90, 48.90),
(2, 6, 1, 28.90, 28.90), (2, 7, 1, 34.90, 34.90),
(3, 11, 1, 65.90, 65.90), (3, 12, 1, 32.90, 32.90),
(4, 16, 1, 45.90, 45.90), (4, 17, 1, 28.90, 28.90),
(5, 22, 2, 12.90, 25.80), (5, 25, 1, 8.90, 8.90), (5, 24, 1, 16.90, 16.90),
(6, 8, 1, 26.90, 26.90), (6, 9, 1, 12.90, 12.90), (6, 10, 1, 15.90, 15.90);

INSERT INTO entrega (pedido_id, repartidor_id, fecha_entrega, estado, comentarios) VALUES
(1, 1, '2025-08-18 14:30:00', 'ENTREGADO', 'Entrega exitosa'),
(2, 2, '2025-08-18 19:45:00', 'ENTREGADO', 'Cliente satisfecho'),
(3, 3, NULL, 'EN_CAMINO', 'En camino al destino'),
(4, 4, NULL, 'ASIGNADO', 'Pedido asignado, preparando recojo');

INSERT INTO calificacion (pedido_id, cliente_id, restaurante_id, puntuacion, comentario) VALUES
(1, 4, 1, 5, 'Excelentes pizzas, llegaron calientes y en buen tiempo'),
(2, 5, 2, 4, 'Buenas hamburguesas, pero el delivery tardó un poco');

INSERT INTO configuracion (clave, valor, descripcion, tipo) VALUES
('COSTO_DELIVERY_BASE', '5.00', 'Costo base de delivery en soles', 'NUMBER'),
('TIEMPO_PREPARACION_PROMEDIO', '30', 'Tiempo promedio de preparación en minutos', 'NUMBER'),
('COMISION_PLATAFORMA', '10.00', 'Comisión de la plataforma en porcentaje', 'NUMBER'),
('EMAIL_NOTIFICACIONES', 'notificaciones@deliverysystem.com', 'Email para notificaciones del sistema', 'STRING'),
('HORARIO_ATENCION_INICIO', '06:00', 'Hora de inicio de atención general', 'STRING'),
('HORARIO_ATENCION_FIN', '23:59', 'Hora de fin de atención general', 'STRING');

INSERT INTO promocion (restaurante_id, nombre, descripcion, tipo, valor, codigo, fecha_inicio, fecha_fin, monto_minimo) VALUES
(1, 'Descuento Pizzas 20%', 'Descuento del 20% en todas las pizzas', 'DESCUENTO_PORCENTAJE', 20.00, 'PIZZA20', '2025-08-01 00:00:00', '2025-12-31 23:59:59', 40.00),
(2, 'Envío Gratis Burger', 'Envío gratis en pedidos de hamburguesas', 'ENVIO_GRATIS', 0.00, 'BURGERGRATIS', '2025-08-01 00:00:00', '2025-09-30 23:59:59', 35.00),
(3, 'Descuento S/10 Sushi', 'Descuento fijo de S/10 en pedidos de sushi', 'DESCUENTO_FIJO', 10.00, 'SUSHI10', '2025-08-15 00:00:00', '2025-08-31 23:59:59', 60.00);

-- CREAR ÍNDICES PARA OPTIMIZACIÓN
CREATE INDEX idx_cliente_email ON cliente(email);
CREATE INDEX idx_cliente_fecha_registro ON cliente(fecha_registro);
CREATE INDEX idx_categoria_nombre ON categoria(nombre);
CREATE INDEX idx_categoria_activo ON categoria(activo, orden_mostrar);
CREATE INDEX idx_restaurante_cliente ON restaurante(cliente_id);
CREATE INDEX idx_restaurante_activo ON restaurante(activo);
CREATE INDEX idx_restaurante_horarios ON restaurante(hora_apertura, hora_cierre);
CREATE INDEX idx_menu_restaurante ON menu(restaurante_id);
CREATE INDEX idx_menu_categoria ON menu(categoria_id);
CREATE INDEX idx_menu_disponible ON menu(disponible);
CREATE INDEX idx_menu_precio ON menu(precio);
CREATE INDEX idx_menu_restaurante_categoria ON menu(restaurante_id, categoria_id);
CREATE INDEX idx_menu_restaurante_disponible ON menu(restaurante_id, disponible);
CREATE INDEX idx_menu_restaurante_precio ON menu(restaurante_id, precio);
CREATE INDEX idx_pedido_cliente ON pedido(cliente_id);
CREATE INDEX idx_pedido_restaurante ON pedido(restaurante_id);
CREATE INDEX idx_pedido_estado ON pedido(estado);
CREATE INDEX idx_pedido_fecha ON pedido(fecha_pedido);
CREATE INDEX idx_pedido_metodo_pago ON pedido(metodo_pago);
CREATE INDEX idx_pedido_cliente_fecha ON pedido(cliente_id, fecha_pedido);
CREATE INDEX idx_pedido_restaurante_fecha ON pedido(restaurante_id, fecha_pedido);
CREATE INDEX idx_pedido_restaurante_estado ON pedido(restaurante_id, estado);
CREATE INDEX idx_detalle_pedido ON detalle_pedido(pedido_id);
CREATE INDEX idx_detalle_menu ON detalle_pedido(menu_id);
CREATE INDEX idx_repartidor_cliente ON repartidor(cliente_id);
CREATE INDEX idx_repartidor_disponible ON repartidor(disponible);
CREATE INDEX idx_repartidor_estado ON repartidor(estado);
CREATE INDEX idx_entrega_pedido ON entrega(pedido_id);
CREATE INDEX idx_entrega_repartidor ON entrega(repartidor_id);
CREATE INDEX idx_entrega_estado ON entrega(estado);
CREATE INDEX idx_entrega_fecha_asignacion ON entrega(fecha_asignacion);
CREATE INDEX idx_entrega_fecha_entrega ON entrega(fecha_entrega);
CREATE INDEX idx_entrega_repartidor_estado ON entrega(repartidor_id, estado);
CREATE INDEX idx_calificacion_pedido ON calificacion(pedido_id);
CREATE INDEX idx_calificacion_cliente ON calificacion(cliente_id);
CREATE INDEX idx_calificacion_restaurante ON calificacion(restaurante_id);
CREATE INDEX idx_calificacion_puntuacion ON calificacion(puntuacion);
CREATE INDEX idx_calificacion_restaurante_puntuacion ON calificacion(restaurante_id, puntuacion);
CREATE INDEX idx_cliente_rol_cliente ON cliente_rol(cliente_id);
CREATE INDEX idx_cliente_rol_rol ON cliente_rol(rol_id);
CREATE INDEX idx_promocion_restaurante ON promocion(restaurante_id);
CREATE INDEX idx_promocion_codigo ON promocion(codigo);
CREATE INDEX idx_promocion_activo ON promocion(activo);
CREATE INDEX idx_promocion_fechas ON promocion(fecha_inicio, fecha_fin, activo);
CREATE INDEX idx_configuracion_clave ON configuracion(clave);