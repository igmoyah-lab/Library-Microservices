# Eva_3_Library - Sistema de Biblioteca con Microservicios

## 1. Descripción general

`Eva_3_Library` es un sistema de biblioteca desarrollado con arquitectura de microservicios usando Spring Boot.

La solución está compuesta por:

- **1 BFF** como punto de entrada principal.
- **9 microservicios** con responsabilidades independientes.
- **8 bases de datos PostgreSQL**, respetando la regla *Database per Service* cuando corresponde.
- Autenticación y autorización mediante **JWT Bearer Token**.
- Documentación de API mediante **Swagger / OpenAPI**.
- Pruebas automatizadas con **JUnit 5**, **Mockito** y **H2**.
- Reportes de cobertura mediante **JaCoCo**.
- Orquestación completa con **Docker Compose**.

---

## 2. Arquitectura general

El cliente consume el sistema mediante el BFF. El BFF valida el token JWT y redirige cada solicitud hacia el microservicio correspondiente.

```text
Cliente / Swagger / Postman
             |
             v
        BFF - Puerto 5000
             |
   +---------+---------+---------+
   |         |         |         |
   v         v         v         v
ms-auth   ms-book   ms-user   otros servicios
```

Flujo general:

```text
Cliente
  -> BFF
      -> ms-auth
      -> ms-book
      -> ms-user
      -> ms-loan
      -> ms-return
      -> ms-reservation
      -> ms-fine
      -> ms-notification
      -> ms-report
```

El cliente no necesita comunicarse directamente con los microservicios internos.

---

## 3. Tecnologías utilizadas

- Java 25
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Gradle
- JWT
- Swagger / OpenAPI
- JUnit 5
- Mockito
- H2 para pruebas
- JaCoCo
- Docker
- Docker Compose

---

## 4. Servicios y puertos

| Servicio | Responsabilidad principal | Puerto |
|---|---|---:|
| `bff` | Entrada principal y enrutamiento | 5000 |
| `ms-book` | Gestión de libros | 5001 |
| `ms-user` | Gestión de perfiles de usuario | 5002 |
| `ms-auth` | Registro, login y generación de JWT | 5003 |
| `ms-loan` | Gestión de préstamos | 5004 |
| `ms-return` | Gestión de devoluciones | 5005 |
| `ms-reservation` | Gestión de reservas | 5006 |
| `ms-fine` | Gestión de multas | 5007 |
| `ms-notification` | Gestión de notificaciones | 5008 |
| `ms-report` | Generación y consulta de reportes | 5009 |

---

## 5. Bases de datos

Cada microservicio que necesita persistencia cuenta con su propia base de datos PostgreSQL.

| Base de datos | Microservicio | Puerto local |
|---|---|---:|
| `book` | `ms-book` | 3001 |
| `user` | `ms-user` | 3002 |
| `authdb` | `ms-auth` | 3003 |
| `loans` | `ms-loan` | 3004 |
| `returns` | `ms-return` | 3005 |
| `reservations` | `ms-reservation` | 3006 |
| `fines` | `ms-fine` | 3007 |
| `notifications` | `ms-notification` | 3008 |

`ms-report` no utiliza una base de datos propia, porque obtiene información desde otros microservicios y procesa los datos necesarios para sus reportes.

---

## 6. Responsabilidad de los módulos

### 6.1 BFF

El BFF es el punto de entrada principal del sistema.

Funciones:

- Recibir las solicitudes del cliente.
- Validar JWT en endpoints protegidos.
- Redirigir solicitudes al microservicio correspondiente.
- Centralizar la documentación Swagger.
- Propagar respuestas y códigos HTTP de los servicios internos.
- Manejar errores de servicios externos.

URL principal:

```text
http://localhost:5000
```

### 6.2 ms-auth

Responsable de:

- Registro de usuarios.
- Inicio de sesión.
- Validación de credenciales.
- Generación de JWT.

Endpoints principales a través del BFF:

```text
POST /register
POST /login
```

### 6.3 ms-book

Responsable del CRUD completo de libros:

```text
GET    /books
POST   /books
GET    /books/{id}
PUT    /books/{id}
DELETE /books/{id}
GET    /books/search
```

