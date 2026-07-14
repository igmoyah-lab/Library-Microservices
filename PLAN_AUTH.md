# Plan de Autenticación - Library Microservices

## 1. Objetivo

Implementar un mecanismo de autenticación seguro para **Library Microservices**, utilizando **Spring Security** y **JWT**, manteniendo la responsabilidad de registro y validación de credenciales en el microservicio independiente `ms-auth`.

El sistema utiliza el **BFF** como punto de entrada principal y como API Gateway. El cliente no consume directamente los microservicios internos para las operaciones normales, sino que realiza sus solicitudes a través del BFF.

Los objetivos principales son:

- Permitir el registro de usuarios.
- Permitir el inicio de sesión.
- Almacenar contraseñas cifradas.
- Generar tokens JWT.
- Validar tokens JWT en el BFF.
- Proteger los endpoints privados.
- Permitir el acceso público a login, registro y Swagger.
- Consultar la sesión del usuario autenticado.
- Evitar que cada microservicio implemente autenticación por separado.
- Mantener una arquitectura stateless.
- Integrar logs y monitoreo de errores.
- Facilitar pruebas mediante Swagger, Postman y curl.

---

## 2. Alcance

Este plan considera la seguridad de:

- BFF / API Gateway.
- `ms-auth`.
- `ms-book`.
- `ms-user`.
- `ms-loan`.
- `ms-return`.
- `ms-reservation`.
- `ms-fine`.
- `ms-notification`.
- `ms-report`.

La autenticación se valida en el BFF antes de permitir el acceso a las funcionalidades protegidas.

---

## 3. Arquitectura de autenticación

```text
Cliente / Swagger / Postman / curl
                  |
                  v
          BFF / API Gateway
             Puerto 5000
                  |
          Valida token JWT
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
ms-fine       ms-notification  ms-report
Puerto 5007   Puerto 5008      Puerto 5009
```

Flujo de autenticación:

```text
Cliente
   |
   | POST /register o POST /login
   v
BFF
   |
   | Solicitud interna
   v
ms-auth
   |
   | Consulta o guarda credenciales
   v
PostgreSQL Auth
```

Flujo de endpoint protegido:

```text
Cliente
   |
   | Authorization: Bearer JWT
   v
BFF
   |
   | Valida firma, expiración y usuario
   v
Microservicio correspondiente
```

---

## 4. Principios de seguridad aplicados

- Separación de responsabilidades.
- Autenticación centralizada.
- Arquitectura stateless.
- Contraseñas cifradas.
- Token firmado.
- Validación de expiración.
- Rutas públicas limitadas.
- Endpoints de negocio protegidos.
- Respuestas `401 Unauthorized` para token ausente o inválido.
- No exponer stack traces al cliente.
- Variables privadas mediante `.env`.
- No subir secretos a GitHub.
- Monitoreo de errores mediante GlitchTip.
- Logs mediante SLF4J.

---

## 5. Responsabilidades por módulo

## 5.1 BFF / API Gateway

Responsabilidades:

- Recibir las solicitudes externas.
- Exponer `/register` y `/login`.
- Redirigir autenticación hacia `ms-auth`.
- Validar JWT mediante Spring Security.
- Extraer el correo desde el token.
- Exponer `GET /session`.
- Permitir Swagger y OpenAPI sin autenticación.
- Proteger endpoints de negocio.
- Consumir los nueve microservicios.
- Propagar respuestas y códigos HTTP.
- Manejar errores de comunicación.
- Registrar errores en consola.
- Enviar errores inesperados a GlitchTip.

El BFF no debe almacenar contraseñas.

---

## 5.2 ms-auth

Responsabilidades:

- Registrar usuarios.
- Validar que el correo no esté duplicado.
- Cifrar contraseñas.
- Validar credenciales.
- Generar JWT.
- Definir fecha de emisión.
- Definir fecha de expiración.
- Firmar el token con la clave configurada.
- Responder al BFF.

`ms-auth` mantiene su propia base PostgreSQL.

---

## 5.3 ms-user

Responsabilidades relacionadas con autenticación:

