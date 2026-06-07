# Evaluación 3 — Paso 3: Documentación de Endpoints

## Autenticación — `/api/auth`

### POST /api/auth/login
Autentica un usuario y devuelve un JWT.

**Request Body:**
```json
{
  "email": "admin@rednorte.cl",
  "rut": null,
  "contrasena": "Admin1234!"
}
```
*(Usar `email` o `rut`, no ambos)*

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipoToken": "Bearer",
  "idUsuario": 1,
  "nombre": "Administrador Sistema",
  "email": "admin@rednorte.cl",
  "roles": ["ROLE_ADMIN"],
  "expiracion": 86400000
}
```

**Response 401:** { "error": "Credenciales inválidas" }

---

### POST /api/auth/registro
Registra un nuevo paciente (requiere ROLE_ADMIN).

**Request Body:**
```json
{
  "rut": "12345678-9",
  "nombre": "Juan Pérez",
  "email": "juan@correo.cl",
  "contrasena": "Paciente1234!",
  "prevision": "FONASA",
  "datosClinicosSensibles": {
    "grupoSanguineo": "O+",
    "pesoKg": 70.5,
    "alturaCm": 175,
    "alergias": ["Penicilina", "Polen"],
    "enfermedadesCronicas": ["Asma"]
  }
}
```

**Response 201:** { "mensaje": "Paciente registrado exitosamente" }

---

### POST /api/auth/admin/registro?rol=ROLE_MEDICO
Registra un médico o admin (requiere ROLE_ADMIN).

**Request Body:**
```json
{
  "rut": "87654321-0",
  "nombre": "Dr. María López",
  "email": "maria@clinica.cl",
  "contrasena": "Medico1234!"
}
```

**Response 201:** { "mensaje": "Usuario registrado exitosamente" }

---

### POST /api/auth/validar
Valida un token JWT (uso interno entre microservicios).

**Headers:** `Authorization: Bearer <token>`

**Response 200:** { "valido": true, "email": "admin@rednorte.cl", "roles": ["ROLE_ADMIN"] }

---

## Pacientes — `/api/pacientes`

### GET /api/pacientes
Lista todos los pacientes (requiere ROLE_ADMIN o ROLE_MEDICO).

**Response 200:**
```json
[
  {
    "idPaciente": 1,
    "rut": "12345678-9",
    "nombre": "Juan Pérez",
    "email": "juan@correo.cl",
    "prevision": "FONASA",
    "estado": true,
    "datosClinicosSensibles": {
      "grupoSanguineo": "O+",
      "pesoKg": 70.5,
      "alturaCm": 175,
      "alergias": ["Penicilina"],
      "enfermedadesCronicas": ["Asma"]
    }
  }
]
```

---

### GET /api/pacientes/rut/{rut}
Busca paciente por RUT.

**Response 200:** Objeto `PacienteResponseDTO`
**Response 404:** { "error": "Paciente no encontrado con rut: ..." }

---

### PUT /api/pacientes/rut/{rut}/clinicos
Actualiza datos clínicos del paciente.

**Request Body:**
```json
{
  "prevision": "ISAPRE",
  "email": "nuevo@correo.cl",
  "datosClinicosSensibles": {
    "grupoSanguineo": "A+",
    "pesoKg": 72.0,
    "alturaCm": 176,
    "alergias": ["Penicilina"],
    "enfermedadesCronicas": ["Asma", "Diabetes"]
  }
}
```

**Response 200:** Objeto `PacienteResponseDTO` actualizado

---

### PATCH /api/pacientes/rut/{rut}/desactivar
Desactiva un paciente (soft-delete, solo Admin).

**Response 200:** { "mensaje": "Paciente desactivado exitosamente" }

---

### PATCH /api/pacientes/rut/{rut}/activar
Reactiva un paciente (solo Admin).

**Response 200:** { "mensaje": "Paciente activado exitosamente" }

---

## Admin Médicos — `/api/admin/medicos`

### GET /api/admin/medicos
Lista todos los médicos (solo Admin).

**Response 200:**
```json
[
  {
    "idUsuario": 2,
    "rut": "87654321-0",
    "nombre": "Dr. María López",
    "email": "maria@clinica.cl",
    "estado": true
  }
]
```

---

### PUT /api/admin/medicos/{id}
Actualiza datos de un médico (solo Admin).

**Request Body:**
```json
{
  "rut": "87654321-0",
  "nombre": "Dr. María López",
  "email": "maria@nuevaclinica.cl"
}
```

**Response 200:** Objeto `Usuario` actualizado
**Response 404:** { "error": "Médico no encontrado" }
