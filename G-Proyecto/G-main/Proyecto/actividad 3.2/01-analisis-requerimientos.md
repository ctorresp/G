# Paso 1: Análisis de Requerimientos y Planificación

**Asignatura:** Taller de Alto Cómputo
**Indicador de Logro:** IL 3.2 — Diseña componentes frontend y backend utilizando distintos lenguajes de programación y tecnologías
**Proyecto:** RedNorte Stack — Sistema de Gestión Clínica

---

## 1. Análisis de Requerimientos

### 1.1 Requerimientos Funcionales

| ID  | Descripción | Módulo | Prioridad |
|-----|------------|--------|-----------|
| RF-01 | Autenticación de usuarios mediante JWT (login con email/rut y contraseña BCrypt) | Auth | Alta |
| RF-02 | Registro de nuevos pacientes con ficha clínica completa (previsión, datos clínicos sensibles) | Pacientes | Alta |
| RF-03 | Listar todos los pacientes con datos clínicos | Pacientes | Alta |
| RF-04 | Buscar paciente por RUT | Pacientes | Alta |
| RF-05 | Editar datos clínicos del paciente (alergias, peso, altura, grupo sanguíneo, etc.) | Pacientes | Alta |
| RF-06 | Eliminar paciente (solo Admin) | Pacientes | Media |
| RF-07 | Control de acceso por roles: ROLE_ADMIN, ROLE_MEDICO, ROLE_TRIAJER, ROLE_COORDINADOR, ROLE_PACIENTE | Auth | Alta |
| RF-08 | Registro de triaje asociado a paciente (vista Triajer) | Triaje | Alta |
| RF-09 | Solicitud, programación, cancelación y finalización de cirugías | Cirugías | Alta |
| RF-10 | Gestión de lista de espera quirúrgica con prioridades | ListaEspera | Media |
| RF-11 | Administración de penalizaciones por inasistencia a cirugías | Penalizaciones | Media |
| RF-12 | Reporte de pérdidas económicas por cirugías no realizadas (rango de fechas) | Reportes | Media |
| RF-13 | CRUD de médicos (solo Admin) | Admin | Alta |
| RF-14 | Activación/desactivación de usuarios (solo Admin) | Admin | Alta |
| RF-15 | Catálogo de pabellones disponibles y especialidades médicas | Pabellón | Alta |
| RF-16 | Reasignación de cirugías entre pabellones | Reasignación | Media |

### 1.2 Requerimientos No Funcionales

| ID   | Descripción |
|------|------------|
| RNF-01 | Arquitectura de **microservicios**: auth-service (gestión pacientes) y pabellon-service (cirugías, triaje) independientes |
| RNF-02 | Comunicación **stateless** mediante tokens JWT (HS256, 24h de expiración) |
| RNF-03 | Contenerización con **Docker Compose** para entornos reproducibles |
| RNF-04 | Bases de datos **separadas por microservicio** (PostgreSQL 15), una por dominio |
| RNF-05 | Frontend **SPA** con Angular standalone components y routing lazy |
| RNF-06 | Seguridad: contraseñas cifradas con **BCrypt**, CORS configurado, endpoints protegidos por `@PreAuthorize` |
| RNF-07 | Escalabilidad horizontal: cada microservicio escala independientemente |
| RNF-08 | Health checks en base de datos para orquestación correcta de dependencias |
| RNF-09 | Pruebas unitarias con JUnit 5 + Mockito (backend) y Vitest (frontend) |

---

## 2. Selección Justificada de Tecnologías

