# Guía de Testing - Library Microservices

## 1. Objetivo

Esta guía permite validar el funcionamiento completo de **Library Microservices** antes de la entrega y defensa del examen.

El proyecto está compuesto por:

- 1 BFF que funciona como punto de entrada y API Gateway.
- 9 microservicios.
- 8 bases de datos PostgreSQL independientes.
- Autenticación y sesión mediante JWT.
- Comunicación HTTP entre microservicios.
- Manejo centralizado de errores.
- Logs en consola.
- Integración con GlitchTip.
- Pruebas automatizadas con JUnit 5, Mockito y H2.
- Reportes de cobertura mediante JaCoCo.
- Orquestación completa con Docker Compose.

La guía contempla:

1. Pruebas de infraestructura.
2. Pruebas funcionales.
3. Pruebas de seguridad.
4. Pruebas de integración.
5. Pruebas de reglas de negocio.
6. Pruebas de errores.
7. Pruebas automatizadas.
8. Revisión de cobertura.
9. Evidencias para la defensa.

---

## 2. Arquitectura considerada para las pruebas

```text
Cliente / Swagger / Postman / curl
                  |
                  v
          BFF / API Gateway
             Puerto 5000
                  |
   +--------------+--------------+
   |              |              |
   v              v              v
ms-auth        ms-book        ms-user
Puerto 5003    Puerto 5001    Puerto 5002

   +--------------+--------------+
   |              |              |
   v              v              v
ms-loan        ms-return      ms-reservation
Puerto 5004    Puerto 5005    Puerto 5006

   +--------------+--------------+
   |              |              |
   v              v              v
ms-fine        ms-notification  ms-report
Puerto 5007    Puerto 5008      Puerto 5009
```

El cliente debe utilizar principalmente el BFF:

```text
http://localhost:5000
```

Swagger del BFF:

```text
http://localhost:5000/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:5000/v3/api-docs
```

---

## 3. Servicios del sistema

| Servicio | Función | Puerto |
|---|---|---:|
| `bff` | Entrada principal, JWT, sesión, enrutamiento y agregación | `5000` |
| `ms-book` | Gestión de libros y disponibilidad | `5001` |
| `ms-user` | Gestión de perfiles de usuario | `5002` |
| `ms-auth` | Registro, login y generación de JWT | `5003` |
| `ms-loan` | Gestión de préstamos | `5004` |
| `ms-return` | Gestión de devoluciones | `5005` |
| `ms-reservation` | Gestión de reservas | `5006` |
| `ms-fine` | Gestión de multas | `5007` |
| `ms-notification` | Gestión de notificaciones | `5008` |
| `ms-report` | Generación de reportes | `5009` |

---

## 4. Bases de datos

| Base de datos | Servicio propietario | Puerto local |
|---|---|---:|
| `book` | `ms-book` | `3001` |
| `user` | `ms-user` | `3002` |
| `authdb` | `ms-auth` | `3003` |
| `loans` | `ms-loan` | `3004` |
| `returns` | `ms-return` | `3005` |
| `reservations` | `ms-reservation` | `3006` |
| `fines` | `ms-fine` | `3007` |
| `notifications` | `ms-notification` | `3008` |

`ms-report` no necesita base de datos propia. Obtiene información desde otros microservicios y procesa el resultado.

---

## 5. Herramientas recomendadas

- Docker Desktop.
- Visual Studio Code.
- Swagger UI.
- Postman.
- Extensión REST Client.
- CMD o PowerShell.
- Java 25.
- Gradle Wrapper.
- PostgreSQL.
- H2 para pruebas.
- JUnit 5.
- Mockito.
- JaCoCo.
- GlitchTip.

---

## 6. Preparación del entorno

### 6.1 Comprobar Docker Desktop

Ejecutar:

```cmd
docker version
```

El resultado debe incluir:

```text
Client:
...

Server:
...
```

Comprobar el contexto:

```cmd
docker context ls
```

En Windows con Docker Desktop debe aparecer seleccionado:

```text
desktop-linux *
```

Si no está seleccionado:

```cmd
docker context use desktop-linux
```

---

### 6.2 Configurar variables privadas

Crear el archivo `.env` desde `.env.example`.

Windows CMD:

```cmd
copy .env.example .env
```

Contenido esperado:

```env
SENTRY_ENABLED=true
SENTRY_DSN=COLOCAR_DSN_REAL_DE_GLITCHTIP
SENTRY_ENVIRONMENT=examen-docker
SENTRY_RELEASE=library-microservices-bff@1.0.0
SENTRY_DEBUG=false
```

Comprobar que `.env` está ignorado:

```cmd
git check-ignore .env
```

Resultado esperado:

```text
.env
```

El archivo `.env` no debe subirse a GitHub.

---

## 7. Levantar el sistema completo

### 7.1 Validar Docker Compose