- No genera tokens.
- No valida contraseñas.
- No realiza login.
- Administra perfiles.
- Relaciona el perfil con `authEmail`.
- Permite crear o actualizar datos personales.
- Se consume mediante rutas protegidas del BFF.

---

## 5.4 ms-book

Responsabilidades relacionadas con autenticación:

- No registra usuarios.
- No genera JWT.
- Administra libros.
- Sus endpoints se consumen mediante el BFF.
- Las operaciones de negocio requieren una sesión válida.

---

## 5.5 ms-loan

Responsabilidades relacionadas con autenticación:

- No autentica usuarios.
- Recibe solicitudes autorizadas desde el BFF.
- Utiliza el ID de usuario para crear y consultar préstamos.
- Valida al usuario consultando `ms-user`.

---

## 5.6 ms-return

Responsabilidades relacionadas con autenticación:

- No valida JWT.
- Procesa devoluciones solicitadas desde el BFF.
- Se comunica con préstamos, libros y multas.

---

## 5.7 ms-reservation

Responsabilidades relacionadas con autenticación:

- No realiza login.
- Recibe solicitudes autorizadas.
- Valida usuario y libro.
- Puede generar notificaciones.

---

## 5.8 ms-fine

Responsabilidades relacionadas con autenticación:

- Gestiona multas.
- Recibe solicitudes protegidas desde el BFF.
- No almacena credenciales.

---

## 5.9 ms-notification

Responsabilidades relacionadas con autenticación:

- Gestiona notificaciones.
- Permite consultar notificaciones de un usuario.
- No genera ni valida tokens.

---

## 5.10 ms-report

Responsabilidades relacionadas con autenticación:

- Genera reportes.
- No posee base de datos propia.
- Se consume desde el BFF mediante una sesión válida.

---

## 6. Flujo completo de autenticación

```mermaid
sequenceDiagram
    participant Client as Cliente
    participant BFF as BFF / API Gateway
    participant Auth as ms-auth
    participant AuthDB as PostgreSQL Auth
    participant Service as Microservicio protegido

    Client->>BFF: POST /register
    BFF->>Auth: Solicitud de registro
    Auth->>AuthDB: Verificar correo
    Auth->>Auth: Cifrar contraseña
    Auth->>AuthDB: Guardar usuario
    Auth->>Auth: Generar JWT
    Auth-->>BFF: JWT
    BFF-->>Client: JWT

    Client->>BFF: POST /login
    BFF->>Auth: Solicitud de login
    Auth->>AuthDB: Buscar usuario
    Auth->>Auth: Comparar contraseña cifrada
    Auth->>Auth: Generar JWT
    Auth-->>BFF: JWT
    BFF-->>Client: JWT

    Client->>BFF: GET endpoint protegido + Bearer JWT
    BFF->>BFF: Validar firma y expiración
    BFF->>BFF: Extraer correo autenticado
    BFF->>Service: Redirigir solicitud
    Service-->>BFF: Respuesta
    BFF-->>Client: Respuesta autorizada
```

---

## 7. Registro de usuario

Endpoint público:

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

```text
200 OK o 201 Created
```

Ejemplo de respuesta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Reglas:

- El correo debe ser válido.
- La contraseña no puede estar vacía.
- El correo no debe existir.
- La contraseña debe almacenarse cifrada.
- Nunca se debe devolver la contraseña.
- El token debe incluir el correo como subject.

---

## 8. Inicio de sesión

Endpoint público:

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

```text
200 OK
```

Respuesta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Reglas:

- El usuario debe existir.
- La contraseña debe coincidir.
- No se debe indicar de forma excesivamente detallada si falló el correo o la contraseña.
- El token debe tener expiración.
- El token debe estar firmado.

---

## 9. Estructura conceptual del JWT

El token contiene tres partes:

```text
HEADER.PAYLOAD.SIGNATURE
```

Ejemplo conceptual del payload:

```json
{
  "sub": "usuario@test.com",
  "iat": 1783986041,
  "exp": 1784072441
}
```

Campos:

| Campo | Significado |
|---|---|
| `sub` | Correo o identificador del usuario |
| `iat` | Fecha de emisión |
| `exp` | Fecha de expiración |

