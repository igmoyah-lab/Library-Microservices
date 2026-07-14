# Tests Quick Start - Library Microservices

## 1. Objetivo

Esta guía rápida permite:

- Levantar **Library Microservices** con Docker Compose.
- Confirmar que los 18 contenedores estén activos.
- Probar autenticación y sesión JWT.
- Ejecutar los flujos principales de la biblioteca.
- Comprobar la comunicación entre microservicios.
- Validar logs y GlitchTip.
- Ejecutar los tests automatizados.
- Generar los reportes JaCoCo.

Para una explicación más detallada, revisar:

```text
README.md
GUIA_TESTING.md
PLAN_AUTH.md
```

---

## 2. Arquitectura actual

El proyecto contiene:

```text
1 BFF / API Gateway
9 microservicios
8 bases PostgreSQL
```

Servicios:

```text
bff
ms-auth
ms-book
ms-user
ms-loan
ms-return
ms-reservation
ms-fine
ms-notification
ms-report
```

Arquitectura resumida:

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

   +--------------+--------------+
   |              |              |
   v              v              v
ms-loan        ms-return      ms-reservation

   +--------------+--------------+
   |              |              |
   v              v              v
ms-fine       ms-notification  ms-report
```

URL principal:

```text
http://localhost:5000
```

Swagger:

```text
http://localhost:5000/swagger-ui/index.html
```

OpenAPI:

```text
http://localhost:5000/v3/api-docs
```

---

## 3. Puertos

| Componente | Puerto |
|---|---:|
| BFF | `5000` |
| ms-book | `5001` |
| ms-user | `5002` |
| ms-auth | `5003` |
| ms-loan | `5004` |
| ms-return | `5005` |
| ms-reservation | `5006` |
| ms-fine | `5007` |
| ms-notification | `5008` |
| ms-report | `5009` |

Bases PostgreSQL:

| Base | Puerto local |
|---|---:|
| Book | `3001` |
| User | `3002` |
| Auth | `3003` |
| Loans | `3004` |
| Returns | `3005` |
| Reservations | `3006` |
| Fines | `3007` |
| Notifications | `3008` |

---

# PARTE I: LEVANTAR EL PROYECTO

## 4. Comprobar Docker Desktop

Ejecutar:

```cmd
docker version
```

Debe mostrar:

```text
Client:
...

Server:
...
```

Comprobar contexto:

```cmd
docker context ls
```

En Windows debe aparecer:

```text
desktop-linux *
```

Si no está seleccionado:

```cmd
docker context use desktop-linux
```

Si Docker no inicia:

```cmd
wsl --shutdown
```

Luego abrir Docker Desktop y esperar:

```text
Engine running
```

---

## 5. Preparar `.env`

Comprobar que exista:

```cmd
dir .env
```

Si no existe:

```cmd
copy .env.example .env
```

Variables principales:

```env
SENTRY_ENABLED=true
SENTRY_DSN=COLOCAR_DSN_REAL_DE_GLITCHTIP
SENTRY_ENVIRONMENT=examen-docker
SENTRY_RELEASE=library-microservices-bff@1.0.0
SENTRY_DEBUG=false
```

Confirmar que Git ignore el archivo:

```cmd
git check-ignore .env
```

Resultado esperado:

```text
.env
```

Nunca subir el `.env` real.

---

## 6. Validar Docker Compose

Desde la raíz:

```text
C:\Users\Pc\Eva_3_Library
```

Ejecutar:

```cmd
docker compose config
```

Resultado esperado:

- No aparecen errores YAML.
- No aparece `environment must be a mapping`.
- Se muestran los servicios y variables procesadas.

---

## 7. Levantar todo

Primera construcción o después de cambios:

```cmd
docker compose up -d --build
```

Construcción completamente limpia:

```cmd
docker compose down
docker compose build --no-cache
docker compose up -d
```

---

## 8. Comprobar contenedores

```cmd
docker compose ps
```

Resultado esperado:

```text
8 bases PostgreSQL
9 microservicios
1 BFF
18 contenedores
```

Las bases deben aparecer:

```text
Up (healthy)
```

Los servicios:

```text
Up
```

---

## 9. Revisar logs

BFF:

```cmd
docker compose logs --tail=100 bff
```

En tiempo real:

```cmd
docker compose logs -f bff
```

Resultado esperado:

```text
Tomcat started on port 5000
Started BffApplication
```

Para salir de los logs:

```text
Ctrl + C
```

Esto no detiene los contenedores.

---

# PARTE II: AUTENTICACIÓN Y SESIÓN

## 10. Registrar usuario

CMD:

```cmd
curl -X POST http://localhost:5000/register ^
-H "Content-Type: application/json" ^
-d "{\"email\":\"usuario@test.com\",\"password\":\"Test1234\"}"
```

Resultado esperado:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Si el correo ya existe, usar login.

---

## 11. Iniciar sesión

```cmd
curl -X POST http://localhost:5000/login ^
-H "Content-Type: application/json" ^
-d "{\"email\":\"usuario@test.com\",\"password\":\"Test1234\"}"
```

Copiar el token sin comillas.

---

## 12. Probar acceso sin token

```cmd
curl -i http://localhost:5000/books
```

Resultado esperado:

```text
HTTP/1.1 401
```

---

## 13. Probar sesión

```cmd
curl -i http://localhost:5000/session ^
-H "Authorization: Bearer TOKEN"
```

Resultado esperado:

```text
HTTP/1.1 200
```

Esto confirma:

- JWT válido.
- Sesión stateless activa.
- Correo extraído desde el token.

---

## 14. Autorizar Swagger

1. Abrir Swagger.
2. Presionar **Authorize**.
3. Pegar el token.
4. Confirmar.
5. Ejecutar un endpoint protegido.

---

# PARTE III: SMOKE TEST FUNCIONAL

## 15. Crear perfil

Endpoint:

```http
PUT /users/profile
```

Body:

```json
{
  "authEmail": "usuario@test.com",
  "fullName": "Usuario Test",
  "phone": "912345678",
  "address": "Santiago"
}
```

Resultado esperado:

```text
200 OK
```

Guardar el UUID del usuario.

---

## 16. Crear libro

Endpoint:

```http
POST /books
```

Body:

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "category": "Programming",
  "isbn": "9780132350884",
  "available": true
}
```