```cmd
docker compose config
```

Resultado esperado:

- No aparecen errores YAML.
- `services.bff.environment` se interpreta correctamente.
- Las variables de los servicios se muestran en la configuración procesada.

---

### 7.2 Construir y levantar contenedores

```cmd
docker compose up -d --build
```

También se puede reconstruir todo desde cero:

```cmd
docker compose down
docker compose build --no-cache
docker compose up -d
```

---

### 7.3 Revisar estado

```cmd
docker compose ps
```

Resultado esperado:

- 8 contenedores PostgreSQL.
- 9 microservicios.
- 1 BFF.
- Total: 18 contenedores.
- Las bases deben aparecer como `healthy`.
- Los servicios deben aparecer como `Up`.

---

### 7.4 Revisar logs

Todos los servicios:

```cmd
docker compose logs
```

BFF en tiempo real:

```cmd
docker compose logs -f bff
```

Últimas 150 líneas:

```cmd
docker compose logs --tail=150 bff
```

Resultado esperado en el BFF:

```text
Tomcat started on port 5000
Started BffApplication
```

---

## 8. Flujo general de pruebas

Orden recomendado:

1. Levantar Docker Compose.
2. Comprobar los 18 contenedores.
3. Abrir Swagger.
4. Registrar un usuario.
5. Iniciar sesión.
6. Copiar el JWT.
7. Autorizar Swagger.
8. Consultar la sesión.
9. Crear o actualizar un perfil.
10. Crear libros.
11. Crear un préstamo.
12. Confirmar cambio de disponibilidad.
13. Registrar devolución.
14. Revisar multa si corresponde.
15. Crear reserva.
16. Consultar notificaciones.
17. Consultar reportes.
18. Consultar dashboard.
19. Probar reglas inválidas.
20. Probar GlitchTip.
21. Ejecutar tests automatizados.
22. Abrir JaCoCo.

---

# PARTE I: AUTENTICACIÓN, SESIÓN Y SEGURIDAD

## 9. Pruebas de registro

Endpoint:

```http
POST /register
```

Request:

```json
{
  "email": "usuario@test.com",
  "password": "Test1234"
}
```

Resultado esperado:

- Código HTTP `200 OK` o `201 Created`.
- Usuario registrado.
- Token JWT en la respuesta.

Ejemplo:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Caso de error

Registrar nuevamente el mismo correo.

Resultado esperado:

- Error controlado.
- No se debe crear un usuario duplicado.
- El sistema debe entregar un código `400`, `409` o el configurado por la regla.

---

## 10. Pruebas de login

Endpoint:

```http
POST /login
```

Request:

```json
{
  "email": "usuario@test.com",
  "password": "Test1234"
}
```

Resultado esperado:

- `200 OK`.
- JWT válido.

### Credenciales incorrectas

```json
{
  "email": "usuario@test.com",
  "password": "incorrecta"
}
```

Resultado esperado:

- Error controlado.
- No se entrega token.

---

## 11. Autorizar Swagger

1. Copiar el JWT.
2. Presionar **Authorize**.
3. Pegar:

```text
Bearer TOKEN
```

Según la configuración de Swagger, también puede bastar con pegar solamente el token.

---

## 12. Consultar sesión actual

Endpoint:

```http
GET /session
```

Header:

```http
Authorization: Bearer TOKEN
```

Resultado esperado:

- `200 OK`.
- Correo o datos identificadores del usuario autenticado.
- Confirmación de que la sesión JWT está activa.

---

## 13. Endpoint protegido sin token

Ejemplo:

```http
GET /books
```

Sin header de autorización.

Resultado esperado:

```text
401 Unauthorized
```

---

## 14. Endpoint protegido con token inválido

Header:

```http
Authorization: Bearer TOKEN_MODIFICADO
```

Resultado esperado:

```text
401 Unauthorized
```

---

## 15. Endpoint protegido con token válido

Header:

```http
Authorization: Bearer TOKEN_VALIDO
```

Resultado esperado:

- La solicitud llega al controller.
- Se obtiene `200`, `201`, `400` o `404` según la operación.
- No se obtiene `401`.

---

# PARTE II: PRUEBAS FUNCIONALES

## 16. Pruebas de perfiles de usuario

### 16.1 Crear o actualizar perfil

```http
PUT /users/profile
```

Request:

```json
{
  "authEmail": "usuario@test.com",
  "fullName": "Usuario de Prueba",
  "phone": "912345678",
  "address": "Santiago"
}
```

Resultado esperado:

- `200 OK`.
- Si no existe, el perfil se crea.
- Si existe, se actualiza.
- Se devuelve el perfil guardado.

---

### 16.2 Listar perfiles

```http
GET /users
```

Resultado esperado:

- `200 OK`.
- Lista de perfiles.

---

### 16.3 Eliminar perfil

