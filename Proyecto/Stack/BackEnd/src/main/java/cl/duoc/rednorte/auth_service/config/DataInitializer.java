package cl.duoc.rednorte.auth_service.config;

import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.auth_service.model.Usuario;
import cl.duoc.rednorte.auth_service.repository.RolRepository;
import cl.duoc.rednorte.auth_service.repository.UsuarioRepository;
import cl.duoc.rednorte.datos_clinicos.model.DatosClinicos;
import cl.duoc.rednorte.paciente.model.Paciente;
import cl.duoc.rednorte.paciente.repository.PacienteRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Inicializador de datos: Crea roles y usuarios semilla al arrancar la aplicación
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
        log.info("Iniciando DataInitializer — auth-service");

        Rol rolAdmin = crearRolSiNoExiste("ROLE_ADMIN", "Administrador del sistema");
        Rol rolMedico = crearRolSiNoExiste("ROLE_MEDICO", "Médico o profesional de salud");
        Rol rolPaciente = crearRolSiNoExiste("ROLE_PACIENTE", "Paciente del sistema de toma de horas");

        crearAdminSiNoExiste(rolAdmin);
        crearMedicoSiNoExiste(rolMedico);
        crearPacienteSiNoExiste(rolPaciente);
        crearPaciente2SiNoExiste(rolPaciente);

        log.info("DataInitializer completado exitosamente");
    }

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

    // Crea el usuario administrador por defecto
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
        admin.setContrasena(passwordEncoder.encode("Admin1234!"));
        admin.setEstado(true);

        Set<Rol> roles = new HashSet<>();
        roles.add(rolAdmin);
        admin.setRoles(roles);

        usuarioRepository.save(admin);
        log.info("  [USUARIO CREADO]  {} (contraseña cifrada con BCrypt)", email);
    }

    // Crea un usuario médico de prueba
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
        medico.setContrasena(passwordEncoder.encode("Medico1234!"));
        medico.setEstado(true);

        Set<Rol> roles = new HashSet<>();
        roles.add(rolMedico);
        medico.setRoles(roles);

        usuarioRepository.save(medico);
        log.info("  [USUARIO CREADO]  {} (contraseña cifrada con BCrypt)", email);
    }

    // Crea un paciente de prueba con su ficha clínica
    private void crearPacienteSiNoExiste(Rol rolPaciente) {
        String email = "paciente@rednorte.cl";
        if (usuarioRepository.existsByEmail(email)) {
            log.info("  [USUARIO YA EXISTE]  {}", email);
            return;
        }

        Usuario pacienteUsuario = new Usuario();
        pacienteUsuario.setRut("11111111-1");
        pacienteUsuario.setNombre("María González");
        pacienteUsuario.setEmail(email);
        pacienteUsuario.setContrasena(passwordEncoder.encode("Paciente1234!"));
        pacienteUsuario.setEstado(true);

        Set<Rol> roles = new HashSet<>();
        roles.add(rolPaciente);
        pacienteUsuario.setRoles(roles);

        Usuario usuarioGuardado = usuarioRepository.save(pacienteUsuario);

        Paciente ficha = new Paciente();
        ficha.setUsuario(usuarioGuardado);
        ficha.setPrevision("FONASA");

        DatosClinicos datosClinicos = new DatosClinicos();
        datosClinicos.setGrupoSanguineo("O+");
        datosClinicos.setAlergias(List.of("penicilina"));
        datosClinicos.setEnfermedadesCronicas(new ArrayList<>());
        datosClinicos.setPesoKg(65.5);
        datosClinicos.setAlturaCm(168.0);
        ficha.setDatosClinicosSensibles(datosClinicos);

        pacienteRepository.save(ficha);

        log.info("  [USUARIO + FICHA CREADOS]  {} (contraseña cifrada con BCrypt)", email);
    }

    // Crea un segundo paciente de prueba
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

        DatosClinicos datosClinicos = new DatosClinicos();
        datosClinicos.setGrupoSanguineo("A+");
        datosClinicos.setAlergias(new ArrayList<>());
        datosClinicos.setEnfermedadesCronicas(List.of("hipertension"));
        datosClinicos.setPesoKg(82.0);
        datosClinicos.setAlturaCm(175.5);
        ficha.setDatosClinicosSensibles(datosClinicos);

        pacienteRepository.save(ficha);

        log.info("  [USUARIO + FICHA CREADOS]  {} (contraseña cifrada con BCrypt)", email);
    }
}
