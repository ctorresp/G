# Paso 6: Presentación y Entrega del Proyecto

**Proyecto:** RedNorte Stack — Sistema de Gestión Clínica
**Actividad:** 3.2 — Taller de Alto Cómputo (TAITE 9)
**Duración:** 4 horas | **Equipo:** 3 integrantes

---

## 1. Estructura de la Presentación (10-15 min)

### Diapositiva 1: Portada
- Nombre del proyecto: **RedNorte Stack**
- Integrantes del equipo
- Fecha
- Asignatura

### Diapositiva 2: Problema y Solución
- **Contexto:** Clínica RedNorte necesita un sistema digital para gestionar pacientes, triajes, cirugías y reportes
- **Solución:** Sistema web con microservicios, frontend SPA y contenerización Docker

### Diapositiva 3: Requerimientos Funcionales
- Autenticación JWT con 5 roles
- CRUD pacientes con ficha clínica
- Registro de triaje
- Gestión de cirugías (solicitar, programar, completar)
- Lista de espera y penalizaciones
- Reportes de pérdidas

### Diapositiva 4: Tecnologías Utilizadas
- **Backend:** Spring Boot 3.5 + Java 17 + JPA/Hibernate
- **Frontend:** Angular 21 + TypeScript 5.9
- **Base de datos:** PostgreSQL 15 (Docker) / MySQL (local)
- **Contenedores:** Docker + Docker Compose
- **Autenticación:** JWT HS256 + BCrypt
- **Pruebas:** JUnit 5 + Mockito / Vitest

### Diapositiva 5: Arquitectura (Diagrama)
- Frontend Angular → nginx (proxy reverso) → 2 microservicios
- auth-service (puerto 8081): autenticación, pacientes, admin
- pabellon-service (puerto 8082): cirugías, triaje, pabellones
- Bases de datos PostgreSQL separadas por dominio

### Diapositiva 6: Estrategia de Branching (GitHub Flow)
- `main` → siempre deployable
- `develop` → rama de integración
- `feature/*` → ramas por módulo
- Pull Requests con revisión de código

### Diapositiva 7: Demo en Vivo (5 min)
**Escenario 1: Login y Directorio Pacientes**
1. Navegar a http://localhost:4200
2. Login como Admin (admin@rednorte.cl / Admin1234!)
3. Mostrar Directorio Pacientes con 2 pacientes cargados

**Escenario 2: Roles y permisos**
1. Login como Triajer (triajer@rednorte.cl / Triajer1234!)
2. Mostrar que ahora puede ver Directorio Pacientes (fix aplicado)
3. Mostrar que NO puede editar datos clínicos (403)

**Escenario 3: Flujo completo cirugía**
1. Login como Médico → Solicitar cirugía
2. Login como Coordinador → Ver solicitud pendiente → Programar
3. Login como Médico → Ver cirugía programada → Completar

**Escenario 4: Reportes**
1. Login como Coordinador
2. Ir a Reportes → Seleccionar rango de fechas
3. Mostrar reporte de pérdidas

### Diapositiva 8: Mejora Realizada
- **Bug encontrado:** `@PreAuthorize` en `PacienteController` excluía a Triajer y Coordinador
- **Solución:** Agregar `ROLE_TRIAJER` y `ROLE_COORDINADOR` al `hasAnyAuthority`
- **Archivos modificados:** `PacienteController.java` (líneas 25 y 41)
- **Lección aprendida:** Verificar que la seguridad no bloquee funcionalidades legítimas

### Diapositiva 9: Resultados de Pruebas
- **15 tests unitarios backend:** 0 fallos, 0 errores
- **5 contenedores Docker:** todos healthy
- **14 casos de prueba funcionales:** 13 OK, 1 corregido durante la actividad