```http
DELETE /users/{id}
```

Resultado esperado:

- Eliminación exitosa.
- Al consultar nuevamente, el perfil no existe.

### Caso de error

Usar un UUID inexistente.

Resultado esperado:

- `404 Not Found` o error controlado equivalente.

---

## 17. Pruebas del CRUD de libros

### 17.1 Crear libro

```http
POST /books
```

Request de ejemplo:

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "category": "Software Engineering",
  "isbn": "9780132350884",
  "available": true
}
```

Resultado esperado:

- `201 Created`.
- UUID generado.
- `available` en `true`.

---

### 17.2 Listar libros

```http
GET /books
```

Resultado esperado:

- `200 OK`.
- Lista con el libro creado.

---

### 17.3 Buscar por ID

```http
GET /books/{id}
```

Resultado esperado:

- `200 OK` para ID existente.
- `404 Not Found` para ID inexistente.

---

### 17.4 Buscar por filtros

```http
GET /books/search?title=Clean
```

Otros filtros posibles:

```text
author
category
isbn
```

Resultado esperado:

- Lista de coincidencias.
- Lista vacía si no existen resultados, según implementación.

---

### 17.5 Actualizar libro

```http
PUT /books/{id}
```

Request:

```json
{
  "title": "Clean Code Updated",
  "author": "Robert C. Martin",
  "category": "Programming",
  "isbn": "9780132350884",
  "available": true
}
```

Resultado esperado:

- `200 OK`.
- Datos modificados.

---

### 17.6 Eliminar libro

```http
DELETE /books/{id}
```

Resultado esperado:

- `200 OK` o `204 No Content`.
- El libro deja de aparecer.

---

### 17.7 Validaciones

Probar campos vacíos:

```json
{
  "title": "",
  "author": "",
  "category": "",
  "isbn": ""
}
```

Resultado esperado:

- `400 Bad Request`.
- Mensajes de validación.

---

## 18. Pruebas de préstamos

### 18.1 Preparación

Antes de crear un préstamo:

- Debe existir un perfil de usuario.
- Debe existir un libro.
- El libro debe tener `available = true`.

---

### 18.2 Crear préstamo

```http
POST /loans
```

Request de referencia:

```json
{
  "userId": "UUID_DEL_USUARIO",
  "bookId": "UUID_DEL_LIBRO"
}
```

Confirmar el formato exacto en Swagger.

Resultado esperado:

- `201 Created` o `200 OK`.
- Estado del préstamo: `ACTIVE`.
- Fecha de préstamo asignada.
- Fecha de vencimiento asignada.
- El libro cambia a `available = false`.

---

### 18.3 Consultar préstamo por ID

```http
GET /loans/{loanId}
```

Resultado esperado:

- Información del préstamo.
- `404` para ID inexistente.

---

### 18.4 Consultar préstamos de un usuario

```http
GET /loans/user/{userId}
```

Resultado esperado:

- Lista de préstamos asociados.

---

### 18.5 Usuario inexistente

Intentar crear préstamo con un `userId` inexistente.

Resultado esperado:

- Error controlado.
- No se guarda el préstamo.
- El libro permanece disponible.

---

### 18.6 Libro inexistente

Intentar crear préstamo con un `bookId` inexistente.

Resultado esperado:

- Error controlado.
- No se guarda el préstamo.

---

### 18.7 Libro no disponible

Usar un libro con `available = false`.

Resultado esperado:

- Regla de negocio rechazada.
- No se crea un segundo préstamo.

---

### 18.8 Préstamo activo duplicado

Intentar prestar nuevamente el mismo libro mientras tiene un préstamo activo.

Resultado esperado:

- Error controlado.
- No existen dos préstamos activos para el mismo libro.

---

### 18.9 Compensación

Simular o probar un fallo en `ms-book` después de guardar inicialmente el préstamo.

Resultado esperado:

- El préstamo guardado se elimina o revierte.
- No queda una operación distribuida incompleta.
- Se registra el error correspondiente.

Esta prueba normalmente se demuestra mejor mediante un test automatizado con mocks.

---

## 19. Pruebas de devoluciones

### 19.1 Registrar devolución

```http
POST /returns
```

Request de referencia:

```json
{
  "loanId": "UUID_DEL_PRESTAMO"
}
```

Confirmar campos en Swagger.

Resultado esperado:

- Devolución registrada.
- Préstamo actualizado.
- Libro vuelve a `available = true`.
- Si existe atraso, se solicita la creación de una multa.

---

### 19.2 Consultar devolución por préstamo

```http
GET /returns/loan/{loanId}
```

Resultado esperado:

- Datos de la devolución.
- Error controlado si no existe.

---

### 19.3 Devolver dos veces

Intentar registrar una segunda devolución para el mismo préstamo.

Resultado esperado:

- Regla de negocio rechazada.
- No se duplican devoluciones.

---

### 19.4 Préstamo inexistente

Resultado esperado:

- Error controlado.
- No se modifica ningún libro.

---

### 19.5 Compensación de devolución

Simular fallo al actualizar el libro o al comunicarse con multas.

Resultado esperado:

- La operación no queda parcialmente completada.
- Se muestra un error claro.
- El error queda registrado en logs.

---

## 20. Pruebas de reservas

### 20.1 Crear reserva

```http
POST /reservations
```

Request de referencia:

```json
{
  "userId": "UUID_DEL_USUARIO",
  "bookId": "UUID_DEL_LIBRO"
}
```

Resultado esperado:

- Reserva creada.
- Estado inicial asignado.
- Notificación generada cuando corresponda.

---

### 20.2 Usuario inexistente

Resultado esperado:

- Error controlado.
- No se crea reserva.

---

### 20.3 Libro inexistente

Resultado esperado:

- Error controlado.
- No se crea reserva.

---

### 20.4 Reserva duplicada

Intentar crear más de una reserva activa equivalente.

Resultado esperado:

- Aplicación de la regla configurada.
- No se generan registros duplicados.

---

### 20.5 Falla de notificación

Simular indisponibilidad de `ms-notification`.

Resultado esperado:

- Error centralizado.
- Compensación según la implementación.
- Registro en consola.

---

## 21. Pruebas de multas

### 21.1 Consultar multa por ID

```http
GET /fines/{fineId}
```

Resultado esperado:

- `200 OK` si existe.
- `404 Not Found` si no existe.

---

### 21.2 Consultar multas por usuario

```http
GET /fines/user/{userId}
```

Resultado esperado:

- Lista de multas asociadas.

---

### 21.3 Marcar multa como pagada

```http
PATCH /fines/{fineId}/pay
```

Resultado esperado:

- La multa cambia a estado pagado.
- No puede pagarse una multa inexistente.

---

### 21.4 Contar multas

```http
GET /fines/count
```

Resultado esperado:

- Valor numérico utilizado por reportes o dashboard.

---

## 22. Pruebas de notificaciones

### 22.1 Crear notificación

Puede generarse automáticamente desde una reserva o probarse directamente en el microservicio según sus endpoints.

Request de referencia:

```json
{
  "userId": "UUID_DEL_USUARIO",
  "message": "Prueba de notificación"
}
```

Resultado esperado:

- Notificación creada.
- Campo `read` inicialmente en `false`.

---

### 22.2 Consultar notificación por ID

```http
GET /notifications/{notificationId}
```

Resultado esperado:

- Datos de la notificación.

---

### 22.3 Consultar por usuario

```http
GET /notifications/user/{userId}
```

Resultado esperado:

- Lista de notificaciones del usuario.

---

### 22.4 Marcar como leída

```http
PATCH /notifications/{notificationId}/read
```

Resultado esperado:

- `read = true`.

---

## 23. Pruebas de reportes

### 23.1 Resumen general

```http
GET /reports/general-summary
```

Resultado esperado:

- `200 OK`.
- Información construida a partir de préstamos y multas.
- `ms-report` realiza el cálculo sin base de datos propia.

---

### 23.2 Microservicio dependiente no disponible

Detener temporalmente un servicio dependiente, por ejemplo:

```cmd
docker compose stop ms-fine
```

Ejecutar el reporte.

Resultado esperado:

- Error de comunicación controlado.
- Código HTTP coherente.
- Log en BFF o `ms-report`.

Volver a iniciar:

```cmd
docker compose start ms-fine
```

---

## 24. Pruebas de dashboard

Endpoint:

```http
GET /dashboard/user/{userId}
```

Resultado esperado:

- Respuesta compuesta.
- Información obtenida desde distintos microservicios.
- Préstamos.
- Reservas.
- Multas.
- Notificaciones.

Esta prueba demuestra que el BFF no solo redirige, sino que también integra y agrega respuestas.

---

# PARTE III: LOGS, ERRORES Y GLITCHTIP

## 25. Manejo centralizado de errores

Probar como mínimo:

| Escenario | Resultado esperado |
|---|---|
| UUID inexistente | `404` o error controlado |
| Body inválido | `400 Bad Request` |
| Sin JWT | `401 Unauthorized` |
| Regla de negocio incumplida | `400` o `409` |
| Servicio interno detenido | Error externo controlado |
| Excepción inesperada | `500 Internal Server Error` |

La respuesta del BFF no debe exponer información sensible del stack trace.

Ejemplo:

```json
{
  "success": false,
  "data": null,
  "message": "Ocurrió un error interno en el servidor"
}
```

---

## 26. Logs en consola

Mantener una ventana con:

```cmd
docker compose logs -f bff
```

Probar una operación exitosa y una operación fallida.

Resultado esperado:

- `INFO` para eventos normales.
- `WARN` para errores esperados o reglas.
- `ERROR` con stack trace para errores inesperados.

---

## 27. Prueba de GlitchTip

Endpoint:

```http
GET /test/glitchtip
```

Este endpoint está protegido con JWT.

Sin token:

```cmd
curl -i http://localhost:5000/test/glitchtip
```

Resultado esperado:

```text
401 Unauthorized
```

Con token:

```cmd
curl -i http://localhost:5000/test/glitchtip ^
-H "Authorization: Bearer TOKEN_VALIDO"
```

Resultado esperado:

```text
HTTP/1.1 500
```

Respuesta esperada:

```json
{
  "success": false,
  "data": null,
  "message": "Ocurrió un error interno en el servidor"
}
```

Log esperado:

```text
IllegalStateException:
Error de prueba controlado desde Eva-3-Library BFF
```

Evento esperado en GlitchTip:

```text
IllegalStateException
Error de prueba controlado desde Eva-3-Library BFF
```

Datos esperados:

```text
Environment: examen-docker
Release: eva-3-library-bff@1.0.0
```

---

## 28. Evidencias de GlitchTip

Guardar capturas de:

1. Endpoint devolviendo `500`.
2. Stack trace en Docker.
3. Issue recibido en GlitchTip.
4. Detalle del evento.
5. Environment.
6. Release.
7. `.env.example`.
8. `.gitignore` protegiendo `.env`.

---

# PARTE IV: PRUEBAS AUTOMATIZADAS

## 29. Tecnologías de testing

- JUnit 5.
- Mockito.
- Spring Boot Test.
- MockMvc.
- H2.
- JaCoCo.

---

## 30. Tipos de pruebas

### 30.1 Tests de DTO y validaciones

Comprueban:

- Campos obligatorios.
- Tamaños mínimos y máximos.
- Correos válidos.
- Valores nulos.
- Formatos incorrectos.

### 30.2 Tests de controllers

Comprueban:

- Códigos HTTP.
- Serialización JSON.
- Rutas.
- Headers.
- Validaciones.
- Uso del service simulado.

### 30.3 Tests de services

Comprueban:

- Reglas de negocio.
- Creación, actualización y eliminación.
- Excepciones.
- Comunicación simulada.
- Compensaciones.
- Estados de las entidades.

### 30.4 Tests de repositories

Comprueban:

- Persistencia con H2.
- Búsquedas personalizadas.
- Consultas por UUID.
- Consultas por usuario.
- Estados activos.
- Existencia de registros.

### 30.5 Tests de contexto

Comprueban que la aplicación pueda cargar su contexto de Spring correctamente.

---

## 31. Casos automatizados recomendados por módulo

### BFF

- Login y registro redirigidos correctamente.
- Validación de JWT.
- `/session` con token.
- Controllers del BFF.
- Propagación de errores externos.
- `GlobalExceptionHandler`.
- Dashboard compuesto.
- `GlitchTipTestController`.

### ms-auth

- Registro exitoso.
- Correo duplicado.
- Login exitoso.
- Contraseña incorrecta.
- Generación de JWT.
- Persistencia del usuario.

### ms-book

- Crear libro.
- Listar libros.
- Buscar por ID.
- Buscar por filtros.
- Actualizar.
- Eliminar.
- Campos inválidos.
- Disponibilidad.

### ms-user

- Crear perfil.
- Actualizar perfil por correo.
- Listar perfiles.
- Eliminar.
- Buscar por email ignorando mayúsculas.
- Validaciones.

### ms-loan

- Crear préstamo exitoso.
- Usuario inexistente.
- Libro inexistente.
- Libro no disponible.
- Préstamo activo duplicado.
- Actualizar disponibilidad.
- Compensar cuando falla `ms-book`.

### ms-return

- Registrar devolución.
- Préstamo inexistente.
- Devolución duplicada.
- Liberar libro.
- Generar multa por atraso.
- Compensar fallos externos.

### ms-reservation

- Crear reserva.
- Usuario inexistente.
- Libro inexistente.
- Reserva duplicada.
- Crear notificación.
- Falla de notificación.

### ms-fine

- Crear multa.
- Consultar por ID.
- Consultar por usuario.
- Marcar como pagada.
- Multa inexistente.
- Conteo.

### ms-notification

- Crear notificación.
- Consultar por ID.
- Consultar por usuario.
- Marcar como leída.
- Notificación inexistente.

### ms-report

- Generar resumen.
- Obtener conteos.
- Respuesta vacía.
- Error de `ms-loan`.
- Error de `ms-fine`.

---

## 32. Ejecutar tests de un módulo

Ejemplo BFF:

```cmd
cd bff
gradlew.bat clean test
```

Ejemplo préstamos:

```cmd
cd ms-loan
gradlew.bat clean test
```

Resultado esperado:

```text
BUILD SUCCESSFUL
```

Reporte:

```text
build/reports/tests/test/index.html
```

---

## 33. Ejecutar tests de todos los módulos

Desde PowerShell en la raíz:

```powershell
$modules = @(
    "bff",
    "ms-auth",
    "ms-book",
    "ms-user",
    "ms-loan",
    "ms-return",
    "ms-reservation",
    "ms-fine",
    "ms-notification",
    "ms-report"
)

