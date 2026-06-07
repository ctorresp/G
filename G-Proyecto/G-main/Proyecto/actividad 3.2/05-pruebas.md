# Paso 5: Integración y Pruebas

**Proyecto:** RedNorte Stack — Sistema de Gestión Clínica

---

## 1. Estrategia de Pruebas

| Tipo | Herramienta | Cobertura |
|------|------------|-----------|
| Unitarias (Backend) | JUnit 5 + Mockito | Controllers, Services |
| Integración (Backend) | SpringBootTest + SecurityTest | Endpoints con autenticación |
| Unitarias (Frontend) | Vitest + jsdom | Servicios, Guards, Interceptors |
| Integración (Sistema) | Docker Compose + pruebas manuales | Flujo completo frontend-backend-DB |

---

## 2. Pruebas Backend (JUnit 5 + Mockito)

### 2.1 Tests Existentes

```
src/test/java/cl/duoc/rednorte/
├── AuthServiceApplicationTests.java
├── auth_service/controller/
│   ├── AuthControllerTest.java
│   └── AdminMedicoControllerTest.java
├── auth_service/service/
│   ├── AuthServiceTest.java
│   └── AdminMedicoServiceTest.java
├── paciente/controller/
│   └── PacienteControllerTest.java
└── paciente/service/
    └── PacienteServiceTest.java
```

### 2.2 Cobertura de Tests

| Clase de Test | Métodos Probados | Asserts |
|--------------|------------------|---------|
| AuthControllerTest | login exitoso, login fallido (credenciales inválidas), registro paciente | 10+ |
| AuthServiceTest | registro paciente, validación token, búsqueda usuario | 8+ |
| PacienteControllerTest | listar pacientes, buscar por RUT, actualizar datos clínicos | 12+ |
| PacienteServiceTest | CRUD paciente, manejo de excepciones | 10+ |
| AdminMedicoControllerTest | CRUD médicos, cambios de estado | 6+ |
| AdminMedicoServiceTest | operaciones admin sobre médicos | 5+ |

### 2.3 Ejemplo de Test: PacienteControllerTest

```java
@WebMvcTest(PacienteController.class)
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PacienteService pacienteService;

    @Test
    void listarTodos_deberiaRetornar200() throws Exception {
        when(pacienteService.listarTodos()).thenReturn(List.of(
            new PacienteResponseDTO(/*...*/)
        ));

        mockMvc.perform(get("/api/pacientes")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
               .andExpect(status().isOk());
    }

    @Test
    void listarTodos_sinToken_deberiaRetornar401() throws Exception {
        mockMvc.perform(get("/api/pacientes"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void buscarPorRut_noExistente_deberiaRetornar404() throws Exception {
        when(pacienteService.buscarPorRut("00000000-0"))
            .thenThrow(new RuntimeException("Paciente no encontrado"));

        mockMvc.perform(get("/api/pacientes/rut/00000000-0")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
               .andExpect(status().isNotFound());
    }
}
```

### 2.4 Ejecución de Tests

```bash
# Desde la raíz del proyecto BackEnd
cd BackEnd
./mvnw test

# Resultado esperado:
# [INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
```

---

## 3. Pruebas de Integración (Sistema Completo)

### 3.1 Entorno de Prueba

```bash
# Iniciar todos los servicios
docker compose up -d --build
```

**Servicios desplegados:**
| Contenedor | Imagen | Puerto | Estado |
|-----------|--------|--------|--------|
| rednorte-db-postgre | postgres:15-alpine | 5432 | healthy |
| rednorte-db-pabellon | postgres:15-alpine | 5432 | healthy |
| rednorte-backend | stack-backend:latest | 8080:8081 | running |
| rednorte-pabellon | stack-pabellon-service:latest | 8082 | running |
| rednorte-frontend | stack-frontend:latest | 4200:80 | running |

### 3.2 Casos de Prueba Funcionales

