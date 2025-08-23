# Sistema de Delivery y Gestión de Restaurantes

## Descripción
Backend completo para un sistema de delivery y gestión de restaurantes desarrollado con Spring Boot, implementando APIs REST robustas para el manejo de pedidos, menús, restaurantes y entregas.

## Tecnologías
- **Spring Boot 3.5.4** con Java 21
- **Spring Security** con autenticación JWT
- **Spring Data JPA** con Hibernate
- **MySQL 8.0+** como base de datos
- **Maven** para gestión de dependencias
- **Lombok** para reducir boilerplate code

## Requisitos Previos
- Java 21 o superior
- MySQL 8.0 o superior
- Maven 3.6 o superior

## Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone <repository-url>
cd backend-sistema-delivery-y-gestion
```

### 2. Configurar Base de Datos
```bash
# Conectar a MySQL
mysql -u root -p

# Crear base de datos
CREATE DATABASE IF NOT EXISTS deliverydb;
exit

# Ejecutar script de creación de tablas y datos
mysql -u root -p deliverydb < deliverybd.sql
```

### 3. Configurar application.properties
Actualizar las credenciales de base de datos en `src/main/resources/application.properties`:
```properties
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
```

### 4. Compilar y Ejecutar
```bash
# Compilar el proyecto
./mvnw clean compile

# Ejecutar la aplicación
./mvnw spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## Estructura de la Base de Datos

### Tablas Principales
- **cliente** - Usuarios del sistema (clientes, administradores, restaurantes)
- **rol** - Roles de usuario (CLIENTE, ADMIN, RESTAURANTE)
- **restaurante** - Información de restaurantes
- **menu** - Menús y platos de cada restaurante
- **pedido** - Pedidos realizados por clientes
- **detalle_pedido** - Detalles de cada pedido
- **repartidor** - Información de repartidores
- **entrega** - Gestión de entregas
- **calificacion** - Calificaciones de pedidos

### Datos de Prueba Incluidos
- 8 clientes con diferentes roles
- 5 restaurantes con menús variados
- 25 platos/menús distribuidos por categorías
- 6 pedidos de ejemplo con diferentes estados
- 5 repartidores disponibles
- Entregas y calificaciones de muestra

## APIs Disponibles

### Autenticación
- `POST /api/autenticacion/registro` - Registro de usuarios
- `POST /api/autenticacion/login` - Inicio de sesión

### Usuarios Predefinidos
```
Cliente: cliente@example.com / 123456
Admin: admin@example.com / 123456
Restaurante: restaurante@example.com / 123456
```

## Testing

### Ejecutar Tests
```bash
# Todos los tests
./mvnw test

# Tests específicos
./mvnw test -Dtest=ClienteServiceTest
```

### Verificar Base de Datos
```sql
-- Verificar tablas creadas
SHOW TABLES;

-- Contar registros
SELECT COUNT(*) FROM cliente;
SELECT COUNT(*) FROM restaurante;
SELECT COUNT(*) FROM menu;
SELECT COUNT(*) FROM pedido;
```

## Desarrollo

### Comandos Útiles
```bash
# Compilación rápida
./mvnw compile

# Limpiar y recompilar
./mvnw clean compile

# Ejecutar con perfil de desarrollo
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Estructura del Proyecto
```
src/
├── main/
│   ├── java/com/delivery/sistema/delivery/y/gestion/
│   │   ├── Config/           # Configuraciones
│   │   ├── Controller/       # Controladores REST
│   │   ├── Dto/             # Data Transfer Objects
│   │   ├── Entity/          # Entidades JPA
│   │   ├── Repository/      # Repositorios de datos
│   │   ├── Security/        # Configuración de seguridad
│   │   └── Services/        # Lógica de negocio
│   └── resources/
│       └── application.properties
└── test/                    # Tests unitarios e integración
```

## Próximos Pasos en Desarrollo

1. **Completar Entidades JPA** - Implementar entidades faltantes
2. **Crear Repositorios** - Repositorios con consultas JPQL
3. **Desarrollar Servicios** - Lógica de negocio completa
4. **Implementar Controllers** - APIs REST completas
5. **Agregar Swagger** - Documentación de APIs
6. **Testing Completo** - Tests unitarios e integración

## Contribución

1. Fork el proyecto
2. Crear rama para feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## Licencia
Proyecto académico para CIBERTEC - Desarrollo de Aplicaciones Web I

## Contacto
Desarrollado para el curso DAW I - Ciclo 5, CIBERTEC 2025