Resultado esperado:

```text
201 Created
```

Guardar el UUID del libro.

---

## 17. Listar libros

```http
GET /books
```

Resultado esperado:

```text
200 OK
```

---

## 18. Buscar libro

```http
GET /books/{bookId}
```

También:

```http
GET /books/search?title=Clean
```

Resultado esperado:

```text
200 OK
```

---

## 19. Crear préstamo

```http
POST /loans
```

Body de referencia:

```json
{
  "userId": "UUID_USUARIO",
  "bookId": "UUID_LIBRO"
}
```

Confirmar campos exactos en Swagger.

Resultado esperado:

- Préstamo creado.
- Estado `ACTIVE`.
- Libro cambia a:

```json
{
  "available": false
}
```

Guardar el UUID del préstamo.

---

## 20. Probar préstamo duplicado

Intentar crear otro préstamo para el mismo libro.

Resultado esperado:

- Error controlado.
- No se crea un segundo préstamo activo.

---

## 21. Registrar devolución

```http
POST /returns
```

Body de referencia:

```json
{
  "loanId": "UUID_PRESTAMO"
}
```

Resultado esperado:

- Devolución registrada.
- Préstamo actualizado.
- Libro vuelve a:

```json
{
  "available": true
}
```

---

## 22. Consultar devolución

```http
GET /returns/loan/{loanId}
```

Resultado esperado:

```text
200 OK
```

---

## 23. Crear reserva

```http
POST /reservations
```

Body de referencia:

```json
{
  "userId": "UUID_USUARIO",
  "bookId": "UUID_LIBRO"
}
```

Resultado esperado:

- Reserva creada.
- Notificación creada cuando corresponda.

---

## 24. Consultar notificaciones

```http
GET /notifications/user/{userId}
```

Resultado esperado:

```text
200 OK
```

Marcar como leída:

```http
PATCH /notifications/{notificationId}/read
```

Resultado esperado:

```json
{
  "read": true
}
```

---

## 25. Consultar multas

Por usuario:

```http
GET /fines/user/{userId}
```

Por ID:

```http
GET /fines/{fineId}
```

Marcar pagada:

```http
PATCH /fines/{fineId}/pay
```

Conteo:

```http
GET /fines/count
```

---

## 26. Consultar reporte

```http
GET /reports/general-summary
```

Resultado esperado:

- Reporte generado usando datos de préstamos y multas.
- `ms-report` funciona sin base propia.

---

## 27. Consultar dashboard

```http
GET /dashboard/user/{userId}
```

Resultado esperado:

- Datos combinados.
- Préstamos.
- Reservas.
- Multas.
- Notificaciones.

Esta prueba demuestra que el BFF agrega respuestas de varios servicios.

---

