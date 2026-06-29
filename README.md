# Proyecto Library - Arquitectura de Microservicios

## 1. Descripción general

Este proyecto corresponde a un sistema de biblioteca desarrollado con arquitectura de microservicios usando Spring Boot.
La solución está compuesta por un BFF y tres microservicios principales:

* **BFF**: punto de entrada principal del sistema. Recibe las solicitudes HTTP y las redirige hacia los microservicios correspondientes.
* **auth**: microservicio encargado del registro, login y generación de token JWT.
* **ms-book**: microservicio encargado de la gestión de libros.
* **ms-user**: microservicio encargado de la gestión de perfiles de usuario.

El sistema utiliza autenticación mediante **JWT Bearer Token**, documentación con **Swagger/OpenAPI**, pruebas automatizadas con **JUnit 5**, **Mockito**, **H2** para pruebas de persistencia y cobertura con **JaCoCo**.

---

## 2. Arquitectura general

La comunicación del sistema sigue el siguiente flujo:

```text
Cliente / Swagger / Postman
          ↓
         BFF
          ↓
 ┌────────┼────────┐
 ↓        ↓        ↓
auth  ms-book  ms-user
```

El **BFF** funciona como una capa intermedia que centraliza las solicitudes.
De esta forma, el cliente no se comunica directamente con los microservicios internos, sino que realiza las peticiones al BFF.

---

## 3. Tecnologías utilizadas

* Java
* Spring Boot
* Spring Web
* Spring Security
* Spring Data JPA
* Gradle
* PostgreSQL
* H2 Database para pruebas
* JWT
* Swagger / OpenAPI
* JUnit 5
* Mockito
* JaCoCo

---

## 4. Módulos del proyecto

### 4.1 BFF

El BFF es la entrada principal del sistema.
Se encarga de recibir las solicitudes HTTP y redirigirlas hacia el microservicio correspondiente.

Funciones principales:

* Redirigir solicitudes de autenticación hacia `ms-auth`.
* Redirigir solicitudes de libros hacia `ms-book`.
* Redirigir solicitudes de perfiles hacia `ms-user`.
* Validar token JWT en endpoints protegidos.
* Exponer documentación Swagger centralizada.

Puerto utilizado:

```text
http://localhost:5000
```

---

### 4.2 ms-auth

Microservicio encargado de la autenticación.

Funciones principales:

* Registro de usuarios.
* Login de usuarios.
* Generación de token JWT.
* Validación de credenciales.

Endpoints principales expuestos a través del BFF:

```text
POST /register
POST /login
```

Flujo de autenticación:

```text
1. El usuario se registra en /register.
2. Luego inicia sesión en /login.
3. El sistema devuelve un token JWT.
4. El token se utiliza en Swagger o Postman como Bearer Token.
```

---

### 4.3 ms-book

Microservicio encargado de la gestión de libros.

Funciones principales:

* Crear libros.
* Listar libros.
* Buscar libros por filtros.
* Buscar libro por ID.
* Actualizar libro.
* Eliminar libro.

Endpoints expuestos a través del BFF:

```text
GET    /books
POST   /books
GET    /books/{id}
PUT    /books/{id}
DELETE /books/{id}
GET    /books/search
```

Ejemplo de request para crear un libro:

```json
{
  "title": "El Principito",
  "author": "Antoine de Saint-Exupéry",
  "category": "Literatura",
  "isbn": "123456789"
}
```

Ejemplo de respuesta:

```json
{
  "success": true,
  "data": {
    "id": "00000000-0000-0000-0000-000000000000",
    "title": "El Principito",
    "author": "Antoine de Saint-Exupéry",
    "category": "Literatura",
    "isbn": "123456789"
  },
  "message": "Libro creado con éxito"
}
```

---

### 4.4 ms-user

Microservicio encargado de la gestión de perfiles de usuario.

Funciones principales:

* Obtener todos los perfiles.
* Crear o actualizar un perfil.
* Eliminar perfil por ID.
* Buscar perfil por correo de autenticación.

Endpoints expuestos a través del BFF:

```text
GET    /users
PUT    /users/profile
DELETE /users/{id}
```

Ejemplo de request para completar o actualizar perfil:

```json
{
  "authEmail": "usuario@test.com",
  "fullName": "Usuario Test",
  "phone": "912345678",
  "address": "Santiago"
}
```