El token no debe almacenar:

- Contraseña.
- Datos bancarios.
- Información sensible innecesaria.
- Claves privadas.

---

## 10. Uso del token

Header:

```http
Authorization: Bearer TOKEN
```

Ejemplo:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Desde CMD:

```cmd
curl -i http://localhost:5000/books ^
-H "Authorization: Bearer TOKEN"
```

---

## 11. Sesión de usuario

Endpoint protegido:

```http
GET /session
```

Objetivo:

- Confirmar que el JWT fue aceptado.
- Obtener el correo del usuario autenticado.
- Demostrar una sesión stateless.
- Evitar mantener una sesión tradicional en memoria del servidor.

Resultado esperado:

```text
200 OK
```

Ejemplo conceptual:

```json
{
  "authenticated": true,
  "email": "usuario@test.com"
}
```

---

## 12. Endpoints públicos

Deben mantenerse públicos:

```http
POST /register
POST /login
```

También:

```text
/swagger-ui/**
/swagger-ui.html
/v3/api-docs/**
```

El endpoint de salud puede mantenerse público si se implementa y se considera necesario.

---

## 13. Endpoints protegidos

Todos los endpoints de negocio deben requerir JWT.

Ejemplos:

### Libros

```http
GET    /books
POST   /books
GET    /books/{id}
PUT    /books/{id}
DELETE /books/{id}
GET    /books/search
```

### Usuarios

```http
GET    /users
PUT    /users/profile
DELETE /users/{id}
```

### Préstamos

```http
POST /loans
GET  /loans/{loanId}
GET  /loans/user/{userId}
```

### Devoluciones

```http
POST /returns
GET  /returns/loan/{loanId}
```

### Reservas

```http
POST /reservations
```

### Multas

```http
GET   /fines/{fineId}
GET   /fines/user/{userId}
PATCH /fines/{fineId}/pay
GET   /fines/count
```

### Notificaciones

```http
GET   /notifications/{notificationId}
GET   /notifications/user/{userId}
PATCH /notifications/{notificationId}/read
```

### Reportes

```http
GET /reports/general-summary
```

### Dashboard

```http
GET /dashboard/user/{userId}
```

### GlitchTip

```http
GET /test/glitchtip
```

El endpoint de GlitchTip debe permanecer protegido para evitar que cualquier persona genere errores de prueba.

---

## 14. Configuración conceptual de Spring Security

Ejemplo del BFF:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers(
        "/login",
        "/register",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**"
    ).permitAll()
    .anyRequest().authenticated()
)
```

También se recomienda:

```java
.sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

Y desactivar CSRF para una API REST stateless:

```java
.csrf(csrf -> csrf.disable())
```

El filtro JWT debe ejecutarse antes del filtro estándar de autenticación por usuario y contraseña.

---

## 15. Proceso del filtro JWT

El filtro debe:

1. Leer el header `Authorization`.
2. Comprobar que comience con `Bearer `.
3. Extraer el token.
4. Validar firma.
5. Validar expiración.
6. Extraer el correo.
7. Crear la autenticación.
8. Guardarla en el `SecurityContext`.
9. Continuar la cadena de filtros.

Pseudoflujo:

```text
Request
   |
   v
¿Existe Authorization?
   |
   +-- No --> Continuar sin autenticación
   |
   +-- Sí --> ¿Comienza con Bearer?
                 |
                 +-- No --> Continuar o rechazar
                 |
                 +-- Sí --> Validar token
                               |
                               +-- Inválido --> 401
                               |
                               +-- Válido --> SecurityContext
```

---

## 16. Configuración de Swagger con JWT

Ejemplo:

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

Resultado:

- Swagger muestra el botón **Authorize**.
- El token se envía automáticamente.
- Los endpoints protegidos se pueden probar desde el navegador.

---

## 17. Variables de entorno

### BFF

```env
JWT_SECRET=CLAVE_SECRETA_LARGA
JWT_EXPIRATION_HOURS=24
AUTH_SERVICE_URL=http://ms-auth:5003
```

### ms-auth