foreach ($module in $modules) {
    Write-Host "===================================="
    Write-Host "Ejecutando tests de $module"
    Write-Host "===================================="

    Push-Location $module
    .\gradlew.bat clean test

    if ($LASTEXITCODE -ne 0) {
        Pop-Location
        throw "Fallaron los tests de $module"
    }

    Pop-Location
}

Write-Host "TODOS LOS SERVICIOS PASARON"
```

---

# PARTE V: COBERTURA JACOCO

## 34. Generar reporte de cobertura

Dentro de cada módulo:

```cmd
gradlew.bat clean test jacocoTestReport
```

Reporte HTML:

```text
build/reports/jacoco/test/html/index.html
```

Abrir desde CMD:

```cmd
start build\reports\jacoco\test\html\index.html
```

---

## 35. Cobertura mínima

El requisito del examen es:

```text
40 % mínimo
```

Se debe comprobar cobertura en los componentes evaluados, idealmente en los 10 módulos.

Registrar:

| Módulo | Tests | Resultado | Cobertura |
|---|---:|---|---:|
| `bff` |  |  |  |
| `ms-auth` |  |  |  |
| `ms-book` |  |  |  |
| `ms-user` |  |  |  |
| `ms-loan` |  |  |  |
| `ms-return` |  |  |  |
| `ms-reservation` |  |  |  |
| `ms-fine` |  |  |  |
| `ms-notification` |  |  |  |
| `ms-report` |  |  |  |

---

## 36. Ejecutar tests y JaCoCo en todos los módulos

PowerShell:

```powershell
$modules = @(
    "bff",
    "ms-auth",
    "ms-book",
    "ms-user",
    "ms-loan",
    "ms-return",
    "ms-reservation",
    "ms-fine",
    "ms-notification",
    "ms-report"
)

