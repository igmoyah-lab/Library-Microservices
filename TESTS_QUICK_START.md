# Tests Quick Start - SystemLibrary

## 1. Objetivo

Esta guía rápida permite levantar el proyecto **SystemLibrary**, probar los endpoints principales y ejecutar los tests automatizados antes de la entrega.

La arquitectura actual del sistema está compuesta por:

```text id="e3gvmv"
BFF
ms-auth
ms-book
ms-user
PostgreSQL
```

El BFF es el punto de entrada principal y se ejecuta en:

```text id="ya39zl"
http://localhost:5000
```

---

## 2. Orden recomendado para levantar el proyecto

El orden recomendado es:

```text id="z72hrb"
1. Levantar PostgreSQL o Docker.
2. Levantar ms-auth.
3. Levantar ms-book.
4. Levantar ms-user.
5. Levantar BFF.
```

El BFF debe levantarse al final, ya que redirige solicitudes hacia los demás microservicios.

---

## 3. Levantar bases de datos con Docker

Si cada microservicio tiene su propio `docker-compose.yml`, entrar a cada carpeta y levantar su base de datos.

Ejemplo:

```bash id="h6ys26"
cd ms-book
docker compose up -d
```

Luego:

```bash id="tg137n"
cd ../ms-user
docker compose up -d
```

Luego:

```bash id="3bbov9"
cd ../ms-auth
docker compose up -d
```

Verificar contenedores activos:

```bash id="9kr9fz"
docker ps
```

Resultado esperado:

```text id="f1w2j1"
Los contenedores de PostgreSQL deben aparecer como activos.
```

---

## 4. Levantar microservicios

Desde cada carpeta del microservicio, ejecutar:

```bash id="n7j9w6"
./gradlew bootRun
```

En Windows:

```bash id="6kf5sn"
.\gradlew bootRun
```

Orden recomendado:

```text id="qfr9ov"
1. ms-auth
2. ms-book
3. ms-user
4. bff
```

---

## 5. Verificar Swagger del BFF

Con el BFF levantado, abrir:

```text id="f62uli"
http://localhost:5000/swagger-ui/index.html
```

Resultado esperado:

```text id="9s5t1x"
Swagger UI debe cargar correctamente.
Deben aparecer los controllers:
- auth-controller
- book-controller
- user-controller
```

También se puede verificar la especificación OpenAPI en:

```text id="0tddt7"
http://localhost:5000/v3/api-docs
```

---

## 6. Flujo rápido de prueba desde Swagger

### 6.1 Registrar usuario

Endpoint:

```http id="ya8uo7"
POST /register
```

Body:

```json id="4i5kis"
{
  "email": "usuario@test.com",
  "password": "123456"
}
```

Resultado esperado:

```text id="cd59ky"
200 OK o 201 Created
```

---

### 6.2 Login

Endpoint:

```http id="cd644x"
POST /login
```

Body:

```json id="blvarh"
{
  "email": "usuario@test.com",
  "password": "123456"
}
```

Resultado esperado:

```text id="f5uhqe"
200 OK
```

La respuesta debe contener un token JWT:

```json id="6nx6vw"
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 6.3 Autorizar Swagger

En Swagger:

```text id="wts0tw"
1. Presionar Authorize.
2. Pegar el token JWT.
3. Confirmar.
4. Cerrar ventana.
```

El token se usa como:

```http id="qjn9hb"
Authorization: Bearer TOKEN
```

---

### 6.4 Probar endpoint protegido sin token

Endpoint:

```http id="8cjt2p"
GET /books
```

Resultado esperado sin token:

```text id="d72fo5"
401 Unauthorized
```

Esto confirma que la seguridad está activa.

---

### 6.5 Probar endpoint protegido con token

Endpoint:

```http id="4z57wm"
GET /books
```

Resultado esperado con token:

```text id="fh6jdv"
200 OK
```

---

## 7. Pruebas rápidas para ms-book desde BFF

Todas estas pruebas se ejecutan desde Swagger del BFF o Postman usando el puerto 5000.

### 7.1 Crear libro

```http id="s285sl"
POST /books
```

Body:

```json id="mpeqlk"
{
  "title": "El Principito",
  "author": "Antoine de Saint-Exupéry",
  "category": "Literatura",
  "isbn": "123456789"
}
```

Resultado esperado:

```text id="3cxpxq"
201 Created
```

---

### 7.2 Listar libros

```http id="z6xcl7"
GET /books
```

Resultado esperado:

```text id="t1rxwa"
200 OK
```

---

### 7.3 Buscar libro por ID

```http id="nx9zkf"
GET /books/{id}
```

Resultado esperado:

```text id="z6nk8w"
200 OK si existe.
404 Not Found si no existe.
```

---

### 7.4 Actualizar libro

```http id="u4q1xf"
PUT /books/{id}
```

Body:

```json id="s6fusv"
{
  "title": "El Principito Actualizado",
  "author": "Antoine de Saint-Exupéry",
  "category": "Literatura Infantil",
  "isbn": "987654321"
}
```

Resultado esperado:

```text id="84ys4t"
200 OK
```

---

### 7.5 Eliminar libro

```http id="9im4i8"
DELETE /books/{id}
```

Resultado esperado:

```text id="0u9xzr"
200 OK o 204 No Content.
```

---

### 7.6 Buscar libros por filtros

```http id="rhgg4b"
GET /books/search?title=principito
```

Resultado esperado:

```text id="1keti4"
200 OK
```

---

## 8. Pruebas rápidas para ms-user desde BFF

### 8.1 Crear o actualizar perfil

```http id="2phf8p"
PUT /users/profile
```

Body:

```json id="45i7gu"
{
  "authEmail": "usuario@test.com",
  "fullName": "Usuario Test",
  "phone": "912345678",
  "address": "Santiago"
}
```

Resultado esperado:

```text id="pdlr95"
200 OK
```

---

### 8.2 Listar perfiles

```http id="mgeewh"
GET /users
```

Resultado esperado:

```text id="6w98en"
200 OK
```

---

### 8.3 Eliminar perfil

```http id="2x1uvt"
DELETE /users/{id}
```

Resultado esperado:

```text id="nv72v6"
200 OK
```

---

## 9. Probar con Postman

Si se usa Postman, el flujo recomendado es:

```text id="t8dxo4"
1. Crear request POST /register.
2. Crear request POST /login.
3. Copiar token JWT.
4. Configurar Authorization como Bearer Token.
5. Probar endpoints protegidos.
```

URL base:

```text id="xnp8a1"
http://localhost:5000
```

Ejemplo:

```http id="nct6v6"
GET http://localhost:5000/books
```

Header requerido:

```http id="y90g8u"
Authorization: Bearer TOKEN
```

---

## 10. Ejecutar tests automatizados

Desde cada microservicio que tenga tests, ejecutar:

```bash id="89t05k"
./gradlew test
```

En Windows:

```bash id="jlr9um"
.\gradlew test
```

Ejemplo para `ms-book`:

```bash id="yma0lh"
cd ms-book
.\gradlew test
```

Ejemplo para `ms-user`:

```bash id="6dizym"
cd ms-user
.\gradlew test
```

Resultado esperado:

```text id="0vdolj"
BUILD SUCCESSFUL
```

---

## 11. Revisar reporte de tests

Después de ejecutar los tests, Gradle genera un reporte HTML.

Ruta:

```text id="49a9rj"
build/reports/tests/test/index.html
```

Ejemplo:

```text id="1ernf0"
ms-book/build/reports/tests/test/index.html
```

El reporte muestra:

```text id="im5wzo"
- Cantidad de tests ejecutados.
- Cantidad de tests fallidos.
- Tests omitidos.
- Porcentaje de éxito.
- Duración.
```

---

## 12. Ejecutar cobertura con JaCoCo

Para generar el reporte de cobertura:

```bash id="37lvht"
./gradlew test jacocoTestReport
```

En Windows:

```bash id="wgmtd9"
.\gradlew test jacocoTestReport
```

Reporte HTML:

```text id="erz0tx"
build/reports/jacoco/test/html/index.html
```

Reporte XML:

```text id="tbtma5"
build/reports/jacoco/test/jacocoTestReport.xml
```

---

## 13. Revisar errores comunes

### 13.1 Docker no inicia

Verificar que Docker Desktop esté abierto.

```bash id="jyw7bd"
docker ps
```

---

### 13.2 Puerto ocupado

Si aparece error de puerto ocupado, revisar el puerto usado por el microservicio o base de datos.

Solución posible:

```text id="x0za84"
Cambiar el puerto en application.properties o docker-compose.yml.
```

---

### 13.3 Error de conexión a PostgreSQL

Revisar:

```text id="1m56be"
- Usuario.
- Contraseña.
- Nombre de la base de datos.
- Puerto.
- URL JDBC.
- Si el contenedor está activo.
```

---

### 13.4 Error 401 Unauthorized

Causa probable:

```text id="9xwo0l"
No se envió token JWT o el token es inválido.
```

Solución:

```text id="c02k5j"
1. Ejecutar /login.
2. Copiar token.
3. Pegar token en Authorize de Swagger.
4. Repetir la solicitud.
```

---

### 13.5 Swagger no abre

Revisar que el BFF esté levantado en el puerto 5000.

URL:

```text id="jb5uk5"
http://localhost:5000/swagger-ui/index.html
```

También verificar que en `SecurityConfig` estén permitidas las rutas:

```text id="mpdk3i"
"/swagger-ui/**"
"/swagger-ui.html"
"/v3/api-docs/**"
```

---

### 13.6 Tests intentan conectarse a PostgreSQL

Causa probable:

```text id="s1l3il"
El test está usando application.properties en vez de application-test.properties.
```

Solución:

```text id="22z3tw"
Agregar @ActiveProfiles("test") o usar @DataJpaTest según corresponda.
```

También revisar que exista:

```text id="5uwxyn"
src/test/resources/application-test.properties
```

---

## 14. Checklist rápido

### Servicios

* [ ] PostgreSQL o Docker está activo.
* [ ] `ms-auth` inicia correctamente.
* [ ] `ms-book` inicia correctamente.
* [ ] `ms-user` inicia correctamente.
* [ ] `bff` inicia correctamente en el puerto 5000.

### Swagger

* [ ] Swagger abre correctamente.
* [ ] Se visualizan `auth-controller`, `book-controller` y `user-controller`.
* [ ] El botón Authorize aparece.
* [ ] `/v3/api-docs` devuelve el JSON OpenAPI.

### Autenticación

* [ ] Se registra un usuario.
* [ ] Se inicia sesión correctamente.
* [ ] Se obtiene token JWT.
* [ ] El token permite acceder a endpoints protegidos.
* [ ] Sin token se obtiene `401 Unauthorized`.

### Libros

* [ ] Se crea un libro.
* [ ] Se listan libros.
* [ ] Se busca libro por ID.
* [ ] Se busca libro por filtros.
* [ ] Se actualiza un libro.
* [ ] Se elimina un libro.

### Perfiles

* [ ] Se crea o actualiza un perfil.
* [ ] Se listan perfiles.
* [ ] Se elimina un perfil.

### Tests

* [ ] Tests de `ms-book` pasan correctamente.
* [ ] Tests de `ms-user` pasan correctamente.
* [ ] Reporte Gradle generado.
* [ ] Reporte JaCoCo generado.

---

## 15. Resultado esperado final

Al finalizar las pruebas, se debe confirmar que:

```text id="s6gmp8"
- El BFF redirige correctamente las solicitudes.
- ms-auth registra y autentica usuarios.
- ms-book gestiona libros correctamente.
- ms-user gestiona perfiles correctamente.
- Los endpoints protegidos requieren JWT.
- Swagger permite documentar y probar la API.
- Los tests automatizados pasan correctamente.
- JaCoCo genera el reporte de cobertura.
```