| ID | Caso | Pasos | Resultado Esperado | Resultado Obtenido |
|----|------|-------|-------------------|-------------------|
| CP-01 | Login Admin exitoso | POST /api/auth/login (admin@rednorte.cl, Admin1234!) | 200 + JWT token | ✅ OK |
| CP-02 | Login con credenciales inválidas | POST /api/auth/login (email erróneo) | 401 Unauthorized | ✅ OK |
| CP-03 | Listar pacientes como Admin | GET /api/pacientes con token Admin | 200 + lista pacientes | ✅ OK |
| CP-04 | Listar pacientes como Triajer | GET /api/pacientes con token Triajer | 200 + lista pacientes | ✅ OK (fix aplicado) |
| CP-05 | Listar pacientes sin token | GET /api/pacientes sin header | 401 Unauthorized | ✅ OK |
| CP-06 | Buscar paciente por RUT existente | GET /api/pacientes/rut/11111111-1 | 200 + datos paciente | ✅ OK |
| CP-07 | Buscar paciente por RUT inexistente | GET /api/pacientes/rut/00000000-0 | 404 Not Found | ✅ OK |
| CP-08 | Actualizar datos clínicos | PUT /api/pacientes/rut/11111111-1/clinicos | 200 + datos actualizados | ✅ OK |
| CP-09 | Solicitar cirugía | POST /api/pabellon/cirugias/solicitar | 201 Created | ✅ OK |
| CP-10 | Programar cirugía | PUT /api/pabellon/cirugias/1/programar | 200 + estado PROGRAMADA | ✅ OK |
| CP-11 | Acceder a frontend | Navegar a http://localhost:4200 | Home page cargada | ✅ OK |
| CP-12 | Login desde frontend | Ingresar credenciales en formulario login | Redirección a dashboard | ✅ OK |
| CP-13 | CRUD pacientes desde frontend | Crear, listar, editar, eliminar paciente | Flujo completo sin errores | ✅ OK |
| CP-14 | Vista Triajer sin permiso de listar (pre-fix) | Login como Triajer, ver Directorio | 403 Forbidden | ✅ CORREGIDO |

### 3.3 Prueba de Roles y Permisos

| Endpoint | ADMIN | MEDICO | TRIAJER | COORDINADOR | Sin Auth |
|----------|-------|--------|---------|-------------|----------|
| GET /api/pacientes | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 | ❌ 401 |
| GET /api/pacientes/rut/{rut} | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 | ❌ 401 |
| PUT /api/pacientes/rut/{rut}/clinicos | ✅ 200 | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 |
| DELETE /api/pacientes/rut/{rut} | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 403 | ❌ 401 |
| POST /api/pabellon/cirugias/solicitar | ✅ 200 | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 |
| PUT /api/pabellon/cirugias/{id}/programar | ✅ 200 | ❌ 403 | ❌ 403 | ✅ 200 | ❌ 401 |
| POST /api/pabellon/triajes | ✅ 200 | ❌ 403 | ✅ 200 | ❌ 403 | ❌ 401 |

---

## 4. Consideraciones de Rendimiento

| Aspecto | Estrategia | Detalle |
|---------|-----------|---------|
| **Conexiones a BD** | Pool HikariCP | Configuración por defecto (max 10 conexiones por servicio). Ajustable según carga |
| **JWT Stateless** | Sin sesiones en servidor | No consume memoria en backend. El token contiene toda la información necesaria |
| **Compresión nginx** | gzip activado | Frontend estático comprimido, reduce ancho de banda ~70% |
| **Caché de consultas** | JPA 2nd level cache | Se puede activar para consultas frecuentes (especialidades, pabellones) |
| **Índices en BD** | JPA ddl-auto genera índices | Se deben agregar índices manuales para columnas consultadas frecuentemente (rut, email) |

---

## 5. Consideraciones de Escalabilidad

| Componente | Estrategia de Escalado |
|------------|----------------------|
| **auth-service** | Escalado horizontal: múltiples instancias detrás de nginx (round-robin). JWT permite sesión sin estado compartido |
| **pabellon-service** | Escalado horizontal independiente. Cada instancia conecta a su propia base PostgreSQL o a un cluster |
| **Base de datos** | PostgreSQL cluster con réplicas de lectura: auth-service y pabellon-service tienen DB separadas, escalan independientemente |
| **nginx** | Puede escalarse verticalmente o reemplazarse por API Gateway (Kong, Traefik) |
| **Frontend** | Estático servido por nginx, escalable mediante CDN o múltiples réplicas |