## Diagramas del sistema

### Diagrama C2 - Sistema Biblioteca

![Diagrama C2 - Sistema Biblioteca](docs/diagrama-c2-sistema-biblioteca.jpeg)

### Diagrama C3 - SystemLibrary

![Diagrama C3 - SystemLibrary](docs/diagrama-c3-systemlibrary.jpeg)

## 5. Seguridad con JWT

El proyecto utiliza autenticación mediante JWT.

Los endpoints públicos son:

```text
POST /register
POST /login
```

Los demás endpoints requieren token.

Para consumir endpoints protegidos se debe enviar el header:

```http
Authorization: Bearer <token>
```

Ejemplo:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

En Swagger se configuró la autenticación mediante el botón **Authorize**, usando el esquema `bearerAuth`.

---

## 6. Swagger / OpenAPI

El BFF expone la documentación Swagger del sistema.

URL de Swagger UI:

```text
http://localhost:5000/swagger-ui/index.html
```

URL de la especificación OpenAPI en formato JSON:

```text
http://localhost:5000/v3/api-docs
```

Swagger permite:

* Visualizar los endpoints disponibles.
* Probar solicitudes HTTP desde el navegador.
* Revisar los DTOs en la sección Schemas.
* Usar autenticación JWT mediante el botón Authorize.
* Generar documentación OpenAPI para importar en herramientas como Postman.

---

## 7. Flujo recomendado para probar en Swagger

Para probar correctamente los endpoints protegidos desde Swagger:

```text
1. Levantar los microservicios necesarios.
2. Abrir Swagger del BFF:
   http://localhost:5000/swagger-ui/index.html
3. Ejecutar POST /register para registrar un usuario.
4. Ejecutar POST /login para obtener un token JWT.
5. Copiar el token generado.
6. Presionar el botón Authorize.
7. Pegar el token JWT.
8. Probar endpoints protegidos como GET /books o GET /users.
```

---

## 8. Importación en Postman

La documentación OpenAPI puede exportarse desde:

```text
http://localhost:5000/v3/api-docs
```

Luego puede importarse en Postman como archivo JSON.

También se puede generar una colección Postman a partir de esta especificación para probar los endpoints del BFF.

Al usar Postman, se recomienda configurar el token JWT a nivel de colección:

```text
Authorization → Type: Bearer Token → Token
```

---

## 9. Configuración de Swagger con JWT

Para permitir autenticación mediante header en Swagger, se agregó una clase `OpenApiConfig` en el BFF:

```java
@Configuration
@OpenAPIDefinition(
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
```

Esta configuración permite que Swagger muestre el botón **Authorize** y envíe el token JWT en las solicitudes protegidas.

Además, en `SecurityConfig` se permitieron las rutas de Swagger:

```java
.requestMatchers(
    "/login",
    "/register",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/v3/api-docs/**"
).permitAll()
```

Esto permite acceder a la documentación sin token, manteniendo protegidos los endpoints privados.

---

## 10. Pruebas automatizadas

El proyecto incorpora pruebas automatizadas para validar el comportamiento de las distintas capas.

### 10.1 Tests en ms-book

El microservicio `ms-book` cuenta con pruebas para:

```text
BookDtoValidationTest
BookControllerTest
BookServiceImplTest
BookRepositoryTest
```

Objetivo de cada test:

* **BookDtoValidationTest**: valida restricciones del DTO.
* **BookControllerTest**: valida las respuestas del controlador usando mocks.
* **BookServiceImplTest**: valida la lógica del servicio usando Mockito.
* **BookRepositoryTest**: valida consultas JPA usando H2 en memoria.

---

### 10.2 Tests en ms-user

El microservicio `ms-user` cuenta con pruebas para:

```text
UserDtoValidationTest
UserControllerTest
UserServiceImplTest
UserRepositoryTest
```

Objetivo de cada test:

* **UserDtoValidationTest**: valida restricciones del DTO.
* **UserControllerTest**: valida el comportamiento del controlador.
* **UserServiceImplTest**: valida la lógica de creación, actualización y eliminación de perfiles.
* **UserRepositoryTest**: valida búsquedas por correo usando JPA.

---

## 11. Repository test con H2

Los tests de repository utilizan H2 en memoria para evitar depender de la base de datos real de desarrollo.

