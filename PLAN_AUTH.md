# Plan de Autenticación - SystemLibrary

## 1. Objetivo

Implementar un mecanismo de autenticación seguro para el sistema **SystemLibrary**, utilizando **Spring Security** y **JWT**, separando la responsabilidad de autenticación en un microservicio independiente llamado `ms-auth`.

El objetivo principal es que los usuarios puedan registrarse, iniciar sesión y obtener un token JWT, el cual será utilizado para consumir los endpoints protegidos del sistema a través del BFF.

---

## 2. Arquitectura de autenticación

La autenticación se encuentra separada en el microservicio `ms-auth`.

El cliente no se comunica directamente con `ms-auth`, sino que lo hace a través del **BFF**.

```text id="6u91ss"
Cliente / Swagger / Postman
          ↓
         BFF
          ↓
       ms-auth
          ↓
      PostgreSQL
```

El BFF funciona como punto de entrada principal y redirige las solicitudes de registro y login hacia el microservicio de autenticación.

---

## 3. Responsabilidades por módulo

### BFF

Responsabilidades:

* Recibir solicitudes externas.
* Redirigir `/register` y `/login` hacia `ms-auth`.
* Validar token JWT en endpoints protegidos.
* Permitir acceso público a Swagger, login y registro.
* Proteger rutas privadas como `/books` y `/users`.

### ms-auth

Responsabilidades:

* Registrar usuarios.
* Validar credenciales.
* Cifrar contraseñas.
* Generar token JWT.
* Responder al BFF con el token generado.

### ms-book

Responsabilidades relacionadas con autenticación:

* No genera tokens.
* No registra usuarios.
* Sus endpoints se consumen protegidos desde el BFF.

### ms-user

Responsabilidades relacionadas con autenticación:

* No genera tokens.
* No realiza login.
* Administra perfiles de usuario relacionados al correo autenticado.
* Sus endpoints se consumen protegidos desde el BFF.

---

## 4. Flujo de autenticación

```mermaid id="0zqkhu"
sequenceDiagram
    participant Cliente
    participant BFF
    participant AuthService as ms-auth
    participant DB as PostgreSQL Auth
    participant BookService as ms-book
    participant UserService as ms-user

    Cliente->>BFF: POST /register
    BFF->>AuthService: Redirige registro
    AuthService->>DB: Guarda usuario con contraseña cifrada
    DB-->>AuthService: Usuario registrado
    AuthService-->>BFF: Token o confirmación
    BFF-->>Cliente: Respuesta de registro

    Cliente->>BFF: POST /login
    BFF->>AuthService: Redirige login
    AuthService->>DB: Valida email y password
    DB-->>AuthService: Usuario válido
    AuthService-->>BFF: Token JWT
    BFF-->>Cliente: Token JWT

    Cliente->>BFF: GET /books con Authorization Bearer Token
    BFF->>BFF: Valida token JWT
    BFF->>BookService: Redirige solicitud
    BookService-->>BFF: Lista de libros
    BFF-->>Cliente: Respuesta autorizada

    Cliente->>BFF: GET /users con Authorization Bearer Token
    BFF->>BFF: Valida token JWT
    BFF->>UserService: Redirige solicitud
    UserService-->>BFF: Lista de perfiles
    BFF-->>Cliente: Respuesta autorizada
```

---

## 5. Endpoints relacionados con autenticación

Los endpoints de autenticación se consumen desde el BFF.

| Método | Endpoint         | Descripción                        | Seguridad |
| ------ | ---------------- | ---------------------------------- | --------- |
| POST   | `/register`      | Registrar usuario                  | Público   |
| POST   | `/login`         | Iniciar sesión y obtener token JWT | Público   |
| GET    | `/books`         | Obtener libros                     | Protegido |
| POST   | `/books`         | Crear libro                        | Protegido |
| GET    | `/users`         | Obtener perfiles                   | Protegido |
| PUT    | `/users/profile` | Crear o actualizar perfil          | Protegido |
| DELETE | `/users/{id}`    | Eliminar perfil                    | Protegido |

---

## 6. Request de registro

Endpoint:

```http id="qjsmew"
POST /register
```

Body:

```json id="gw5vej"
{
  "email": "usuario@test.com",
  "password": "123456"
}
```

Resultado esperado:

```text id="hr5vct"
200 OK o 201 Created
```

Ejemplo de respuesta:

```json id="ml8u3d"
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

## 7. Request de login

Endpoint:

```http id="g97qxt"
POST /login
```

Body:

```json id="zo8eod"
{
  "email": "usuario@test.com",
  "password": "123456"
}
```

Resultado esperado:

```text id="dccq0p"
200 OK
```

Ejemplo de respuesta:

```json id="def6w6"
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

## 8. Uso del token JWT

