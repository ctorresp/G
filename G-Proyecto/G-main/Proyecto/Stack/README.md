# RedNorte — Sistema de Gestión Clínica

## Stack

- **Backend**: Spring Boot 3.5 + JPA/Hibernate + JWT
- **Frontend**: Angular 21 (standalone components)
- **Base de datos**: PostgreSQL 15
- **Infra**: Docker Compose

## Inicio rápido

```bash
# 1. Iniciar todos los servicios
docker-compose up -d --build

# 2. Acceder
#    Frontend: http://localhost:4200
#    Backend:  http://localhost:8080/api
#    Pabellón: http://localhost:8082/api/pabellon
```

## Usuarios por defecto

| Rol | Email | Password |
|-----|-------|----------|
| Administrador | admin@rednorte.cl | Admin1234! |
| Médico | medico@rednorte.cl | Medico1234! |
| Paciente | paciente@rednorte.cl | Paciente1234! |
| Triajer | triajer@rednorte.cl | Triajer1234! |
| Coordinador | coordinador@rednorte.cl | Coordi1234! |

## Variables de entorno

| Variable | Default | Descripción |
|----------|---------|-------------|
| `JWT_SECRET` | `RedNorte2024SecretKey...` | Clave para firmar tokens |
| `JWT_EXPIRATION_MS` | `86400000` | Expiración del token (24h) |
| `SEED_PASSWORD_ADMIN` | `Admin1234!` | Password seed admin |
| `SEED_PASSWORD_MEDICO` | `Medico1234!` | Password seed médico |
| `SEED_PASSWORD_PACIENTE` | `Paciente1234!` | Password seed paciente |
| `SEED_PASSWORD_TRIAJER` | `Triajer1234!` | Password seed triajer |
| `SEED_PASSWORD_COORDINADOR` | `Coordi1234!` | Password seed coordinador |

## Desarrollo local

### Backend

```bash
cd BackEnd
# Perfil MySQL local (requiere Laragon o MySQL)
./mvnw spring-boot:run
# Perfil Docker (PostgreSQL)
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

### Frontend

```bash
cd Frontend
npm install
ng serve
```

### PabellonService

```bash
cd PabellonService
mvn spring-boot:run -Dspring-boot.run.profiles=test
```