Ejemplo de configuración en `application-test.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:books_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.open-in-view=false

spring.flyway.enabled=false
```

Esto permite ejecutar pruebas de persistencia sin afectar la base de datos PostgreSQL real.

---

## 12. Ejecutar tests

Desde la raíz del microservicio correspondiente:

```bash
./gradlew test
```

En Windows:

```bash
.\gradlew test
```

Este comando ejecuta toda la suite de pruebas del microservicio.

---

## 13. Reporte de tests

Después de ejecutar los tests, Gradle genera un reporte HTML en:

```text
build/reports/tests/test/index.html
```

Este reporte muestra:

* Cantidad de tests ejecutados.
* Tests exitosos.
* Tests fallidos.
* Tests omitidos.
* Tiempo de ejecución.

---

## 14. Coverage con JaCoCo

El proyecto incorpora JaCoCo para medir la cobertura de pruebas.

Configuración en `build.gradle`:

```gradle
plugins {
    id 'jacoco'
}

tasks.named('test') {
    useJUnitPlatform()
    finalizedBy tasks.named('jacocoTestReport')
}

jacocoTestReport {
    dependsOn tasks.named('test')
    reports {
        xml.required = true
        html.required = true
    }
}
```

Para ejecutar tests y generar cobertura:

```bash
./gradlew test jacocoTestReport
```

En Windows:

```bash
.\gradlew test jacocoTestReport
```

Reporte HTML:

```text
build/reports/jacoco/test/html/index.html
```

Reporte XML:

```text
build/reports/jacoco/test/jacocoTestReport.xml
```

El reporte HTML permite visualizar qué clases y métodos están cubiertos por pruebas.

---

## 15. Orden recomendado para revisar los tests

Orden sugerido para presentar o explicar las pruebas:

```text
1. DTO Validation Test
2. ServiceImpl Test
3. Controller Test
4. Repository Test con H2
5. Reporte de cobertura JaCoCo
```

Este orden permite explicar primero las pruebas más simples y luego avanzar hacia pruebas de lógica e integración con persistencia.

---

## 16. Comandos útiles

Levantar un microservicio:

```bash
./gradlew bootRun
```

En Windows:

```bash
.\gradlew bootRun
```

Ejecutar tests:

```bash
.\gradlew test
```

Ejecutar tests con JaCoCo:

```bash
.\gradlew test jacocoTestReport
```

Limpiar y compilar:

```bash
.\gradlew clean build
```

---

## 17. Códigos HTTP utilizados

El proyecto utiliza códigos HTTP según el tipo de operación:

```text
200 OK       → Consulta o actualización exitosa.
201 Created  → Recurso creado correctamente.
400 Bad Request → Solicitud inválida.
401 Unauthorized → Token ausente o inválido.
404 Not Found → Recurso no encontrado.
500 Internal Server Error → Error interno del servidor.
```

Ejemplos:

```text
POST /books      → 201 Created
GET /books       → 200 OK
PUT /books/{id}  → 200 OK
DELETE /books/{id} → 200 OK
```

---

## 18. Estructura general del proyecto

```text
Proyecto
├── bff
│   ├── controller
│   ├── service
│   ├── client
│   ├── dto
│   ├── config
│   └── security
│
├── ms-auth
│   ├── controller
│   ├── service
│   ├── dto
│   ├── entity
│   ├── repository
│   └── security
│
├── ms-book
│   ├── controller
│   ├── service
│   ├── dto
│   ├── entity
│   ├── repository
│   └── exception
│
└── ms-user
    ├── controller
    ├── service
    ├── dto
    ├── entity
    ├── repository
    └── exception
```

---

## 19. Conclusión

Este proyecto implementa una arquitectura de microservicios con un BFF como punto de entrada, separando responsabilidades entre autenticación, gestión de libros y gestión de perfiles de usuario.

Además, incorpora buenas prácticas como:

* Separación por capas.
* Comunicación entre microservicios mediante el BFF.
* Seguridad con JWT.
* Documentación con Swagger/OpenAPI.
* Validación de DTOs.
* Pruebas unitarias e integración.
* Base de datos H2 para tests.
* Reportes de cobertura con JaCoCo.

La documentación Swagger permite probar la API de forma visual, mientras que los tests automatizados aseguran el correcto funcionamiento de las capas principales del sistema.