### Diapositiva 10: Desafíos y Aprendizajes
| Desafío | Solución | Aprendizaje |
|---------|----------|-------------|
| Contraseñas en texto plano en BD | BCrypt + JWT | Seguridad en APIs REST |
| Microservicio sin endpoints de pabellón | Se creó PabellonService completo | Separación de dominios |
| Token compartido entre servicios | JWT con misma secret key | Comunicación stateless |
| Error 403 en Triajer/Coordinador | Fix en `@PreAuthorize` | Pruebas de roles exhaustivas |

### Diapositiva 11: Consideraciones de Escalabilidad
- Microservicios escalan horizontalmente (round-robin nginx)
- JWT stateless: no requiere sesión compartida
- Bases de datos separadas: evitan cuello de botella entre dominios
- Mejora futura: agregar caché Redis para consultas frecuentes

### Diapositiva 12: Cierre
- **Repositorio:** https://github.com/usuario/rednorte-stack
- **Tecnologías:** Spring Boot + Angular + Docker + PostgreSQL
- **Preguntas?**

---

## 2. Entregables

| # | Producto | Archivo | Estado |
|---|----------|---------|--------|
| 1 | Documento de análisis, tecnologías y branching | `01-analisis-requerimientos.md` | ✅ Completado |
| 2 | Diagrama de arquitectura y estructura de carpetas | `02-arquitectura.md` | ✅ Completado |
| 3 | Código backend funcional y endpoints documentados | `03-backend.md` + `BackEnd/` + `PabellonService/` | ✅ Completado |
| 4 | Código frontend funcional e interfaces documentadas | `04-frontend.md` + `Frontend/` | ✅ Completado |
| 5 | Documento de pruebas y consideraciones | `05-pruebas.md` | ✅ Completado |
| 6 | Presentación y código fuente | `06-presentacion.md` + repositorio completo | ✅ Completado |

---

## 3. Link a la Aplicación en Funcionamiento

```
Frontend:  http://localhost:4200
Backend:   http://localhost:8080/swagger-ui.html (si se agrega)
Pabellón:  http://localhost:8082
```

### Credenciales de Prueba

| Rol | Email | Contraseña |
|-----|-------|-----------|
| Administrador | admin@rednorte.cl | Admin1234! |
| Médico | medico@rednorte.cl | Medico1234! |
| Triajer | triajer@rednorte.cl | Triajer1234! |
| Coordinador | coordinador@rednorte.cl | Coordi1234! |
| Paciente | paciente@rednorte.cl | Paciente1234! |

### Cómo Iniciar

```bash
# Desde la raíz del proyecto
docker compose up -d --build

# Verificar estado
docker ps

# Detener
docker compose down
```

---

## 4. Reflexión del Equipo

### ¿Qué funcionó bien?
- La arquitectura de microservicios permitió dividir el trabajo entre los integrantes del equipo sin conflictos
- Docker Compose facilitó la reproducibilidad del entorno en diferentes máquinas
- El fix de `@PreAuthorize` se encontró y corrigió rápidamente gracias a los tests que verificaron el comportamiento por rol

### ¿Qué dificultades enfrentamos?
- La sincronización del JWT entre auth-service y pabellon-service (deben compartir la misma secret key)
- El manejo de errores del frontend cuando el backend responde con 403
- La curva de aprendizaje de la configuración de nginx como proxy reverso

### ¿Qué mejoraría para una próxima versión?
- Agregar Swagger/OpenAPI para documentación interactiva de endpoints
- Implementar tests para pabellon-service (actualmente solo cubre auth-service)
- Agregar caché Redis para optimizar consultas a catálogos (especialidades, pabellones)
- Migrar el dashboard monolítico a componentes separados para mejor mantenibilidad
- Agregar CI/CD con GitHub Actions para ejecutar tests automáticamente

---

*Documento generado como parte de la Actividad 3.2 — Taller de Alto Cómputo*
*IL 3.2 — Diseña componentes frontend y backend, utilizando distintos lenguajes de programación y tecnologías*
