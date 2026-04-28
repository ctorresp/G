package cl.duoc.rednorte.auth_service.config;

import cl.duoc.rednorte.auth_service.model.Paciente;
import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.auth_service.model.Usuario;
import cl.duoc.rednorte.auth_service.repository.PacienteRepository;
import cl.duoc.rednorte.auth_service.repository.RolRepository;
import cl.duoc.rednorte.auth_service.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Inicializador de datos de la aplicación.
 * Se ejecuta una sola vez al arrancar el microservicio.
 *
 * Garantiza que TODAS las contraseñas de los usuarios semilla
 * sean cifradas con BCrypt mediante el PasswordEncoder de Spring Security.
 * Nunca se almacena texto plano en la base de datos.
 *
 * Usuarios creados si no existen:
 * - admin@rednorte.cl / Admin1234! → ROLE_ADMIN
 * - medico@rednorte.cl / Medico1234! → ROLE_MEDICO
 * - paciente@rednorte.cl / Paciente1234! → ROLE_PACIENTE (con ficha)
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("═══════════════════════════════════════════");
        log.info("  Iniciando DataInitializer — auth-service ");
        log.info("═══════════════════════════════════════════");

        // ── 1. Crear roles si no existen ──────────────────
        Rol rolAdmin = crearRolSiNoExiste("ROLE_ADMIN", "Administrador del sistema");
        Rol rolMedico = crearRolSiNoExiste("ROLE_MEDICO", "Médico o profesional de salud");
        Rol rolPaciente = crearRolSiNoExiste("ROLE_PACIENTE", "Paciente del sistema de toma de horas");

        // ── 2. Crear usuarios semilla con contraseña cifrada ──
        crearAdminSiNoExiste(rolAdmin);
        crearMedicoSiNoExiste(rolMedico);
        crearPacienteSiNoExiste(rolPaciente);
        crearPaciente2SiNoExiste(rolPaciente);

        log.info("═══════════════════════════════════════════");
        log.info("  DataInitializer completado exitosamente  ");
        log.info("═══════════════════════════════════════════");
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS PRIVADOS
    // ─────────────────────────────────────────────────────────

    /**
     * Crea un rol si aún no existe en la base de datos.
     *
     * @param nombre      nombre del rol (ej. "ROLE_ADMIN")
     * @param descripcion descripción del rol
     * @return entidad Rol persistida o ya existente
     */
    private Rol crearRolSiNoExiste(String nombre, String descripcion) {
        return rolRepository.findByNombreRol(nombre).orElseGet(() -> {
            Rol rol = new Rol();
            rol.setNombreRol(nombre);
            rol.setDescripcion(descripcion);
            Rol guardado = rolRepository.save(rol);
            log.info("  [ROL CREADO]  {}", nombre);
            return guardado;
        });
    }

    /**
     * Crea el usuario administrador por defecto si no existe.
     * La contraseña "Admin1234!" se cifra con BCrypt al momento de la creación.
     *
     * @param rolAdmin entidad Rol del administrador
     */
    private void crearAdminSiNoExiste(Rol rolAdmin) {
        String email = "admin@rednorte.cl";
        if (usuarioRepository.existsByEmail(email)) {
            log.info("  [USUARIO YA EXISTE]  {}", email);
            return;
        }

        Usuario admin = new Usuario();
        admin.setRut("12345678-9");
        admin.setNombre("Administrador Sistema");
        admin.setEmail(email);
        // ✔ Contraseña cifrada con BCrypt — nunca se guarda texto plano
        admin.setContrasena(passwordEncoder.encode("Admin1234!"));
        admin.setEstado(true);

        Set<Rol> roles = new HashSet<>();
        roles.add(rolAdmin);
        admin.setRoles(roles);

        usuarioRepository.save(admin);
        log.info("  [USUARIO CREADO]  {} (contraseña cifrada con BCrypt)", email);
    }

    /**
     * Crea un usuario médico de prueba si no existe.
     * La contraseña "Medico1234!" se cifra con BCrypt al momento de la creación.
     *
     * @param rolMedico entidad Rol del médico
     */
    private void crearMedicoSiNoExiste(Rol rolMedico) {
        String email = "medico@rednorte.cl";
        if (usuarioRepository.existsByEmail(email)) {
            log.info("  [USUARIO YA EXISTE]  {}", email);
            return;
        }

        Usuario medico = new Usuario();
        medico.setRut("98765432-1");
        medico.setNombre("Dr. Juan Pérez");
        medico.setEmail(email);
        // ✔ Contraseña cifrada con BCrypt — nunca se guarda texto plano
        medico.setContrasena(passwordEncoder.encode("Medico1234!"));
        medico.setEstado(true);

        Set<Rol> roles = new HashSet<>();
        roles.add(rolMedico);
        medico.setRoles(roles);

        usuarioRepository.save(medico);
        log.info("  [USUARIO CREADO]  {} (contraseña cifrada con BCrypt)", email);
    }

    /**
     * Crea un usuario paciente de prueba con su ficha clínica si no existe.
     * La contraseña "Paciente1234!" se cifra con BCrypt al momento de la creación.
     *
     * @param rolPaciente entidad Rol del paciente
     */
    private void crearPacienteSiNoExiste(Rol rolPaciente) {
        String email = "paciente@rednorte.cl";
        if (usuarioRepository.existsByEmail(email)) {
            log.info("  [USUARIO YA EXISTE]  {}", email);
            return;
        }

        // Crear usuario
        Usuario pacienteUsuario = new Usuario();
        pacienteUsuario.setRut("11111111-1");
        pacienteUsuario.setNombre("María González");
        pacienteUsuario.setEmail(email);
        // ✔ Contraseña cifrada con BCrypt — nunca se guarda texto plano
        pacienteUsuario.setContrasena(passwordEncoder.encode("Paciente1234!"));
        pacienteUsuario.setEstado(true);

        Set<Rol> roles = new HashSet<>();
        roles.add(rolPaciente);
        pacienteUsuario.setRoles(roles);

        Usuario usuarioGuardado = usuarioRepository.save(pacienteUsuario);

        // Crear ficha de paciente asociada
        Paciente ficha = new Paciente();
        ficha.setUsuario(usuarioGuardado);
        ficha.setPrevision("FONASA");
        ficha.setDatosClinicosSensibles(
                "{\"alergias\": [\"penicilina\"], \"grupo_sanguineo\": \"O+\", \"enfermedades_cronicas\": []}");
        pacienteRepository.save(ficha);

        log.info("  [USUARIO + FICHA CREADOS]  {} (contraseña cifrada con BCrypt)", email);
    }

    /**
     * Crea un segundo paciente de prueba con su ficha clínica si no existe.
     */
    private void crearPaciente2SiNoExiste(Rol rolPaciente) {
        String email = "paciente2@rednorte.cl";
        if (usuarioRepository.existsByEmail(email)) {
            log.info("  [USUARIO YA EXISTE]  {}", email);
            return;
        }

        Usuario pacienteUsuario = new Usuario();
        pacienteUsuario.setRut("22222222-2");
        pacienteUsuario.setNombre("Pedro Soto");
        pacienteUsuario.setEmail(email);
        pacienteUsuario.setContrasena(passwordEncoder.encode("Paciente1234!"));
        pacienteUsuario.setEstado(true);

        Set<Rol> roles = new HashSet<>();
        roles.add(rolPaciente);
        pacienteUsuario.setRoles(roles);

        Usuario usuarioGuardado = usuarioRepository.save(pacienteUsuario);

        Paciente ficha = new Paciente();
        ficha.setUsuario(usuarioGuardado);
        ficha.setPrevision("ISAPRE");
        ficha.setDatosClinicosSensibles(
                "{\"alergias\": [], \"grupo_sanguineo\": \"A+\", \"enfermedades_cronicas\": [\"hipertension\"]}");
        pacienteRepository.save(ficha);

        log.info("  [USUARIO + FICHA CREADOS]  {} (contraseña cifrada con BCrypt)", email);
    }
}