# PARTE IV: GLITCHTIP Y LOGS

## 28. Probar endpoint sin token

```cmd
curl -i http://localhost:5000/test/glitchtip
```

Resultado esperado:

```text
HTTP/1.1 401
```

---

## 29. Probar GlitchTip con token

```cmd
curl -i http://localhost:5000/test/glitchtip ^
-H "Authorization: Bearer TOKEN"
```

Resultado esperado:

```text
HTTP/1.1 500
```

Body esperado:

```json
{
  "success": false,
  "data": null,
  "message": "Ocurrió un error interno en el servidor"
}
```

---

## 30. Revisar error en Docker

```cmd
docker compose logs --tail=150 bff
```

Buscar:

```text
IllegalStateException
Error de prueba controlado desde Eva-3-Library BFF
```

---

## 31. Revisar GlitchTip

En GlitchTip debe aparecer:

```text
IllegalStateException
Error de prueba controlado desde Eva-3-Library BFF
```

Datos esperados:

```text
Environment: examen-docker
Release: eva-3-library-bff@1.0.0
```

La integración queda comprobada cuando el mismo error aparece:

1. Como respuesta `500`.
2. En Docker logs.
3. En GlitchTip.

---

# PARTE V: TESTS AUTOMATIZADOS

## 32. Ejecutar tests de un módulo

Ejemplo BFF:

```cmd
cd bff
gradlew.bat clean test
cd ..
```

Ejemplo préstamos:

```cmd
cd ms-loan
gradlew.bat clean test
cd ..
```

Resultado esperado:

```text
BUILD SUCCESSFUL
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
    Write-Host "=================================="
    Write-Host "TESTS: $module"
    Write-Host "=================================="

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

## 34. Abrir reporte de tests

Cada módulo genera:

```text
build/reports/tests/test/index.html
```

Ejemplo:

```cmd
start bff\build\reports\tests\test\index.html
```

---

# PARTE VI: JACOCO

## 35. Generar cobertura de un módulo

```cmd
cd bff
gradlew.bat clean test jacocoTestReport
```

Abrir:

```cmd
start build\reports\jacoco\test\html\index.html
```

Volver:

```cmd
cd ..
```

---

## 36. Ejecutar JaCoCo en todos los módulos

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
    Write-Host "=================================="
    Write-Host "JACOCO: $module"
    Write-Host "=================================="

    Push-Location $module
    .\gradlew.bat clean test jacocoTestReport

    if ($LASTEXITCODE -ne 0) {
        Pop-Location
        throw "Falló JaCoCo en $module"
    }

    Pop-Location
}

Write-Host "TESTS Y REPORTES GENERADOS"
```

---

## 37. Cobertura requerida

Requisito:

```text
40 % mínimo
```

Registrar resultados:

| Módulo | Tests correctos | Cobertura |
|---|---:|---:|
| bff |  |  |
| ms-auth |  |  |
| ms-book |  |  |
| ms-user |  |  |
| ms-loan |  |  |
| ms-return |  |  |
| ms-reservation |  |  |
| ms-fine |  |  |
| ms-notification |  |  |
| ms-report |  |  |

---

## 38. Error de JaCoCo por clases diferentes

Mensaje:

```text
Classes in bundle do not match with execution data
Execution data for class ... does not match
```

Solución:

```cmd
gradlew.bat clean
gradlew.bat test jacocoTestReport
```

Si continúa:

1. Eliminar la carpeta `build`.
2. Comprobar que no exista una clase duplicada en `src/test`.
3. Ejecutar nuevamente.

---

# PARTE VII: RECONSTRUIR SOLO EL BFF

## 39. Después de modificar código del BFF

Desde la raíz:

```cmd
docker compose stop bff
docker compose rm -f bff
docker compose build --no-cache bff
docker compose up -d --no-deps bff
```

Comprobar:

```cmd
docker compose ps bff
docker compose logs --tail=100 bff
```

Importante:

```text
docker compose build --no-deps bff
```

no es válido.

El parámetro `--no-deps` se utiliza con:

```cmd
docker compose up -d --no-deps bff
```

---

# PARTE VIII: ERRORES RÁPIDOS

## 40. Docker Engine no está iniciado

Mensaje:

```text
failed to connect to the docker API
dockerDesktopLinuxEngine
```

Solución:

```cmd
wsl --shutdown
```

Abrir Docker Desktop.

---

## 41. YAML inválido

Mensaje:

```text
services.bff.environment must be a mapping
```

Formato correcto:

```yaml
environment:
  AUTH_SERVICE_URL: "http://ms-auth:5003"
  SENTRY_ENABLED: "${SENTRY_ENABLED:-false}"
```

---

## 42. 401 Unauthorized

Causa:

- No se envió token.
- Token inválido.
- Token expirado.

Solución:

1. Ejecutar `/login`.
2. Copiar el token.
3. Agregar `Authorization: Bearer TOKEN`.

---

## 43. 404 con token válido

El JWT fue aceptado, pero el endpoint no existe en la imagen actual.

Solución:

```cmd
docker compose stop bff
docker compose rm -f bff
docker compose build --no-cache bff
docker compose up -d --no-deps bff
```

---

## 44. Error de conexión a PostgreSQL

Revisar:

```cmd
docker compose ps
docker compose logs NOMBRE_BASE
docker compose logs NOMBRE_SERVICIO
```

Confirmar que la base esté:

```text
healthy
```

---

## 45. Puerto ocupado

Windows:

```cmd
netstat -ano | findstr :5000
```

Identificar proceso:

```cmd
tasklist | findstr PID
```

---

## 46. Servicio reiniciándose

```cmd
docker compose ps
docker compose logs --tail=200 NOMBRE_SERVICIO
```

Revisar:

- Base saludable.
- Variables.
- URL JDBC.
- Migraciones.
- Puertos.
- Dependencias.

---

# PARTE IX: CHECKLIST RÁPIDO

## 47. Infraestructura

- [ ] Docker Desktop iniciado.
- [ ] `docker version` muestra Client y Server.
- [ ] Contexto `desktop-linux`.
- [ ] `.env` configurado.
- [ ] `.env` ignorado.
- [ ] `docker compose config` válido.
- [ ] 18 contenedores activos.
- [ ] 8 bases `healthy`.
- [ ] Swagger abre.

---

## 48. Seguridad

- [ ] Registro funciona.
- [ ] Login funciona.
- [ ] JWT generado.
- [ ] `/session` funciona.
- [ ] Sin token devuelve `401`.
- [ ] Token válido permite acceso.
- [ ] Token inválido devuelve `401`.

---

## 49. Funcionalidad

- [ ] Perfil creado.
- [ ] Libro creado.
- [ ] CRUD de libros.
- [ ] Préstamo creado.
- [ ] Libro no disponible.
- [ ] Préstamo duplicado rechazado.
- [ ] Devolución creada.
- [ ] Libro disponible nuevamente.
- [ ] Reserva creada.
- [ ] Notificación creada.
- [ ] Multa consultada.
- [ ] Reporte generado.
- [ ] Dashboard generado.

---

## 50. Observabilidad

- [ ] Logs visibles en Docker.
- [ ] Endpoint GlitchTip sin token devuelve `401`.
- [ ] Endpoint GlitchTip con token devuelve `500`.
- [ ] Stack trace visible.
- [ ] Issue visible en GlitchTip.

---

## 51. Testing

- [ ] Tests del BFF pasan.
- [ ] Tests de los 9 MS pasan.
- [ ] Reportes HTML existen.
- [ ] JaCoCo generado.
- [ ] Cobertura mínima de 40 %.
- [ ] Evidencias guardadas.

---

## 52. Git

- [ ] Cambios agregados.
- [ ] Commit creado.
- [ ] Push realizado.
- [ ] `.env` no aparece en Git.
- [ ] Rama correcta.
- [ ] Working tree limpio.

Comprobar:

```cmd
git status
```

Resultado esperado:

```text
nothing to commit, working tree clean
```

---

## 53. Comandos mínimos para la defensa

```cmd
docker compose up -d
docker compose ps
docker compose logs --tail=50 bff
```

Abrir:

```text
http://localhost:5000/swagger-ui/index.html
```

Probar:

```text
/register
/login
/session
/books
/loans
/returns
/reservations
/fines
/notifications
/reports/general-summary
/dashboard/user/{userId}
/test/glitchtip
```

---

## 54. Resultado final esperado

Al completar esta guía rápida se debe confirmar que:

- El BFF funciona como API Gateway.
- Los nueve microservicios están activos.
- Las ocho bases respetan Database per Service.
- JWT protege los endpoints.
- La sesión funciona.
- Los flujos de biblioteca operan correctamente.
- Los microservicios se comunican.
- El BFF agrega información.
- Los errores se manejan de forma centralizada.
- Los logs aparecen en Docker.
- Los errores inesperados llegan a GlitchTip.
- Todos los tests pasan.
- JaCoCo genera cobertura.
- El sistema está listo para la defensa.