```env
JWT_SECRET=CLAVE_SECRETA_LARGA
JWT_EXPIRATION_HOURS=24
AUTH_DB_URL=jdbc:postgresql://auth-postgres:5432/authdb
AUTH_DB_USERNAME=authuser
AUTH_DB_PASSWORD=authpass
```

La misma clave debe ser utilizada por:

- `ms-auth` para firmar.
- BFF para validar.

Si no coinciden, todos los tokens serán rechazados.

---

## 18. Configuración en Docker Compose

Ejemplo conceptual:

```yaml
ms-auth:
  environment:
    AUTH_DB_URL: jdbc:postgresql://auth-postgres:5432/authdb
    AUTH_DB_USERNAME: authuser
    AUTH_DB_PASSWORD: authpass
    JWT_SECRET: ${JWT_SECRET}
    JWT_EXPIRATION_HOURS: ${JWT_EXPIRATION_HOURS:-24}

bff:
  environment:
    AUTH_SERVICE_URL: http://ms-auth:5003
    JWT_SECRET: ${JWT_SECRET}
    JWT_EXPIRATION_HOURS: ${JWT_EXPIRATION_HOURS:-24}
```

Para una entrega académica se puede usar una clave de ejemplo, pero para una solución real debe manejarse mediante secretos y no quedar escrita directamente en el repositorio.

---

## 19. Archivo `.env`

Ejemplo:

```env
JWT_SECRET=CAMBIAR_POR_UNA_CLAVE_SEGURA_Y_LARGA
JWT_EXPIRATION_HOURS=24

SENTRY_ENABLED=true
SENTRY_DSN=COLOCAR_DSN
SENTRY_ENVIRONMENT=examen-docker
SENTRY_RELEASE=library-microservices-bff@1.0.0
SENTRY_DEBUG=false
```

El archivo `.env` no debe subirse.

Comprobar:

```cmd
git check-ignore .env
```

Resultado esperado:

```text
.env
```

---

## 20. Cifrado de contraseñas

Las contraseñas deben almacenarse usando un algoritmo de hash adaptativo, por ejemplo BCrypt.

Concepto:

```java
passwordEncoder.encode(password)
```

Validación:

```java
passwordEncoder.matches(rawPassword, encodedPassword)
```

Nunca se debe:

- Guardar la contraseña en texto plano.
- Registrar la contraseña en logs.
- Retornar la contraseña en JSON.
- Incluir la contraseña en el JWT.

---

## 21. Reglas de seguridad

- `/register` es público.
- `/login` es público.
- Swagger es público.
- `/session` es protegido.
- Todo endpoint de negocio es protegido.
- El BFF valida el token.
- Los microservicios no generan tokens.
- Las contraseñas se almacenan cifradas.
- JWT debe estar firmado.
- JWT debe tener expiración.
- JWT inválido devuelve `401`.
- JWT ausente devuelve `401`.
- La clave debe ser suficientemente larga.
- No se deben subir secretos.
- No se deben registrar tokens completos en logs.
- Los errores internos no deben exponer detalles sensibles.

---

## 22. Manejo de errores de autenticación

### Usuario duplicado

Resultado esperado:

```text
400 Bad Request o 409 Conflict
```

### Credenciales incorrectas

Resultado esperado:

```text
401 Unauthorized
```

### Token ausente

Resultado esperado:

```text
401 Unauthorized
```

### Token modificado

Resultado esperado:

```text
401 Unauthorized
```

### Token expirado

Resultado esperado:

```text
401 Unauthorized
```

### Error de ms-auth

Resultado esperado:

- Error controlado por el BFF.
- Código HTTP coherente.
- Log de advertencia o error.
- Sin exponer el stack trace al cliente.

---

## 23. Logs de seguridad

Eventos que conviene registrar:

- Registro exitoso.
- Intento de correo duplicado.
- Login exitoso.
- Login fallido.
- Token inválido.
- Token expirado.
- Error al comunicarse con `ms-auth`.
- Error inesperado.

No registrar:

- Contraseñas.
- JWT completos.
- Claves secretas.
- DSN.
- Datos sensibles.