foreach ($module in $modules) {
    Write-Host "===================================="
    Write-Host "Tests y JaCoCo: $module"
    Write-Host "===================================="

    Push-Location $module
    .\gradlew.bat clean test jacocoTestReport

    if ($LASTEXITCODE -ne 0) {
        Pop-Location
        throw "Falló $module"
    }

    Pop-Location
}

Write-Host "TESTS Y REPORTES GENERADOS"
```

---

## 37. Advertencia de clases distintas en JaCoCo

Si aparece:

```text
Classes in bundle do not match with execution data
Execution data for class ... does not match
```

Ejecutar:

```cmd
gradlew.bat clean
gradlew.bat test jacocoTestReport
```

Si continúa, eliminar manualmente:

```text
build/
```

y volver a ejecutar.

La causa habitual es que el archivo `.exec` fue generado con clases distintas a las compiladas para el reporte.

---

## 38. Explicación de tests para la defensa

Para cada test se debe explicar:

1. Qué clase se prueba.
2. Qué método se prueba.
3. Qué dependencia se simula.
4. Qué datos se preparan.
5. Qué acción se ejecuta.
6. Qué resultado se espera.
7. Qué regla de negocio valida.
8. Por qué el test es importante.

Ejemplo:

> El test `createLoan_whenBookIsAvailable_createsActiveLoan` prueba el método de creación de préstamos. Se simulan los clientes de usuarios y libros para que devuelvan datos válidos. Luego se verifica que el préstamo se guarde con estado ACTIVE y que se solicite el cambio de disponibilidad del libro a false.

Ejemplo de error:

> El test `createLoan_whenBookUpdateFails_deletesSavedLoan` simula un fallo al cambiar la disponibilidad del libro. Se verifica que el préstamo guardado sea eliminado como mecanismo de compensación, evitando datos inconsistentes.

---

# PARTE VI: PRUEBAS DE INTEGRACIÓN Y RESILIENCIA

## 39. Comunicación `ms-loan` → `ms-user` y `ms-book`

Prueba:

1. Crear usuario.
2. Crear libro disponible.
3. Crear préstamo.
4. Consultar libro.

Resultado esperado:

```text
Libro disponible inicialmente: true
Libro después del préstamo: false
```

---

## 40. Comunicación `ms-return` → `ms-loan`, `ms-book` y `ms-fine`

Prueba:

1. Crear préstamo.
2. Registrar devolución.
3. Consultar préstamo.
4. Consultar libro.
5. Consultar multa si existe atraso.

Resultado esperado:

- Préstamo cerrado.
- Libro disponible.
- Multa creada solamente si corresponde.

---

## 41. Comunicación `ms-reservation` → `ms-notification`

Prueba:

1. Crear reserva.
2. Consultar notificaciones del usuario.

Resultado esperado:

- Reserva registrada.
- Notificación relacionada.

---

## 42. Comunicación `ms-report` → `ms-loan` y `ms-fine`

Prueba:

1. Tener préstamos y multas registrados.
2. Ejecutar resumen general.

Resultado esperado:

- Datos coherentes.
- Sin base de datos propia en `ms-report`.

---

## 43. BFF como API Gateway

Demostrar:

- El cliente utiliza el puerto `5000`.
- El BFF consume los puertos internos.
- JWT se valida en el BFF.
- Swagger se encuentra centralizado.
- El BFF agrega datos para dashboard.
- El BFF propaga errores de servicios internos.

---

# PARTE VII: CHECKLIST FINAL

## 44. Infraestructura

- [ ] Docker Desktop está iniciado.
- [ ] `docker version` muestra Client y Server.
- [ ] `docker compose config` no muestra errores.
- [ ] Las 8 bases están `healthy`.
- [ ] Los 9 microservicios están `Up`.
- [ ] El BFF está `Up`.
- [ ] Swagger abre en puerto 5000.

---

## 45. Autenticación y sesión

- [ ] Registro exitoso.
- [ ] Login exitoso.
- [ ] JWT generado.
- [ ] Acceso sin token devuelve `401`.
- [ ] Token inválido devuelve `401`.
- [ ] Token válido permite acceder.
- [ ] `/session` identifica al usuario.

---

## 46. ms-book

- [ ] Crear.
- [ ] Listar.
- [ ] Buscar por ID.
- [ ] Buscar por filtros.
- [ ] Actualizar.
- [ ] Eliminar.
- [ ] Validaciones.
- [ ] Disponibilidad.

---

## 47. ms-user

- [ ] Crear perfil.
- [ ] Actualizar perfil.
- [ ] Listar.
- [ ] Eliminar.
- [ ] Error con ID inexistente.

---

## 48. ms-loan

- [ ] Crear préstamo.
- [ ] Consultar por ID.
- [ ] Consultar por usuario.
- [ ] Validar usuario.
- [ ] Validar libro.
- [ ] Evitar préstamo duplicado.
- [ ] Cambiar disponibilidad.
- [ ] Compensar fallo.

---

## 49. ms-return

- [ ] Registrar devolución.
- [ ] Consultar devolución.
- [ ] Liberar libro.
- [ ] Evitar devolución duplicada.
- [ ] Generar multa si corresponde.
- [ ] Manejar fallos externos.

---

## 50. ms-reservation

- [ ] Crear reserva.
- [ ] Validar usuario.
- [ ] Validar libro.
- [ ] Evitar duplicados.
- [ ] Generar notificación.

---

## 51. ms-fine

- [ ] Consultar por ID.
- [ ] Consultar por usuario.
- [ ] Marcar pagada.
- [ ] Contar multas.
- [ ] Error controlado.

---

## 52. ms-notification

- [ ] Crear.
- [ ] Consultar por ID.
- [ ] Consultar por usuario.
- [ ] Marcar leída.
- [ ] Error controlado.

---

## 53. ms-report y dashboard

- [ ] Resumen general.
- [ ] Respuesta agregada.
- [ ] Dashboard por usuario.
- [ ] Error si un servicio dependiente no responde.

---

## 54. GlitchTip

- [ ] `SENTRY_ENABLED=true`.
- [ ] DSN cargado desde `.env`.
- [ ] `.env` ignorado.
- [ ] Endpoint sin token devuelve `401`.
- [ ] Endpoint con token devuelve `500`.
- [ ] Error visible en Docker.
- [ ] Issue visible en GlitchTip.
- [ ] Environment correcto.
- [ ] Release correcta.

---

## 55. Testing automatizado

- [ ] Tests del BFF.
- [ ] Tests de `ms-auth`.
- [ ] Tests de `ms-book`.
- [ ] Tests de `ms-user`.
- [ ] Tests de `ms-loan`.
- [ ] Tests de `ms-return`.
- [ ] Tests de `ms-reservation`.
- [ ] Tests de `ms-fine`.
- [ ] Tests de `ms-notification`.
- [ ] Tests de `ms-report`.
- [ ] Todos terminan en `BUILD SUCCESSFUL`.

---

## 56. JaCoCo

- [ ] Reporte generado por módulo.
- [ ] Reporte HTML abre.
- [ ] Cobertura registrada.
- [ ] Cobertura mínima de 40 %.
- [ ] Capturas guardadas.
- [ ] Advertencias de clases incompatibles corregidas.

---

# PARTE VIII: EVIDENCIAS PARA EL EXAMEN

## 57. Capturas recomendadas

1. Repositorio de GitHub.
2. Ramas del proyecto.
3. `docker compose config`.
4. `docker compose up -d`.
5. `docker compose ps`.
6. Bases de datos `healthy`.
7. Swagger completo.
8. Registro.
9. Login.
10. JWT.
11. `/session`.
12. CRUD de libros.
13. Creación de perfil.
14. Creación de préstamo.
15. Libro no disponible.
16. Devolución.
17. Libro nuevamente disponible.
18. Multa.
19. Reserva.
20. Notificación.
21. Reporte.
22. Dashboard.
23. Error controlado.
24. Error `500`.
25. Stack trace.
26. Issue en GlitchTip.
27. Tests pasando.
28. Reporte JaCoCo.
29. Diagramas C2 y C3.
30. Historial de commits.

---

## 58. Orden recomendado para presentar

1. Explicar arquitectura C2.
2. Explicar estructura C3.
3. Mostrar repositorio y ramas.
4. Mostrar Docker Compose.
5. Mostrar los 18 contenedores.
6. Abrir Swagger.
7. Registrar e iniciar sesión.
8. Mostrar `/session`.
9. Ejecutar CRUD.
10. Ejecutar flujo de préstamo y devolución.
11. Mostrar comunicación entre servicios.
12. Mostrar error centralizado.
13. Mostrar Docker logs.
14. Mostrar GlitchTip.
15. Mostrar tests.
16. Mostrar JaCoCo.
17. Explicar Database per Service.
18. Explicar por qué `ms-report` no tiene base de datos.

---

## 59. Errores frecuentes

### Docker no está iniciado

```text
failed to connect to the docker API
dockerDesktopLinuxEngine
```

Solución:

```cmd
wsl --shutdown
```

Luego abrir Docker Desktop y esperar `Engine running`.

---

### Error YAML del BFF

```text
services.bff.environment must be a mapping
```

Solución:

Las variables deben quedar indentadas dentro de `environment:`.

Correcto:

```yaml
environment:
  AUTH_SERVICE_URL: "http://ms-auth:5003"
  SENTRY_ENABLED: "${SENTRY_ENABLED:-false}"
