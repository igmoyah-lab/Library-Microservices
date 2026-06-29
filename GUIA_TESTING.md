# Guía de Testing - SystemLibrary

## 1. Objetivo

Validar que los microservicios de **SystemLibrary** funcionen correctamente antes de la entrega, comprobando el flujo completo desde el BFF hacia los microservicios internos.

La guía considera pruebas manuales mediante Swagger, Postman o REST Client, además de pruebas automatizadas con JUnit, Mockito, H2 y JaCoCo.

---

## 2. Arquitectura considerada para las pruebas

El sistema está compuesto por los siguientes módulos:

```text id="w2upvc"
Cliente / Swagger / Postman
          ↓
         BFF
          ↓
 ┌────────┼────────┐
 ↓        ↓        ↓
ms-auth  ms-book  ms-user
```

El BFF funciona como punto de entrada principal.
Por lo tanto, la mayoría de las pruebas funcionales se realizan desde el puerto del BFF.

Puerto del BFF:

```text id="6sn3a9"
http://localhost:5000
```

Swagger del BFF:

```text id="x5i9c7"
http://localhost:5000/swagger-ui/index.html
```

OpenAPI JSON:

```text id="add8da"
http://localhost:5000/v3/api-docs
```

---

## 3. Herramientas recomendadas

* Visual Studio Code.
* Extensión REST Client.
* Postman.
* Swagger UI.
* Docker Desktop.
* Gradle.
* PostgreSQL.
* H2 Database para pruebas.
* JaCoCo para cobertura.

---

## 4. Servicios que deben estar levantados

Antes de probar el sistema completo, se deben levantar los servicios necesarios.

Orden recomendado:

```text id="eeqoh4"
1. PostgreSQL o Docker.
2. ms-auth.
3. ms-book.
4. ms-user.
5. BFF.
```

El BFF debe levantarse al final, ya que redirige solicitudes hacia los demás microservicios.

---

## 5. Pruebas para Auth Service

Estas pruebas se realizan desde el BFF, usando Swagger o Postman.

---

### 5.1 Registrar usuario

Endpoint:

```http id="sy1vqy"
POST /register
```

Request body:

```json id="8vv8vf"
{
  "email": "usuario@test.com",
  "password": "123456"
}
```

Resultado esperado:

* Código HTTP `200 OK` o `201 Created`, según la configuración del controller.
* Se registra el usuario correctamente.
* Se recibe una respuesta con token o mensaje de registro correcto.

Ejemplo de respuesta:

```json id="1sbk5i"
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 5.2 Login

Endpoint:

```http id="4xsmkv"
POST /login
```

Request body:

```json id="zqr8rp"
{
  "email": "usuario@test.com",
  "password": "123456"
}
```

Resultado esperado:

* Código HTTP `200 OK`.
* Se recibe un token JWT.

Ejemplo de respuesta:

```json id="epmxn7"
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 5.3 Validación del token

Luego de obtener el token, se debe usar en los endpoints protegidos mediante el header:

```http id="qse2p8"
Authorization: Bearer TOKEN
```

Resultado esperado:

* Si el token es válido, los endpoints protegidos responden correctamente.
* Si el token no existe o es inválido, el sistema responde `401 Unauthorized`.

---

## 6. Pruebas para Books Service

Estas pruebas se realizan desde el BFF, pero internamente se redirigen hacia `ms-book`.

---

### 6.1 Crear libro

Endpoint:

```http id="nqynwm"
POST /books
```

Request body:

```json id="1if6re"
{
  "title": "El Principito",
  "author": "Antoine de Saint-Exupéry",
  "category": "Literatura",
  "isbn": "123456789"
}
```

Resultado esperado:

* Código HTTP `201 Created`.
* El libro queda registrado.
* La respuesta contiene los datos del libro creado.

Ejemplo de respuesta:

```json id="cs0ain"
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

### 6.2 Listar libros

Endpoint:

```http id="uk1k78"
GET /books
```

Resultado esperado:

* Código HTTP `200 OK`.
* Se obtiene una lista de libros.
* La respuesta contiene `success`, `data` y `message`.

Ejemplo de respuesta:

```json id="ro7ems"
{
  "success": true,
  "data": [
    {
      "id": "00000000-0000-0000-0000-000000000000",
      "title": "El Principito",
      "author": "Antoine de Saint-Exupéry",
      "category": "Literatura",
      "isbn": "123456789"
    }
  ],
  "message": "Libros obtenidos con éxito"
}
```

---

### 6.3 Buscar libro por ID

Endpoint:

```http id="pyc1ss"
GET /books/{id}
```

Ejemplo:

```http id="rzkne5"
GET /books/00000000-0000-0000-0000-000000000000
```

Resultado esperado:

* Código HTTP `200 OK` si el libro existe.
* Código HTTP `404 Not Found` si el libro no existe.
* Se obtiene la información del libro solicitado.

---

### 6.4 Actualizar libro

Endpoint:

```http id="gqex7h"
PUT /books/{id}
```

Request body:

```json id="atvrlz"
{
  "title": "El Principito Actualizado",
  "author": "Antoine de Saint-Exupéry",
  "category": "Literatura Infantil",
  "isbn": "987654321"
}
```

Resultado esperado:

* Código HTTP `200 OK`.
* Los datos del libro quedan actualizados.
* La respuesta contiene los datos modificados.

---

### 6.5 Eliminar libro

Endpoint:

```http id="jd6zbq"
DELETE /books/{id}
```

Resultado esperado:

* Código HTTP `200 OK` o `204 No Content`, según la configuración.
* El libro eliminado ya no debe aparecer en el listado.
* Si el libro no existe, se debe responder con error controlado.

---

### 6.6 Buscar libros por filtros

Endpoint:

```http id="p58v2h"
GET /books/search
```

Parámetros posibles:

```text id="vf8fsm"
title
author
category
isbn
```

Ejemplo:

```http id="hp45bb"
GET /books/search?title=principito
```

Resultado esperado:

* Código HTTP `200 OK`.
* Se obtiene una lista de libros que coinciden con los filtros.
* Si no existen coincidencias, se devuelve lista vacía o mensaje indicando que no se encontraron resultados.

---

## 7. Pruebas para Users Service

Estas pruebas se realizan desde el BFF, pero internamente se redirigen hacia `ms-user`.

El microservicio `ms-user` gestiona perfiles de usuario.
No se encarga del login ni de JWT, ya que esa responsabilidad pertenece a `ms-auth`.

---

### 7.1 Crear o actualizar perfil

Endpoint:

```http id="60gb4w"
PUT /users/profile
```

Request body:

```json id="qk02l3"
{
  "authEmail": "usuario@test.com",
  "fullName": "Usuario Test",
  "phone": "912345678",
  "address": "Santiago"
}
```

Resultado esperado:

* Código HTTP `200 OK`.
* Si el perfil no existe, se crea.
* Si el perfil ya existe, se actualiza.
* La respuesta indica si el perfil fue completado o actualizado.

Ejemplo de respuesta:

```json id="tj268k"
{
  "success": true,
  "message": "Perfil actualizado correctamente",
  "data": {
    "id": "00000000-0000-0000-0000-000000000000",
    "authEmail": "usuario@test.com",
    "fullName": "Usuario Test",
    "phone": "912345678",
    "address": "Santiago"
  }
}
```

---

### 7.2 Listar perfiles

Endpoint:

```http id="200nnh"
GET /users
```

Resultado esperado:

* Código HTTP `200 OK`.
* Se obtiene una lista de perfiles registrados.

Ejemplo de respuesta:

```json id="hj9zvz"
{
  "success": true,
  "message": "Perfiles obtenidos correctamente",
  "data": [
    {
      "id": "00000000-0000-0000-0000-000000000000",
      "authEmail": "usuario@test.com",
      "fullName": "Usuario Test",
      "phone": "912345678",
      "address": "Santiago"
    }
  ]
}
```

---

### 7.3 Eliminar perfil

Endpoint:

```http id="r8biut"
DELETE /users/{id}
```

Resultado esperado:

* Código HTTP `200 OK`.
* El perfil queda eliminado.
* Si el perfil no existe, se debe responder con error controlado.

---

## 8. Pruebas de seguridad

---

### 8.1 Acceso sin token

Probar un endpoint protegido sin enviar token.

Ejemplo:

```http id="icvtqc"
GET /books
```

Resultado esperado:

```text id="gmv84y"
401 Unauthorized
```

Esto valida que el endpoint está protegido.

---

### 8.2 Acceso con token válido

Probar un endpoint protegido enviando token.

Header:

```http id="s19ura"
Authorization: Bearer TOKEN
```

Ejemplo:

```http id="vlik9c"
GET /books
```

Resultado esperado:

```text id="nkn9n2"
200 OK
```

Esto valida que el token JWT está siendo aceptado correctamente.

---

### 8.3 Token inválido

Enviar un token incorrecto o modificado.

Resultado esperado:

```text id="tsii40"
401 Unauthorized
```

Esto valida que el sistema rechaza tokens inválidos.

---

## 9. Pruebas con Swagger

Swagger se puede usar para probar los endpoints desde el navegador.

URL:

```text id="rgzg3z"
http://localhost:5000/swagger-ui/index.html
```

Flujo recomendado:

```text id="c7gkn0"
1. Abrir Swagger.
2. Ejecutar POST /register.
3. Ejecutar POST /login.
4. Copiar el token generado.
5. Presionar Authorize.
6. Pegar el token JWT.
7. Probar GET /books o GET /users.
```

Si Swagger muestra `401 Unauthorized`, significa que el token no fue enviado o es inválido.

---

## 10. Pruebas con Postman

Postman se puede usar para probar manualmente los endpoints.

Recomendación:

```text id="k8mo60"
1. Importar colección o crear requests manualmente.
2. Ejecutar /register.
3. Ejecutar /login.
4. Copiar token.
5. Configurar Authorization como Bearer Token.
6. Probar endpoints protegidos.
```

También se puede importar la documentación OpenAPI desde:

```text id="y44qvt"
http://localhost:5000/v3/api-docs
```

O importar una colección Postman generada previamente.

---

## 11. Pruebas automatizadas

Además de las pruebas manuales, el proyecto incorpora pruebas automatizadas.

---

## 11.1 Tests en ms-book

Clases de test:

```text id="fj033x"
BookDtoValidationTest
BookControllerTest
BookServiceImplTest
BookRepositoryTest
```

Validaciones principales:

* El DTO no acepta campos vacíos.
* El controller responde correctamente.
* El service ejecuta la lógica de negocio.
* El repository consulta datos usando H2.

---

## 11.2 Tests en ms-user

Clases de test:

```text id="vi0ldh"
UserDtoValidationTest
UserControllerTest
UserServiceImplTest
UserRepositoryTest
```

Validaciones principales:

* El DTO valida datos de perfil.
* El controller responde correctamente.
* El service crea, actualiza y elimina perfiles.
* El repository busca por `authEmail` ignorando mayúsculas.

---

## 12. Ejecución de tests

Para ejecutar los tests:

```bash id="2uzq5o"
./gradlew test
```

En Windows:

```bash id="93ctt8"
.\gradlew test
```

Resultado esperado:

```text id="28z839"
BUILD SUCCESSFUL
```

Reporte HTML generado:

```text id="q0ilay"
build/reports/tests/test/index.html
```

---

## 13. Reporte de cobertura JaCoCo

Para generar reporte de cobertura:

```bash id="yiwlw3"
./gradlew test jacocoTestReport
```

En Windows:

```bash id="kim632"
.\gradlew test jacocoTestReport
```

Reporte HTML:

```text id="bmy6ey"
build/reports/jacoco/test/html/index.html
```

Resultado esperado:

* Se genera reporte de cobertura.
* Se muestran paquetes, clases, métodos y líneas cubiertas por pruebas.

---

## 14. Checklist de pruebas manuales

### Servicios

* [ ] PostgreSQL o Docker inicia correctamente.
* [ ] `ms-auth` inicia correctamente.
* [ ] `ms-book` inicia correctamente.
* [ ] `ms-user` inicia correctamente.
* [ ] `bff` inicia correctamente en el puerto 5000.

### Autenticación

* [ ] Se puede registrar un usuario.
* [ ] Se puede iniciar sesión.
* [ ] Se obtiene token JWT.
* [ ] Swagger permite pegar el token en Authorize.
* [ ] Postman permite usar Bearer Token.

### Libros

* [ ] Se puede crear un libro.
* [ ] Se pueden listar libros.
* [ ] Se puede buscar libro por ID.
* [ ] Se puede buscar libro por filtros.
* [ ] Se puede actualizar un libro.
* [ ] Se puede eliminar un libro.

### Perfiles de usuario

* [ ] Se puede crear o actualizar perfil.
* [ ] Se pueden listar perfiles.
* [ ] Se puede eliminar perfil.

### Seguridad

* [ ] Un endpoint protegido sin token responde `401 Unauthorized`.
* [ ] Un endpoint protegido con token válido responde `200 OK`.
* [ ] Un token inválido responde `401 Unauthorized`.

### Testing automatizado

* [ ] Los tests de `ms-book` pasan correctamente.
* [ ] Los tests de `ms-user` pasan correctamente.
* [ ] El reporte de tests de Gradle se genera correctamente.
* [ ] El reporte de cobertura JaCoCo se genera correctamente.

---

## 15. Evidencias sugeridas

Para el informe o presentación se recomienda agregar capturas de:

* Swagger UI mostrando los controllers.
* Flujo de registro o login.
* Token JWT generado.
* Botón Authorize de Swagger.
* Endpoint protegido respondiendo `401 Unauthorized` sin token.
* Endpoint protegido respondiendo `200 OK` con token.
* Reporte de tests de Gradle.
* Reporte de cobertura JaCoCo.
* JSON de OpenAPI en `/v3/api-docs`.

---

## 16. Resultado esperado general

Al finalizar las pruebas, el sistema debe demostrar que:

* El BFF centraliza correctamente las solicitudes.
* `ms-auth` registra y autentica usuarios.
* `ms-book` gestiona correctamente el catálogo de libros.
* `ms-user` gestiona correctamente los perfiles de usuario.
* Los endpoints protegidos requieren token JWT.
* Swagger documenta y permite probar la API.
* Los tests automatizados validan las capas principales.
* JaCoCo genera reportes de cobertura.

---

## 17. Conclusión

La guía de testing permite validar el funcionamiento completo de SystemLibrary, considerando pruebas manuales, pruebas de seguridad y pruebas automatizadas.

El uso de Swagger y Postman facilita la validación funcional de los endpoints, mientras que JUnit, Mockito, H2 y JaCoCo permiten verificar el comportamiento interno de los microservicios.

Con esta guía, se puede comprobar que el sistema cumple con los flujos principales de autenticación, gestión de libros, gestión de perfiles y protección mediante JWT.
