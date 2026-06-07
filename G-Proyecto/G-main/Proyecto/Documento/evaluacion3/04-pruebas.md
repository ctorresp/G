# Evaluación 3 — Paso 5: Integración, Pruebas y Consideraciones

## 1. Estrategia de Pruebas

### Backend (Spring Boot)
- **Framework:** JUnit 5 + Mockito
- **Tipo:** Pruebas unitarias con mocking de dependencias (repositorios, servicios externos)
- **Cobertura:** Servicios (lógica de negocio) y Controladores (HTTP responses)
- **No requiere BD:** Mockeamos las capas de repositorio con Mockito

### Frontend (Angular)
- **Framework:** Vitest (vía @angular/build:unit-test)
- **Tipo:** Pruebas unitarias de componentes con TestBed + HttpClientTestingModule
- **Cobertura:** Creación de componentes, métodos clave, llamadas HTTP, navegación

## 2. Resultados de Pruebas Backend

| Test | Resultado |
|------|-----------|
| AuthServiceTest.login_conEmailValido_deberiaRetornarToken | ✅ |
| AuthServiceTest.login_conRutValido_deberiaResolverEmailYRetornarToken | ✅ |
| AuthServiceTest.login_sinEmailNiRut_deberiaLanzarExcepcion | ✅ |
| AuthServiceTest.registrarPaciente_conDatosValidos_deberiaCrearUsuarioYPaciente | ✅ |
| AuthServiceTest.registrarPaciente_conEmailDuplicado_deberiaLanzarExcepcion | ✅ |
| AuthServiceTest.registrarUsuarioAdmin_deberiaCrearUsuarioConRol | ✅ |
| AuthServiceTest.validarToken_conTokenValido_deberiaRetornarDTO | ✅ |
| AuthServiceTest.validarToken_conTokenInvalido_deberiaLanzarExcepcion | ✅ |
| AdminMedicoServiceTest.listarMedicos_deberiaRetornarLista | ✅ |
| AdminMedicoServiceTest.actualizarMedico_conDatosValidos_deberiaActualizar | ✅ |
| AdminMedicoServiceTest.actualizarMedico_conIdInvalido_deberiaLanzarExcepcion | ✅ |
| AdminMedicoServiceTest.actualizarMedico_conRutDuplicado_deberiaLanzarExcepcion | ✅ |
| PacienteServiceTest.listarTodos_deberiaRetornarListaDeDTOs | ✅ |
| PacienteServiceTest.buscarPorRut_conRutExistente_deberiaRetornarDTO | ✅ |
| PacienteServiceTest.buscarPorRut_conRutInexistente_deberiaLanzarExcepcion | ✅ |
| PacienteServiceTest.actualizarDatosClinicos_deberiaActualizarCampos | ✅ |
| PacienteServiceTest.desactivarPaciente_deberiaCambiarEstado | ✅ |
| PacienteServiceTest.activarPaciente_deberiaCambiarEstado | ✅ |
| AuthControllerTest.login_conCredencialesValidas_deberiaRetornar200 | ✅ |
| AuthControllerTest.login_conCredencialesInvalidas_deberiaRetornar401 | ✅ |
| AuthControllerTest.registrarPaciente_conExito_deberiaRetornar201 | ✅ |
| AuthControllerTest.registrarPaciente_conError_deberiaRetornar400 | ✅ |
| AuthControllerTest.validarToken_conHeaderValido_deberiaRetornar200 | ✅ |
| AuthControllerTest.validarToken_sinHeader_deberiaRetornar400 | ✅ |
| AdminMedicoControllerTest.listarMedicos_deberiaRetornarLista | ✅ |
| AdminMedicoControllerTest.actualizarMedico_conExito_deberiaRetornar200 | ✅ |
| AdminMedicoControllerTest.actualizarMedico_conError_deberiaRetornar400 | ✅ |
| PacienteControllerTest.listarTodos_deberiaRetornar200 | ✅ |
| PacienteControllerTest.buscarPorRut_existente_deberiaRetornar200 | ✅ |
| PacienteControllerTest.buscarPorRut_inexistente_deberiaRetornar404 | ✅ |
| PacienteControllerTest.desactivar_deberiaRetornar200 | ✅ |
| PacienteControllerTest.activar_deberiaRetornar200 | ✅ |

## 3. Resultados de Pruebas Frontend

| Test | Resultado |
|------|-----------|
| App.should create the app | ✅ |
| Home.should create the component | ✅ |
| Home.should navigate to login with rol Médico | ✅ |
| Home.should navigate to login with rol Administrador | ✅ |
| Login.should create | ✅ |
| Login.should read portalSeleccionado from localStorage on init | ✅ |
| Login.should call login endpoint and navigate on success | ✅ |
| Login.should use rut field when input does not contain @ | ✅ |
| Login.should show error message on failed login | ✅ |
| Dashboard.should create | ✅ |
| Dashboard.should redirect to login if no token on init | ✅ |
| Dashboard.should load patients on init if token exists | ✅ |
| Dashboard.should return filtered patients by terminoBusqueda | ✅ |
| Dashboard.should calculate totalPacientes and pacientesActivos | ✅ |
| Dashboard.should call toggle API and switch patient estado | ✅ |
| Dashboard.should load medicos on cambiarVista to medicos | ✅ |
| Dashboard.should toggle between views with cambiarVista | ✅ |
| Dashboard.should format RUT properly | ✅ |
| Dashboard.should handle empty RUT format | ✅ |
| Dashboard.should clear session on cerrarSesion | ✅ |
| authGuard.should allow activation when token exists | ✅ |
| authGuard.should redirect to /login when no token | ✅ |
| authInterceptor.should add Authorization header when token exists | ✅ |
| authInterceptor.should not modify request when no token | ✅ |

## 4. Consideraciones de Rendimiento

- **Backend:** Consultas JPA optimizadas con fetch joins para evitar N+1 queries
- **Frontend:** Uso de `ChangeDetectorRef.detectChanges()` para renderizado controlado
- **Base de Datos:** Índices implícitos en claves primarias y unique constraints en email/RUT
- **Caché:** JWT reduce consultas a BD en cada request (estado de sesión en token)
- **Compresión:** nginx comprime respuestas estáticas (CSS/JS) automáticamente

## 5. Consideraciones de Escalabilidad

- **Microservicios:** Arquitectura preparada para separar auth-service, paciente-service en contenedores independientes
- **Docker:** Cada servicio escala horizontalmente con `docker compose up --scale`
- **Base de Datos:** PostgreSQL soporta réplicas de lectura para escalar consultas
- **JWT Stateless:** No requiere sesiones compartidas entre instancias del backend
- **Frontend estático:** Servido por nginx, puede cachearse en CDN

## 6. Consideraciones de Compatibilidad

- **Navegadores:** Chrome 90+, Firefox 88+, Edge 90+, Safari 14+ (ES2022 + CSS moderno)
- **Responsive:** Diseño adaptable a tablets y móviles mediante media queries
- **API:** Endpoints RESTful con JSON, versionados implícitamente por ruta (/api/v2 en futuro)
- **Docker:** Compatible con Docker Engine 24+ y Docker Compose 2+
- **Base de Datos:** PostgreSQL 14+ (features usadas: JSON columns, joins, unique constraints)