---

## 24. Integración con GlitchTip

GlitchTip se utiliza para errores inesperados del BFF.

Ejemplo de flujo:

```text
Excepción inesperada
        |
        v
GlobalExceptionHandler
        |
        +--> log.error en Docker
        |
        +--> Sentry.captureException
                   |
                   v
                GlitchTip
```

Los errores de autenticación esperados, como token inválido, no deberían enviarse como incidentes críticos.

Los errores `500` sí pueden enviarse.

---

## 25. Prueba manual de registro

CMD:

```cmd
curl -X POST http://localhost:5000/register ^
-H "Content-Type: application/json" ^
-d "{\"email\":\"usuario@test.com\",\"password\":\"Test1234\"}"
```

Resultado esperado:

```json
{
  "token": "..."
}
```

---

## 26. Prueba manual de login

```cmd
curl -X POST http://localhost:5000/login ^
-H "Content-Type: application/json" ^
-d "{\"email\":\"usuario@test.com\",\"password\":\"Test1234\"}"
```

Resultado esperado:

```json
{
  "token": "..."
}
```

---

## 27. Prueba de sesión

```cmd
curl -i http://localhost:5000/session ^
-H "Authorization: Bearer TOKEN"
```

Resultado esperado:

```text
HTTP/1.1 200
```

---

## 28. Prueba sin token

```cmd
curl -i http://localhost:5000/books
```

Resultado esperado:

```text
HTTP/1.1 401
```

---

## 29. Prueba con token válido

```cmd
curl -i http://localhost:5000/books ^
-H "Authorization: Bearer TOKEN_VALIDO"
```

Resultado esperado:

```text
HTTP/1.1 200
```

---

## 30. Prueba con token inválido

```cmd
curl -i http://localhost:5000/books ^
-H "Authorization: Bearer token-modificado"
```

Resultado esperado:

```text
HTTP/1.1 401
```

---

## 31. Prueba de token expirado

Procedimiento recomendado:

1. Configurar temporalmente una expiración corta.
2. Iniciar sesión.
3. Esperar que el token expire.
4. Consumir un endpoint protegido.

Resultado esperado:

```text
401 Unauthorized
```

Después se debe restaurar la expiración normal.

---

## 32. Pruebas en Swagger

1. Abrir:

```text
http://localhost:5000/swagger-ui/index.html
```

2. Ejecutar `POST /register`.
3. Ejecutar `POST /login`.
4. Copiar el JWT.
5. Presionar **Authorize**.
6. Pegar el JWT.
7. Ejecutar `GET /session`.
8. Ejecutar `GET /books`.
9. Confirmar respuesta autorizada.
10. Retirar el token.
11. Repetir `GET /books`.
12. Confirmar `401 Unauthorized`.

---

## 33. Pruebas automatizadas del BFF

Casos recomendados:

- Ruta pública `/register`.
- Ruta pública `/login`.
- Swagger público.
- Endpoint protegido sin token.
- Endpoint protegido con JWT.
- Endpoint `/session`.
- Token inválido.
- Token expirado.
- Filtro JWT.
- Cliente de `ms-auth`.
- Manejo de excepción externa.
- `GlobalExceptionHandler`.

---

## 34. Pruebas automatizadas de ms-auth

Casos recomendados:

- Registro exitoso.
- Correo duplicado.
- Password cifrado.
- Login exitoso.
- Password incorrecto.
- Usuario inexistente.
- Token generado.
- Subject correcto.
- Fecha de emisión.
- Fecha de expiración.
- Token válido.
- Token modificado.
- Persistencia con H2.

---

## 35. Ejemplo conceptual de test de registro

```java
@Test
void register_whenEmailIsNew_returnsToken() {
    // Arrange
    // Se prepara un correo no registrado.

    // Act
    // Se ejecuta el registro.

    // Assert
    // Se verifica el guardado, cifrado y token.
}
```

Explicación para la defensa:

> El test valida que un correo nuevo pueda registrarse, que la contraseña sea cifrada antes de guardarse y que el servicio retorne un JWT válido.

---

## 36. Ejemplo conceptual de test de login incorrecto