```

---

### Endpoint de GlitchTip devuelve 401

Causa:

- Falta JWT.

Solución:

```http
Authorization: Bearer TOKEN
```

---

### Endpoint de GlitchTip devuelve 404

Causa:

- Imagen antigua del BFF.

Solución:

```cmd
docker compose stop bff
docker compose rm -f bff
docker compose build --no-cache bff
docker compose up -d --no-deps bff
```

---

### Servicio no puede conectarse a PostgreSQL

Revisar:

```cmd
docker compose ps
docker compose logs NOMBRE_SERVICIO
docker compose logs NOMBRE_BASE
```

Comprobar que la base esté `healthy`.

---

### Tests intentan conectarse a PostgreSQL real

Revisar:

```text
src/test/resources/application.properties
```

Los tests deben utilizar H2 cuando corresponda.

---

### Git no agrega `.env.example`

Causa:

- El archivo no existe.

Comprobar:

```cmd
dir .env.example
```

Crear desde CMD:

```cmd
(
echo SENTRY_ENABLED=false
echo SENTRY_DSN=COLOCAR_DSN_DE_GLITCHTIP
echo SENTRY_ENVIRONMENT=development
echo SENTRY_RELEASE=library-microservices-bff@1.0.0
echo SENTRY_DEBUG=false
) > .env.example
```

---

## 60. Resultado esperado general

Al finalizar todas las pruebas se debe demostrar que:

- El BFF funciona como entrada principal y API Gateway.
- Los 9 microservicios tienen responsabilidades separadas.
- Las 8 bases respetan Database per Service.
- `ms-report` funciona sin base propia.
- JWT protege los endpoints.
- La sesión puede consultarse.
- El CRUD funciona.
- Los microservicios se comunican.
- Las reglas de negocio se aplican.
- Los errores son centralizados.
- Los logs se muestran en Docker.
- Los errores inesperados llegan a GlitchTip.
- Los tests automatizados pasan.
- JaCoCo genera los reportes.
- La cobertura cumple el mínimo solicitado.
- Docker Compose levanta la solución completa.

---

## 61. Conclusión

Esta guía permite comprobar el funcionamiento integral de **Library Microservices**, desde la infraestructura hasta las reglas de negocio.

Las pruebas manuales demuestran los flujos funcionales, de seguridad y de integración. Las pruebas automatizadas validan los controllers, services, repositories, DTOs, excepciones y compensaciones. JaCoCo entrega evidencia de cobertura y GlitchTip permite demostrar observabilidad y monitoreo de errores.

Con la ejecución completa de esta guía, el proyecto queda preparado para su entrega, defensa individual y evaluación académica