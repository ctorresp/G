# RedNorte — Sistema de Gestión Clínica

## Stack

Proyecto ubicado en `G-Proyecto/G-main/Proyecto/Stack/`. Para todo el detalle (comandos, arquitectura, convenciones), leer ese directorio:

**`G-Proyecto/G-main/Proyecto/Stack/AGENTS.md`**

## Resumen rápido

| Capa | Tecnología | Puerto |
|------|-----------|--------|
| Frontend | Angular 21 standalone + SCSS + Vitest | 4200 |
| UsuarioService (auth+patients) | Spring Boot 3.5 / JPA / JWT | 8081 → 8080 (Docker) |
| PabellonService (surgeries/OR) | Spring Boot 3.5 / JPA / JWT | 8082 |
| DB (Docker) | PostgreSQL 15 | 5432 / 5433 / 5434 |
| DB (local UsuarioService) | MySQL (Laragon) | 3306 |

Java 17, Lombok, Maven wrapper (`./mvnw`).

## Comandos principales

```bash
# Todo el stack
docker-compose up -d --build

# Frontend
cd Frontend && npm install && ng serve

# UsuarioService local (PostgreSQL vía Docker)
cd BackEnd/UsuarioService && ./mvnw spring-boot:run -Dspring-boot.run.profiles=test

# PabellonService local
cd BackEnd/PabellonService && mvn spring-boot:run -Dspring-boot.run.profiles=test

# Tests UsuarioService
cd BackEnd/UsuarioService && ./mvnw test

# Tests frontend (Vitest)
cd Frontend && ng test
```

## Usuarios por defecto

| Rol | Email | Password |
|-----|-------|----------|
| Admin | admin@rednorte.cl | Admin1234! |
| Médico | medico@rednorte.cl | Medico1234! |
| Triajer | triajer@rednorte.cl | Triajer1234! |
| Coordinador | coordinador@rednorte.cl | Coordi1234! |

Ver `G-Proyecto/G-main/Proyecto/Stack/AGENTS.md` para comandos detallados, perfiles de base de datos y convenciones de código.