| Capa | Tecnología | Versión | Justificación |
|------|-----------|---------|---------------|
| **Backend** | Spring Boot | 3.5.0 | Framework maduro con soporte integrado para JPA, Security, y REST. Ecosistema amplio y facilidad para construir microservicios |
| **Lenguaje Backend** | Java | 17 | LTS estable, compatible con Spring Boot 3.5, rendimiento predecible y tipado fuerte |
| **ORM** | Hibernate / JPA | 3.5.0 | Mapeo objeto-relacional automático, soporte para JSON columns (@Column(columnDefinition = "jsonb")) y relaciones 1:1, N:M |
| **Seguridad** | Spring Security + JJWT | 0.12.6 | Integración nativa con Spring Boot. Implementación de autenticación stateless JWT con filtros personalizados |
| **Base de Datos** | PostgreSQL 15 (Alpine) | 15 Alpine | Robustez transaccional, soporte nativo JSONB para datos clínicos. Imagen Alpine minimiza tamaño de contenedor (~200MB) |
| **Base de Datos Local** | MySQL (Laragon) | 8.x | Entorno de desarrollo liviano. Perfil alternativo en application.properties |
| **Frontend** | Angular | 21.2 | Framework completo para SPA, tipado fuerte con TypeScript, routing nativo y HTTP client con interceptors |
| **Lenguaje Frontend** | TypeScript | 5.9 | Tipado estático opcional, compila a JavaScript, integración nativa con Angular |
| **Pruebas Frontend** | Vitest | 4.0 | Moderno, rápido (basado en Vite), soporte nativo para TypeScript y jsdom para DOM simulado |
| **Contenedores** | Docker + Compose | 29.4 | Estandarización de entornos desarrollo/producción, orquestación multi-servicio con health checks y redes |
| **Proxy Reverso** | nginx (Alpine) | stable | Servidor web liviano para servir SPA, routing de API a microservicios, compresión y caché |
| **JWT** | io.jsonwebtoken (JJWT) | 0.12.6 | Librería oficial para JWT en Java, soporta HS256, RS256, claims personalizados |
| **Herramientas Dev** | Lombok | Última | Reducción de boilerplate (getters, setters, constructores) mediante anotaciones |

### 2.1 Justificación de la Arquitectura

Se eligió **microservicios** sobre monolito por:

1. **Separación de dominios**: auth-service maneja autenticación y pacientes; pabellon-service maneja cirugías, triaje y operaciones clínicas
2. **Escalabilidad independiente**: el módulo de cirugías puede escalar sin afectar la autenticación
3. **Bases de datos independientes**: evita acoplamiento, cada servicio tiene su propio schema PostgreSQL
4. **Despliegue autónomo**: cada servicio puede actualizarse sin afectar al otro

### 2.2 Justificación de Frontend Standalone

Angular standalone components (sin NgModules) reduce la complejidad del boilerplate y facilita el lazy loading. Angular 21 ofrece mejoras en rendimiento de compilación y detección de cambios.

---

## 3. Estrategia de Control de Versiones

### 3.1 Modelo: GitHub Flow

```
main  ──────────────────────────────────────────────
  \                   develop (integración)
   \                      |
    └── feature/backend-auth ──┘
    └── feature/backend-pacientes ──┘
    └── feature/backend-pabellon ──┘
    └── feature/frontend-dashboard ──┘
    └── feature/frontend-triaje ──┘
    └── feature/frontend-coordinador ──┘
```

### 3.2 Reglas del Equipo

| Regla | Descripción |
|-------|-------------|
| `main` | Siempre deployable. Solo recibe merges desde `develop` |
| `develop` | Rama de integración diaria. Todos los feature branches se mergean aquí |
| `feature/*` | Una rama por módulo/historia. Duración máxima: 1 día |
| Pull Requests | Mínimo 1 revisión de compañero antes de mergear a `develop` |
| Commits | Prefijo semántico: `feat:`, `fix:`, `docs:`, `refactor:`, `test:` |
| Push directo | Prohibido a `main` y `develop` |

### 3.3 Convención de Commits

```
feat: agregar CRUD de pacientes con datos clínicos
fix: corregir error 403 en listar pacientes para rol Triajer
docs: documentar endpoints del módulo auth
refactor: separar lógica de validación en PacienteService
test: agregar tests unitarios para PacienteController
```

### 3.4 Roles del Equipo (3 integrantes)

| Integrante | Módulos |
|------------|---------|
| **A** | Backend: auth-service (autenticación, pacientes, admin). Docker Compose. |
| **B** | Backend: pabellon-service (cirugías, triaje, lista espera, penalizaciones). |
| **C** | Frontend: dashboard completo, integración con backend, documentación. |

**Flujo de trabajo diario:**

1. `git checkout develop && git pull`
2. `git checkout -b feature/mi-modulo`
3. Trabajar y commitear localmente
4. `git push -u origin feature/mi-modulo`
5. Abrir Pull Request a `develop`
6. Compañero revisa y aprueba
7. Mergear a `develop`
8. Al final del día, si `develop` está estable, merge a `main`

---

*Documento generado como parte de la Actividad 3.2 — Taller de Alto Cómputo*
*Proyecto: RedNorte Stack — Sistema de Gestión Clínica*
