# Agents.md — RedNorte Stack

## API / escaneo

- **No** indexar recursivamente (`**/*`) ni escanear directorios raíz sin confirmación.
- Exclusiones automáticas: `**/target/**`, `**/node_modules/**`, `**/.angular/**`, `**/dist/**`, `**/*.jar`, `**/*.log`.

## Stack

| Capa | Tecnología | Puerto |
|------|-----------|--------|
| Frontend | Angular 21 standalone + SCSS + Vitest | 4200 |
| UsuarioService (auth+patients) | Spring Boot 3.5 / JPA / JWT (JJWT 0.12.6) | 8081 → 8080 (Docker) |
| PabellonService (surgeries/OR) | Spring Boot 3.5 / JPA / JWT | 8082 |
| DB (Docker) | PostgreSQL 15 — `db_pacientes` / `db_pabellon` | 5432/5433/5434 |
| DB (local UsuarioService) | MySQL (Laragon) | 3306 |

Java 17, Lombok, Maven wrapper (`./mvnw` solo en UsuarioService; PabellonService usa `mvn`).

## Comandos

```bash
# Todo el stack
docker-compose up -d --build

# UsuarioService local (MySQL en Laragon)
cd backend/usuario-service && ./mvnw spring-boot:run
# UsuarioService local (PostgreSQL vía Docker)
cd backend/usuario-service && ./mvnw spring-boot:run -Dspring-boot.run.profiles=test

# PabellonService local (siempre PostgreSQL)
cd backend/pabellon-service && mvn spring-boot:run -Dspring-boot.run.profiles=test

# Frontend
cd frontend && npm install && ng serve

# Tests UsuarioService (JUnit 5 + Mockito)
cd backend/usuario-service && ./mvnw test

# Tests frontend (Vitest — NO Jasmine/Karma)
cd frontend && ng test
```

## Arquitectura

- **Frontend**: standalone components, `provideRouter` + `provideHttpClient(withInterceptors([authInterceptor]))`, functional guards. Entry: `src/main.ts` → `App` bootstrapped with `appConfig`. API URL: `environment.ts` (`localhost:8080/api`) / `environment.prod.ts` (`/api`). Build con `@angular/build`, pruebas con `@angular/build:unit-test` (Vitest).
- **UsuarioService**: Controller → Service → Repository. Perfiles: `application.properties` (MySQL default) / `application-test.yml` (PostgreSQL Docker). Packages: `auth_service`, `paciente`, `datos_clinicos`.
- **PabellonService**: mismo stack (Spring Boot 3.5 + JPA + JJWT 0.12.6), base propia `db_pabellon`, app class `PabellonApplication`. Sin MySQL — solo PostgreSQL.
- **Docker (nginx)**: `/api/pabellon/` → `pabellon-service:8082`, `/api/` → `backend:8081`.
- **LocalStorage**: claves en `constants.ts`: `token_clinica`, `portalSeleccionado`, `usuario_rut`, `usuario_id`, `usuario_nombre`, `usuario_roles`.
- **Formato frontend**: Prettier con `singleQuote: true`, `printWidth: 100`, parser `angular` para HTML.

## Convenciones

- Mantener estilo existente (nombres, imports, SCSS).
- Skills: `accessibility`, `frontend-design`, `seo`.
- `Contenedores_RedNorte/` es legado — ignorar.
