# Evaluación 3 — Paso 1: Análisis de Requerimientos y Planificación

## 1. Requerimientos Funcionales

| ID  | Requerimiento | Prioridad |
|-----|--------------|-----------|
| RF1 | Autenticación de usuarios mediante JWT (login con email o RUT) | Alta |
| RF2 | Roles de usuario: Administrador y Médico (cada uno con permisos distintos) | Alta |
| RF3 | CRUD de pacientes: listar, registrar, ver detalle (ficha clínica) | Alta |
| RF4 | CRUD de médicos: listar, registrar, editar (solo Admin) | Alta |
| RF5 | Activar/Desactivar pacientes (soft-delete) | Alta |
| RF6 | Filtro de pacientes por RUT o nombre (frontend) | Media |
| RF7 | Ficha clínica con datos clínicos: grupo sanguíneo, peso, altura, alergias, enfermedades crónicas | Alta |
| RF8 | Edición de ficha clínica por Médico y Admin | Alta |
| RF9 | Panel de estadísticas: total pacientes, pacientes activos | Media |
| RF10 | Despliegue mediante Docker Compose | Alta |

## 2. Requerimientos No Funcionales

| ID  | Requerimiento | Detalle |
|-----|--------------|---------|
| RNF1 | Seguridad | Autenticación JWT, contraseñas cifradas con BCrypt |
| RNF2 | Escalabilidad | Arquitectura basada en microservicios con Docker |
| RNF3 | Persistencia | Base de datos PostgreSQL |
| RNF4 | Frontend reactivo | Angular standalone components |
| RNF5 | API REST | Backend Spring Boot con endpoints RESTful |
| RNF6 | Compatibilidad | Navegadores modernos (Chrome, Firefox, Edge) |
| RNF7 | SEO básico | Meta tags, Open Graph, robots.txt, sitemap.xml |

## 3. Selección de Tecnologías

| Capa | Tecnología | Justificación |
|------|-----------|---------------|
| Backend | Spring Boot 3.5 + Java 17 | Framework maduro para REST APIs, integración nativa con JPA/Hibernate, seguridad Spring Security |
| Frontend | Angular 21 + TypeScript | Framework para SPA reactivas, standalone components, integración con HttpClient |
| Base de Datos | PostgreSQL 16 | Base de datos relacional robusta, soporte JSON, ideal para datos clínicos |
| Autenticación | JWT + BCrypt | Tokens stateless para microservicios, cifrado seguro de contraseñas |
| Contenedores | Docker + Docker Compose | Estandarización de entornos, despliegue reproducible |
| Testing Backend | JUnit 5 + Mockito | Framework estándar de pruebas unitarias en Java |
| Testing Frontend | Vitest | Framework moderno de pruebas para Angular, integrado con @angular/build |

## 4. Estrategia de Control de Versiones

- **Repositorio:** Git (GitHub)
- **Ramas:**
  - `main`: código en producción
  - `develop`: integración de features
  - `feature/*`: desarrollo de funcionalidades específicas
- **Flujo:** Feature Branch Workflow
  1. Crear rama `feature/nombre` desde `develop`
  2. Desarrollar y commitear cambios
  3. Pull Request a `develop` con revisión de código
  4. Merge a `main` para releases
- **Convención de commits:** `tipo(scope): mensaje` (ej: `feat(auth): implementar login JWT`)
