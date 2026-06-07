# Evaluación 3 — Paso 2: Diseño de Arquitectura

## 1. Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Cliente Web (Navegador)                      │
│                          http://localhost:4200                       │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────────┐
│                         Frontend (Angular 21)                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────────────┐  │
│  │   Home   │  │  Login   │  │Dashboard │  │   Auth Interceptor │  │
│  │  Portal  │  │  JWT     │  │ CRUD     │  │  (Bearer Token)    │  │
│  │  Select  │  │  Auth    │  │Pacientes │  └────────────────────┘  │
│  └──────────┘  └──────────┘  └──────────┘                          │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │  environment.apiUrl → /api (relativo en prod, localhost:8080 │   │
│  │  en dev)                                                    │   │
│  └──────────────────────────────────────────────────────────────┘   │
└──────────────────────────────┬──────────────────────────────────────┘
                               │  HTTP / API REST
                               ▼
┌──────────────────────────────────────────────────────────────────────┐
│                    Backend (Spring Boot 3.5)                        │
│                                                                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌───────────────────┐   │
│  │   AuthController │  │PacienteController│  │AdminMedicoCtrl   │   │
│  │  /api/auth/*     │  │/api/pacientes/*  │  │/api/admin/medicos│   │
│  └────────┬────────┘  └────────┬────────┘  └────────┬──────────┘   │
│           │                    │                     │              │
│  ┌────────▼────────────────────▼─────────────────────▼──────────┐   │
│  │                    Service Layer                              │   │
│  │  AuthService · PacienteService · AdminMedicoService           │   │
│  └────────┬────────────────────┬─────────────────────┬──────────┘   │
│           │                    │                     │              │
│  ┌────────▼────────────────────▼─────────────────────▼──────────┐   │
│  │                   Repository Layer (JPA)                      │   │
│  │  UsuarioRepository · PacienteRepository · RolRepository       │   │
│  └────────────────────────────┬──────────────────────────────────┘   │
│                               │                                     │
│  ┌────────────────────────────▼──────────────────────────────────┐   │
│  │                    Security Layer                              │   │
│  │  JwtTokenProvider · JwtAuthFilter · SecurityConfig            │   │
│  │  CorsConfig · CustomUserDetailsService                        │   │
│  └───────────────────────────────────────────────────────────────┘   │
└──────────────────────────────┬──────────────────────────────────────┘
                               │  JDBC / Port 5432
                               ▼
┌──────────────────────────────────────────────────────────────────────┐
│                    PostgreSQL 16                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                 │
│  │  usuarios   │  │  roles      │  │  pacientes   │                 │
│  │  (auth)     │  │  (catálogo) │  │  (fichas)    │                 │
│  └─────────────┘  └─────────────┘  └─────────────┘                 │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │  usuario_roles (join table M:N)                              │   │
│  └──────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────────┘
```

## 2. Estructura de Carpetas del Proyecto

```
Stack/
├── agents.md                          # Instrucciones para IA
├── Documento/
│   ├── Contexto del caso.txt           # Enunciado del proyecto
│   └── evaluacion3/                    # Documentos de evaluación
│       ├── 01-analisis-requerimientos.md
│       ├── 02-arquitectura.md
│       ├── 03-endpoints.md
│       ├── 04-pruebas.md
│       └── 05-presentacion.md
│
├── BackEnd/                            # Backend Spring Boot
│   ├── pom.xml
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── nginx.conf (frontend proxy)
│   └── src/
│       ├── main/
│       │   ├── java/cl/duoc/rednorte/
│       │   │   ├── RedNorteApplication.java
│       │   │   ├── auth_service/
│       │   │   │   ├── config/           CorsConfig, DataInitializer
│       │   │   │   ├── controller/       AuthController, AdminMedicoController
│       │   │   │   ├── dto/              LoginRequest/Response, RegistroRequest, MedicoUpdateRequest
│       │   │   │   ├── model/            Usuario, Rol
│       │   │   │   ├── repository/       UsuarioRepository, RolRepository
│       │   │   │   ├── security/         SecurityConfig, JwtTokenProvider, JwtAuthFilter, CustomUserDetailsService
│       │   │   │   └── service/          AuthService, AdminMedicoService
│       │   │   ├── datos_clinicos/
│       │   │   │   ├── dto/              DatosClinicosDTO, DatosClinicosUpdateRequestDTO
│       │   │   │   └── model/            DatosClinicos
│       │   │   └── paciente/
│       │   │       ├── controller/       PacienteController
│       │   │       ├── dto/              PacienteResponseDTO
│       │   │       ├── model/            Paciente
│       │   │       ├── repository/       PacienteRepository
│       │   │       └── service/          PacienteService
│       │   └── resources/
│       │       ├── application.properties
│       │       └── application-test.yml
│       └── test/java/cl/duoc/rednorte/
│           ├── auth_service/
│           │   ├── AuthServiceApplicationTests.java
│           │   ├── controller/           AuthControllerTest, AdminMedicoControllerTest
│           │   └── service/              AuthServiceTest, AdminMedicoServiceTest
│           └── paciente/
│               ├── controller/           PacienteControllerTest
│               └── service/              PacienteServiceTest
│
├── Frontend/                           # Frontend Angular
│   ├── package.json
│   ├── angular.json
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── tsconfig.json
│   ├── tsconfig.spec.json
│   ├── public/
│   │   ├── favicon.ico
│   │   ├── favicon.svg
│   │   ├── robots.txt
│   │   └── sitemap.xml
│   └── src/
│       ├── index.html
│       ├── main.ts
│       ├── styles.scss
│       ├── environments/
│       │   ├── environment.ts
│       │   └── environment.prod.ts
│       └── app/
│           ├── app.ts                   # Componente raíz
│           ├── app.html                 # <router-outlet>
│           ├── app.scss
│           ├── app.spec.ts
│           ├── app.config.ts            # Proveedores globales
│           ├── app.routes.ts            # Definición de rutas
│           ├── auth.guard.ts            # Guard de autenticación
│           ├── auth.guard.spec.ts
│           ├── auth.interceptor.ts      # Interceptor HTTP (JWT)
│           ├── auth.interceptor.spec.ts
│           ├── home.ts                  # Página de inicio
│           ├── home.html
│           ├── home.scss
│           ├── home.spec.ts
│           ├── login/
│           │   ├── login.ts
│           │   ├── login.html
│           │   ├── login.scss
│           │   └── login.spec.ts
│           └── dashboard/
│               ├── dashboard.ts
│               ├── dashboard.html
│               ├── dashboard.scss
│               └── dashboard.spec.ts
│
└── .agents/                            # Skills de IA
    └── skills/
        ├── accessibility/
        ├── frontend-design/
        └── seo/
```

## 3. Flujo de Datos (Ejemplo: Login + Listar Pacientes)

```
1. Usuario ingresa credenciales en /login
2. Frontend POST /api/auth/login { email, contrasena }
3. Backend AuthController → AuthService → UsuarioRepository
4. Valida credenciales con BCrypt, genera JWT
5. Responde { token, roles, nombre }
6. Frontend guarda token en localStorage
7. Frontend redirige a /dashboard
8. Dashboard hace GET /api/pacientes con header Authorization: Bearer <token>
9. Auth Interceptor agrega token automáticamente
10. Backend JwtAuthFilter valida token → SecurityContext → PacienteController
11. PacienteService → PacienteRepository → PostgreSQL
12. Responde con lista de pacientes
```
