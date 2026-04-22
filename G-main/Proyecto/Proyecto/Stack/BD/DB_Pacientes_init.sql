-- ══════════════════════════════════════════════════════════
--   DB_Pacientes — Script de inicialización de estructura
--   Microservicio: auth-service (Patient Microservice)
--   Tablas: roles, usuarios, usuario_roles, pacientes
--
--   ⚠ IMPORTANTE: Los datos semilla (usuarios y contraseñas)
--   son gestionados por DataInitializer.java al arrancar Spring Boot.
--   Las contraseñas SIEMPRE se cifran con BCrypt mediante
--   PasswordEncoder — NUNCA se insertan en texto plano.
-- ══════════════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS DB_Pacientes
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE DB_Pacientes;

-- ── Tabla: roles ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS roles (
    id_rol      BIGINT        NOT NULL AUTO_INCREMENT,
    nombre_rol  VARCHAR(50)   NOT NULL UNIQUE COMMENT 'Ej: ROLE_ADMIN, ROLE_MEDICO, ROLE_PACIENTE',
    descripcion VARCHAR(255)  COMMENT 'Descripción del rol en el sistema',
    PRIMARY KEY (id_rol)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='Roles del sistema — RBAC';

-- ── Tabla: usuarios ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario  BIGINT        NOT NULL AUTO_INCREMENT,
    rut         VARCHAR(12)   NOT NULL UNIQUE  COMMENT 'RUT chileno con dígito verificador',
    nombre      VARCHAR(100)  NOT NULL         COMMENT 'Nombre completo del usuario',
    email       VARCHAR(150)  NOT NULL UNIQUE  COMMENT 'Correo electrónico (usado para login JWT)',
    contrasena  VARCHAR(255)  NOT NULL         COMMENT 'Contraseña cifrada con BCrypt — NUNCA texto plano',
    estado      TINYINT(1)    NOT NULL DEFAULT 1 COMMENT '1=activo, 0=bloqueado/desactivado',
    PRIMARY KEY (id_usuario)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='Usuarios del sistema con credenciales de acceso JWT/RBAC';

-- ── Tabla: usuario_roles (relación M:N) ───────────────────
CREATE TABLE IF NOT EXISTS usuario_roles (
    id_usuario  BIGINT NOT NULL,
    id_rol      BIGINT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ur_rol FOREIGN KEY (id_rol)
        REFERENCES roles (id_rol) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='Tabla de unión usuarios ↔ roles (RBAC)';

-- ── Tabla: pacientes ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS pacientes (
    id_paciente              BIGINT       NOT NULL AUTO_INCREMENT,
    id_usuario               BIGINT       NOT NULL UNIQUE COMMENT 'Relación 1:1 con la tabla usuarios',
    prevision                VARCHAR(100) COMMENT 'Tipo de previsión: FONASA, ISAPRE, Particular, etc.',
    datos_clinicos_sensibles TEXT         COMMENT 'Datos clínicos sensibles en formato JSON u otro',
    PRIMARY KEY (id_paciente),
    CONSTRAINT fk_pac_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='Ficha única digital del paciente — datos sensibles';

-- ══════════════════════════════════════════════════════════
--   Usuarios semilla gestionados por DataInitializer.java
-- ══════════════════════════════════════════════════════════
--
--   Al iniciar el microservicio, Spring Boot ejecuta DataInitializer.java
--   que crea automáticamente los siguientes usuarios con BCrypt:
--
--   ┌────────────────────────┬──────────────┬───────────────┐
--   │ Email                  │ Contraseña   │ Rol           │
--   ├────────────────────────┼──────────────┼───────────────┤
--   │ admin@rednorte.cl      │ Admin1234!   │ ROLE_ADMIN    │
--   │ medico@rednorte.cl     │ Medico1234!  │ ROLE_MEDICO   │
--   │ paciente@rednorte.cl   │ Paciente1234!│ ROLE_PACIENTE │
--   └────────────────────────┴──────────────┴───────────────┘
--
--   ⚠ Cambia estas contraseñas antes de pasar a producción.
-- ══════════════════════════════════════════════════════════