El token generado en el login debe enviarse en las solicitudes protegidas usando el header:

```http id="z654iz"
Authorization: Bearer TOKEN
```

Ejemplo:

```http id="5zv2y8"
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

En Swagger, el token se agrega presionando el botón:

```text id="w421o0"
Authorize
```

Luego se pega el token JWT para que Swagger lo envíe automáticamente en las peticiones protegidas.

---

## 9. Reglas de seguridad

* Las rutas `/register` y `/login` deben ser públicas.
* Las rutas de Swagger deben ser públicas.
* Las rutas privadas deben requerir token JWT.
* Las contraseñas deben almacenarse cifradas.
* El login debe devolver un token JWT válido.
* El token debe enviarse usando `Authorization: Bearer TOKEN`.
* Si el token no existe, el sistema debe responder `401 Unauthorized`.
* Si el token es inválido, el sistema debe responder `401 Unauthorized`.
* Si el token es válido, el BFF debe permitir la solicitud y redirigirla al microservicio correspondiente.

---

## 10. Configuración de rutas públicas en SecurityConfig

En el BFF se permite acceso público a login, registro y Swagger.

```java id="mg1xar"
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

Esto significa que Swagger, login y registro se pueden usar sin token, mientras que el resto de endpoints requiere autenticación.

---

## 11. Configuración de Swagger con JWT

Para que Swagger permita autenticarse mediante token, se agregó configuración OpenAPI:

```java id="pfojwe"
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

Esta configuración muestra el botón **Authorize** en Swagger UI.

---

## 12. Flujo de prueba en Swagger

Para probar la autenticación desde Swagger:

```text id="z515rw"
1. Abrir Swagger:
   http://localhost:5000/swagger-ui/index.html

2. Ejecutar POST /register.

3. Ejecutar POST /login.

4. Copiar el token JWT.

5. Presionar Authorize.

6. Pegar el token.

7. Ejecutar un endpoint protegido, por ejemplo:
   GET /books

8. Verificar que responda 200 OK.
```

Si no se pega el token, el endpoint protegido debe responder:

```text id="klbqdg"
401 Unauthorized
```

---

## 13. Pruebas esperadas

### Registro correcto

Resultado esperado:

```text id="8jjs4u"
Usuario registrado correctamente.
Token generado o respuesta exitosa.
```

### Login correcto

Resultado esperado:

```text id="dx4g2t"
Credenciales válidas.
Token JWT generado.
```

### Acceso sin token

Endpoint de prueba:

```http id="mc2s5r"
GET /books
```

Resultado esperado:

```text id="fdah5o"
401 Unauthorized
```

### Acceso con token válido

Endpoint de prueba:

```http id="5z69mh"
GET /books
```

Header:

```http id="ycd1xv"
Authorization: Bearer TOKEN
```

Resultado esperado:

```text id="65fup9"
200 OK
```

### Acceso con token inválido

Resultado esperado:

```text id="rfhl84"
401 Unauthorized
```

---

## 14. Checklist de autenticación

* [ ] El endpoint `/register` permite registrar usuarios.
* [ ] El endpoint `/login` permite iniciar sesión.
* [ ] El login devuelve un token JWT.
* [ ] El token puede copiarse y pegarse en Swagger.
* [ ] Swagger muestra el botón Authorize.
* [ ] Los endpoints protegidos responden `401 Unauthorized` sin token.
* [ ] Los endpoints protegidos responden `200 OK` con token válido.
* [ ] Las rutas de Swagger funcionan sin autenticación.
* [ ] Las contraseñas se almacenan cifradas.
* [ ] El BFF redirige correctamente las solicitudes autenticadas.
* [ ] `ms-user` no realiza autenticación, solo administra perfiles.
* [ ] `ms-book` no realiza autenticación, solo administra libros.

---

## 15. Mejoras futuras

* Implementar roles de usuario.
* Agregar autorización por permisos.
* Incorporar refresh token.
* Agregar recuperación de contraseña.
* Configurar expiración del token mediante properties.
* Agregar revocación de tokens.
* Mejorar mensajes de error de autenticación.
* Proteger endpoints según rol.
* Registrar auditoría de login y operaciones sensibles.
* Agregar pruebas específicas para expiración de token.

---

## 16. Conclusión

El plan de autenticación de SystemLibrary separa correctamente la responsabilidad de seguridad en el microservicio `ms-auth`, mientras que el BFF actúa como punto de entrada y filtro para proteger los endpoints privados.

Este diseño permite mantener una arquitectura más ordenada, ya que la autenticación no se mezcla con la gestión de libros ni con la gestión de perfiles de usuario.

La autenticación mediante JWT permite que el cliente acceda a los endpoints protegidos enviando el token en el header `Authorization`, asegurando que solo usuarios autenticados puedan consumir las funcionalidades privadas del sistema.