```java
@Test
void login_whenPasswordIsWrong_throwsException() {
    // Arrange
    // Existe el usuario, pero la contraseña no coincide.

    // Act / Assert
    // Se espera una excepción de autenticación.
}
```

Explicación:

> El test comprueba que una contraseña incorrecta no genere un token y que el acceso sea rechazado.

---

## 37. Ejecutar tests del BFF

```cmd
cd bff
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

## 38. Ejecutar tests de ms-auth

```cmd
cd ms-auth
gradlew.bat clean test
```

Resultado esperado:

```text
BUILD SUCCESSFUL
```

---

## 39. JaCoCo

BFF:

```cmd
cd bff
gradlew.bat clean test jacocoTestReport
```

ms-auth:

```cmd
cd ms-auth
gradlew.bat clean test jacocoTestReport
```

Reporte:

```text
build/reports/jacoco/test/html/index.html
```

Requisito:

```text
40 % mínimo
```

---

## 40. Evidencias para la defensa

Guardar capturas de:

1. Swagger público.
2. Registro.
3. Login.
4. JWT generado.
5. Botón Authorize.
6. `/session`.
7. Endpoint sin token devolviendo `401`.
8. Endpoint con token devolviendo `200`.
9. Token modificado devolviendo `401`.
10. Configuración pública y privada.
11. Contraseña cifrada en base de datos, sin mostrar datos sensibles.
12. Tests de BFF.
13. Tests de `ms-auth`.
14. Reportes JaCoCo.
15. Docker Compose.
16. Contenedores `ms-auth`, BFF y PostgreSQL Auth.
17. Logs.
18. GlitchTip para errores inesperados.

---

## 41. Checklist de autenticación

### Registro y login

- [ ] `/register` funciona.
- [ ] `/login` funciona.
- [ ] Correo duplicado es rechazado.
- [ ] Contraseña incorrecta es rechazada.
- [ ] JWT es generado.
- [ ] JWT contiene subject.
- [ ] JWT contiene expiración.

### Seguridad del BFF

- [ ] Swagger es público.
- [ ] Login es público.
- [ ] Registro es público.
- [ ] `/session` es protegido.
- [ ] Endpoints de libros son protegidos.
- [ ] Endpoints de usuarios son protegidos.
- [ ] Endpoints de préstamos son protegidos.
- [ ] Endpoints de devoluciones son protegidos.
- [ ] Endpoints de reservas son protegidos.
- [ ] Endpoints de multas son protegidos.
- [ ] Endpoints de notificaciones son protegidos.
- [ ] Endpoints de reportes son protegidos.
- [ ] Dashboard es protegido.
- [ ] GlitchTip test es protegido.

### Tokens

- [ ] Token válido permite acceso.
- [ ] Token ausente devuelve `401`.
- [ ] Token modificado devuelve `401`.
- [ ] Token expirado devuelve `401`.
- [ ] La misma clave se usa al firmar y validar.
- [ ] El JWT no incluye información sensible.

### Contraseñas

- [ ] Contraseñas cifradas.
- [ ] No se devuelven contraseñas.
- [ ] No se registran contraseñas en logs.
- [ ] La validación usa `matches`.

### Configuración

- [ ] `.env` está ignorado.
- [ ] `.env.example` existe.
- [ ] JWT secret no se expone.
- [ ] Variables se cargan en Docker.
- [ ] `docker compose config` es válido.

### Testing

- [ ] Tests del BFF pasan.
- [ ] Tests de `ms-auth` pasan.
- [ ] JaCoCo se genera.
- [ ] Cobertura mínima alcanzada.

---

## 42. Errores frecuentes

### 401 en todos los endpoints

Revisar:

- Token copiado correctamente.
- Header `Bearer`.
- Token no expirado.
- Clave de BFF y `ms-auth`.
- Hora del sistema.

---

### Registro funciona, pero el token no sirve

Causa probable:

- `ms-auth` firma con una clave.
- BFF valida con otra clave.

Solución:

- Configurar el mismo `JWT_SECRET`.

---

### Swagger no muestra Authorize

Revisar:

- `OpenApiConfig`.
- `@SecurityScheme`.
- Nombre `bearerAuth`.
- Dependencia de SpringDoc.

---

### Login devuelve 500

Revisar:

```cmd
docker compose logs --tail=150 ms-auth
docker compose logs --tail=150 bff
```

Posibles causas:

- Base de datos no saludable.
- Migración pendiente.
- Variable incorrecta.
- Error de cifrado.
- Usuario no encontrado sin manejo centralizado.

---

### Token válido devuelve 404

El token fue aceptado, pero la ruta no existe.

Revisar:

- Endpoint correcto.
- Controller incluido en la imagen.
- BFF reconstruido.

---

### Token válido devuelve 403

Revisar:

- Reglas de Spring Security.
- CSRF.
- Roles o authorities.
- Configuración del filtro.

---

## 43. Mejoras futuras

- Implementar roles.
- Agregar permisos por endpoint.
- Crear rol administrador.
- Crear rol usuario.
- Implementar refresh token.
- Revocar tokens.
- Recuperación de contraseña.
- Verificación de correo.
- Bloqueo por intentos fallidos.
- Rate limiting.
- Auditoría.
- Rotación de claves.
- Secret manager.
- HTTPS.
- Cookies HttpOnly para clientes web.
- Autenticación multifactor.
- OpenID Connect.
- OAuth 2.0.
- Logout con lista de revocación.
- Alertas de intentos sospechosos.

---

## 44. Riesgos y consideraciones

### JWT no se puede invalidar fácilmente

Al ser stateless, un token válido normalmente continúa funcionando hasta expirar.

Mitigación futura:

- Expiración corta.
- Refresh token.
- Lista de revocación.

### Clave expuesta

Si `JWT_SECRET` se publica, un tercero podría firmar tokens.

Mitigación:

- `.env`.
- Secretos de Docker.
- Rotación inmediata.
- No incluir claves en documentación pública.

### Token almacenado inseguramente

En una aplicación web, `localStorage` puede ser vulnerable a XSS.

Mitigación futura:

- Cookies `HttpOnly`.
- Content Security Policy.
- Sanitización de entradas.

---

## 45. Explicación para la defensa

> La autenticación está separada en `ms-auth`, que registra usuarios, cifra contraseñas, valida credenciales y genera JWT. El BFF funciona como API Gateway y valida el token antes de permitir el acceso a cualquiera de los endpoints privados. Los demás microservicios se concentran en sus responsabilidades de negocio y no duplican la lógica de seguridad. La sesión es stateless, porque el servidor no guarda una sesión tradicional; la identidad se obtiene desde el JWT en cada solicitud.

---

## 46. Resultado esperado general

Al terminar la implementación se debe demostrar que:

- `ms-auth` registra usuarios.
- `ms-auth` valida credenciales.
- Las contraseñas están cifradas.
- Se genera un JWT.
- El BFF valida el JWT.
- Las rutas públicas funcionan sin token.
- Las rutas privadas rechazan solicitudes sin token.
- `/session` obtiene el usuario autenticado.
- Los nueve microservicios se consumen mediante el BFF.
- Los secretos no se suben a GitHub.
- Los errores se manejan correctamente.
- Los tests pasan.
- JaCoCo genera cobertura.
- Docker Compose levanta autenticación, BFF y bases.
- GlitchTip registra errores inesperados.

---

## 47. Conclusión

El plan de autenticación de **Library Microservices** separa correctamente las responsabilidades de seguridad.

`ms-auth` administra las credenciales y la generación de tokens, mientras que el BFF actúa como API Gateway, filtro de seguridad y punto de entrada único para el cliente.

La autenticación JWT permite proteger los endpoints sin mantener sesiones tradicionales en el servidor. La integración con Docker, pruebas automatizadas, JaCoCo, logs y GlitchTip entrega una solución completa y demostrable para la evaluación académica.

Este diseño permite que los microservicios de libros, usuarios, préstamos, devoluciones, reservas, multas, notificaciones y reportes permanezcan enfocados en sus reglas de negocio, sin duplicar la lógica de autenticación.