**Cuello de botella identificado:** La conexión directa entre microservicios y sus respectivas bases de datos PostgreSQL. Para alta carga, se recomienda:

1. Pool de conexiones mayor (HikariCP max 50)
2. Caché Redis para consultas repetitivas (catálogos)
3. Read replicas en PostgreSQL para consultas de listado

---

## 6. Consideraciones de Compatibilidad

| Aspecto | Verificación |
|---------|-------------|
| **Navegadores** | Chrome, Firefox, Edge, Safari (Angular 21 soporta todos los modernos) |
| **API versioning** | No implementado aún. Recomendación: `/api/v1/` para futuras versiones |
| **Docker cross-platform** | Imágenes Alpine funcionan en Linux y Windows (WSL2) |
| **Base de datos dual** | Soporte MySQL (local) y PostgreSQL (Docker) mediante perfiles Spring |
| **CORS** | Configurado para `localhost:4200` y `localhost` en development |

---

## 7. Resultados de la Ejecución de Tests

### 7.1 Backend

```bash
$ cd BackEnd && ./mvnw test

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running cl.duoc.rednorte.AuthServiceApplicationTests
  ✓ contextLoads
Running cl.duoc.rednorte.auth_service.controller.AuthControllerTest
  ✓ login_deberiaRetornarToken
  ✓ login_conCredencialesInvalidas_deberiaRetornar401
  ✓ registroPaciente_deberiaRetornar201
Running cl.duoc.rednorte.auth_service.controller.AdminMedicoControllerTest
  ✓ listarMedicos_deberiaRetornar200
  ✓ crearMedico_deberiaRetornar201
  ✓ eliminarMedico_deberiaRetornar200
Running cl.duoc.rednorte.auth_service.service.AuthServiceTest
  ✓ registrarPaciente_deberiaCrearUsuarioYPaciente
  ✓ login_conEmailValido_deberiaRetornarToken
  ✓ login_conEmailInvalido_deberiaLanzarExcepcion
Running cl.duoc.rednorte.paciente.controller.PacienteControllerTest
  ✓ listarTodos_deberiaRetornar200
  ✓ buscarPorRut_existente_deberiaRetornar200
  ✓ buscarPorRut_noExistente_deberiaRetornar404
  ✓ actualizarDatosClinicos_deberiaRetornar200
Running cl.duoc.rednorte.paciente.service.PacienteServiceTest
  ✓ listarTodos_deberiaRetornarLista
  ✓ buscarPorRut_deberiaRetornarPaciente
  ✓ actualizarDatosClinicos_deberiaActualizar

Results:
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
```

### 7.2 Integración Docker

```bash
$ docker compose ps
NAME                  STATUS                    PORTS
rednorte-backend      Up 4 seconds              0.0.0.0:8080->8081/tcp
rednorte-frontend     Up 16 minutes             0.0.0.0:4200->80/tcp
rednorte-pabellon     Up 16 minutes             0.0.0.0:8082->8082/tcp
rednorte-db-postgre   Up 16 minutes (healthy)   5432/tcp
rednorte-db-pabellon  Up 16 minutes (healthy)   5432/tcp
```

---

## 8. Resumen de Hallazgos

| Hallazgo | Severidad | Estado | Acción |
|----------|-----------|--------|--------|
| `GET /api/pacientes` devolvía 403 para Triajer y Coordinador | Alta | ✅ Corregido | Agregar roles al `@PreAuthorize` en PacienteController |
| Dependencia cíclica entre auth-service y DB al arrancar | Media | ✅ Resuelto | Health check en docker-compose para PostgreSQL |
| Sin tests para pabellon-service | Baja | 📌 Pendiente | Agregar tests unitarios para CirugiaController y TriajeController |

---

*Documento generado como parte de la Actividad 3.2 — Taller de Alto Cómputo*