Ejemplo de creación:

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "category": "Software Engineering",
  "isbn": "9780132350884"
}
```

### 6.4 ms-user

Responsable de:

- Crear o actualizar perfiles.
- Listar perfiles.
- Buscar perfiles.
- Eliminar perfiles.
- Relacionar un perfil con el correo usado en autenticación.

### 6.5 ms-loan

Responsable de:

- Registrar préstamos.
- Consultar préstamos.
- Aplicar reglas relacionadas con usuarios y libros.
- Comunicarse con `ms-user` y `ms-book`.

### 6.6 ms-return

Responsable de:

- Registrar devoluciones.
- Consultar información de préstamos.
- Actualizar el flujo relacionado con libros devueltos.
- Comunicarse con `ms-loan`, `ms-book` y `ms-fine`.

### 6.7 ms-reservation

Responsable de:

- Crear y consultar reservas.
- Validar información de usuarios y libros.
- Solicitar notificaciones relacionadas con reservas.

### 6.8 ms-fine

Responsable de:

- Registrar y consultar multas.
- Persistir información en la base `fines`.
- Exponer información que puede ser utilizada por devoluciones y reportes.

### 6.9 ms-notification

Responsable de:

- Crear y consultar notificaciones.
- Persistir mensajes o eventos relacionados con el sistema.

### 6.10 ms-report

Responsable de:

- Obtener información desde otros microservicios.
- Procesar datos de préstamos y multas.
- Generar respuestas de reporte sin mantener una base de datos propia.

---

## 7. Seguridad con JWT

Los endpoints públicos son:

```text
POST /register
POST /login
```

Los demás endpoints requieren un JWT válido.

Header requerido:

```http
Authorization: Bearer <token>
```

En Swagger:

1. Ejecutar `POST /register` o `POST /login`.
2. Copiar el token.
3. Presionar **Authorize**.
4. Pegar solamente el token.
5. Ejecutar los endpoints protegidos.

---

## 8. Swagger / OpenAPI

Swagger del BFF:

```text
http://localhost:5000/swagger-ui/index.html
```

Especificación OpenAPI:

```text
http://localhost:5000/v3/api-docs
```

Swagger permite:

- Visualizar los endpoints.
- Ejecutar solicitudes desde el navegador.
- Revisar DTOs y esquemas.
- Utilizar autenticación JWT.
- Validar códigos HTTP y respuestas.

---

## 9. Docker Compose

El archivo `docker-compose.yml` levanta:

- 8 bases de datos PostgreSQL.
- 9 microservicios.
- 1 BFF.
- 18 contenedores en total.

### 9.1 Construir y levantar el sistema

Desde la raíz del proyecto:

```bash
docker compose up -d --build
```

### 9.2 Revisar el estado

```bash
docker compose ps
```

Las bases de datos deberían aparecer como:

```text
Up (healthy)
```

Los microservicios y el BFF deberían aparecer como:

```text
Up
```

### 9.3 Revisar logs

Todos los servicios:

```bash
docker compose logs
```

Un servicio específico:

```bash
docker compose logs --tail=100 bff
```

Ejemplo para multas:

```bash
docker compose logs --tail=100 ms-fine
```

### 9.4 Detener los contenedores

```bash
docker compose down
```

Este comando conserva los volúmenes y los datos.

Para eliminar también los volúmenes:

```bash
docker compose down -v
```

> El comando con `-v` elimina las bases de datos almacenadas en Docker.

---

## 10. Ejecución local sin Docker

Cada microservicio incluye su propio wrapper de Gradle.

Ejemplo en Windows:

```bash
cd ms-book
.\gradlew bootRun
```

Ejemplo en Linux o macOS:

```bash
cd ms-book
./gradlew bootRun
```

Al ejecutar servicios localmente, las bases de datos PostgreSQL deben estar disponibles en los puertos indicados en la tabla de bases de datos.

---

## 11. Pruebas automatizadas

El proyecto utiliza:

- JUnit 5
- Mockito
- H2
- Spring Boot Test
- JaCoCo

Ejecutar tests en Windows:

```bash
.\gradlew test
```

Ejecutar tests y generar cobertura:

```bash
.\gradlew test jacocoTestReport
```

Reporte de tests:

```text
build/reports/tests/test/index.html
```

Reporte HTML de JaCoCo:

```text
build/reports/jacoco/test/html/index.html
```

Reporte XML de JaCoCo:

```text
build/reports/jacoco/test/jacocoTestReport.xml
```

Los tests principales validan:

- DTOs.
- Controladores.
- Servicios.
- Repositorios.
- Reglas de negocio.
- Persistencia con H2.

---

## 12. Manejo de errores

El sistema incorpora manejo centralizado de errores y excepciones personalizadas.

Códigos HTTP utilizados:

| Código | Significado |
|---:|---|
| 200 | Consulta o actualización exitosa |
| 201 | Recurso creado correctamente |
| 400 | Solicitud inválida |
| 401 | Token ausente o inválido |
| 404 | Recurso no encontrado |
| 500 | Error interno o error no controlado |

El BFF propaga respuestas de error de los microservicios. Por ejemplo, al consultar un libro eliminado, el sistema devuelve un `404 Not Found` con el mensaje correspondiente.

---

## 13. Migraciones con Flyway

Los servicios con persistencia utilizan Flyway para validar y ejecutar migraciones.

Las migraciones se encuentran normalmente en:

```text
src/main/resources/db/migration
```

Al iniciar el servicio:

1. Flyway verifica el esquema.
2. Ejecuta migraciones pendientes.
3. Hibernate valida las entidades contra la base de datos.

---

## 14. Diagramas de arquitectura

### Diagrama C2

![Diagrama C2 - Sistema Biblioteca](docs/diagrama-c2-sistema-biblioteca.jpeg)

### Diagrama C3

![Diagrama C3 - SystemLibrary](docs/diagrama-c3-systemlibrary.jpeg.jpeg)

> Si el nombre real del archivo C3 no contiene la segunda extensión `.jpeg`, se debe ajustar la ruta anterior.

---

## 15. Estructura general

```text
Eva_3_Library
├── bff
├── ms-auth
├── ms-book
├── ms-user
├── ms-loan
├── ms-return
├── ms-reservation
├── ms-fine
├── ms-notification
├── ms-report
├── docs
├── docker-compose.yml
└── README.md
```

Los microservicios que utilizan persistencia mantienen una estructura similar a:

```text
controller
service
repository
entity
dto
exception
config
```

El BFF mantiene una estructura similar a:

```text
controller
service
client
dto
exception
config
security
```

---

## 16. Prueba recomendada

1. Ejecutar:

```bash
docker compose up -d --build
```

2. Confirmar los 18 contenedores:

```bash
docker compose ps
```

3. Abrir Swagger:

```text
http://localhost:5000/swagger-ui/index.html
```

4. Registrar un usuario mediante `POST /register`.

5. Copiar el JWT y autorizar Swagger.

6. Ejecutar el CRUD de libros:

```text
POST   /books
GET    /books
GET    /books/{id}
PUT    /books/{id}
DELETE /books/{id}
```

7. Verificar que consultar el recurso eliminado devuelva:

```text
404 Not Found
```

---

## 17. Evidencias recomendadas

Para la entrega se recomienda incluir capturas de:

- `docker compose up -d --build` finalizado correctamente.
- `docker compose ps` con los 18 contenedores.
- Bases de datos en estado `healthy`.
- Swagger del BFF.
- Registro o login con generación de JWT.
- Botón **Authorize** configurado.
- CRUD completo de libros.
- Respuesta `404` al consultar un libro eliminado.
- Reporte de tests.
- Reporte de cobertura JaCoCo.
- Diagramas C2 y C3.

---

## 18. Integrantes

- Ignacio Moya
- Bryan Burgos

---

## 19. Conclusión

El proyecto implementa una arquitectura de microservicios completa para la gestión de una biblioteca.

La solución separa responsabilidades entre nueve microservicios y utiliza un BFF como punto de entrada central. Cada servicio que requiere persistencia mantiene su propia base de datos, evitando compartir directamente el almacenamiento entre microservicios.

También incorpora:

- Seguridad con JWT.
- Swagger / OpenAPI.
- CRUD completo.
- Comunicación entre microservicios.
- Manejo centralizado de errores.
- Excepciones personalizadas.
- Migraciones con Flyway.
- Pruebas automatizadas.
- Cobertura con JaCoCo.
- Diagramas C2 y C3.
- Contenedores Docker.
- Orquestación con Docker Compose.