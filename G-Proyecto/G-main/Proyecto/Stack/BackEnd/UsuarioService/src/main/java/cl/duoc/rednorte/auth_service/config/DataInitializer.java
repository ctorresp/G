package cl.duoc.rednorte.auth_service.config;

import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.auth_service.repository.RolRepository;
import cl.duoc.rednorte.usuarios.model.Coordinador;
import cl.duoc.rednorte.usuarios.model.Medico;
import cl.duoc.rednorte.usuarios.model.Triajer;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.CoordinadorRepository;
import cl.duoc.rednorte.usuarios.repository.MedicoRepository;
import cl.duoc.rednorte.usuarios.repository.TriajerRepository;
import cl.duoc.rednorte.usuarios.repository.UsuarioRepository;
import cl.duoc.rednorte.paciente.model.Paciente;
import cl.duoc.rednorte.paciente.repository.PacienteRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.rednorte.usuarios.model.Especialidad;
import cl.duoc.rednorte.usuarios.repository.EspecialidadRepository;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private RolRepository rolRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private TriajerRepository triajerRepository;
    @Autowired private CoordinadorRepository coordinadorRepository;
    @Autowired private EspecialidadRepository especialidadRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${SEED_PASSWORD_ADMIN:Admin1234!}") private String seedPasswordAdmin;
    @Value("${SEED_PASSWORD_MEDICO:Medico1234!}") private String seedPasswordMedico;
    @Value("${SEED_PASSWORD_TRIAJER:Triajer1234!}") private String seedPasswordTriajer;
    @Value("${SEED_PASSWORD_COORDINADOR:Coordi1234!}") private String seedPasswordCoordinador;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Iniciando Datos — auth-service");

        Rol rolAdmin = crearRolSiNoExiste("ROLE_ADMIN", "Administrador del sistema");
        Rol rolMedico = crearRolSiNoExiste("ROLE_MEDICO", "Médico o profesional de salud");
        Rol rolTriajer = crearRolSiNoExiste("ROLE_TRIAJER", "Triajer o enfermero de triaje");
        Rol rolCoordinador = crearRolSiNoExiste("ROLE_COORDINADOR", "Coordinador Quirúrgico");

        crearEspecialidades();
        crearAdminSiNoExiste(rolAdmin);
        crearMedicoSiNoExiste(rolMedico);
        crearTriajerSiNoExiste(rolTriajer);
        crearCoordinadorSiNoExiste(rolCoordinador);
        crearPacientes();

        log.info("Datos Iniciados exitosamente");
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

    private void crearAdminSiNoExiste(Rol rolAdmin) {
        String email = "admin@rednorte.cl";
        var usuarioExistente = usuarioRepository.findByEmail(email);
        if (usuarioExistente.isPresent()) {
            log.info("  [USUARIO YA EXISTE]  {}", email);
            return;
        }
        Usuario admin = new Usuario();
        admin.setRut("12345678-9");
        admin.setNombre("Administrador Sistema");
        admin.setEmail(email);
        admin.setContrasena(passwordEncoder.encode(seedPasswordAdmin));
        admin.setEstado(true);
        admin.setRol(rolAdmin);
        usuarioRepository.save(admin);
        log.info("  [USUARIO CREADO]  {} (contraseña cifrada)", email);
    }

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
        medico.setContrasena(passwordEncoder.encode(seedPasswordMedico));
        medico.setEstado(true);
        medico.setRol(rolMedico);
        Usuario guardado = usuarioRepository.save(medico);
        Especialidad espCirugia = especialidadRepository.findByNombre("Cirugía General")
                .orElseThrow(() -> new RuntimeException("Especialidad Cirugía General no encontrada"));
        Medico med = new Medico();
        med.setUsuario(guardado);
        med.setEspecialidad(espCirugia);
        medicoRepository.save(med);
        log.info("  [USUARIO + MEDICO CREADOS]  {} (contraseña cifrada)", email);
    }

    private void crearTriajerSiNoExiste(Rol rolTriajer) {
        String email = "triajer@rednorte.cl";
        if (usuarioRepository.existsByEmail(email)) {
            log.info("  [USUARIO YA EXISTE]  {}", email);
            return;
        }
        Usuario triajer = new Usuario();
        triajer.setRut("33333333-3");
        triajer.setNombre("Enfermero Carlos Muñoz");
        triajer.setEmail(email);
        triajer.setContrasena(passwordEncoder.encode(seedPasswordTriajer));
        triajer.setEstado(true);
        triajer.setRol(rolTriajer);
        Usuario guardado = usuarioRepository.save(triajer);
        Triajer t = new Triajer();
        t.setUsuario(guardado);
        triajerRepository.save(t);
        log.info("  [USUARIO + TRIAJER CREADOS]  {} (contraseña cifrada)", email);
    }

    private void crearCoordinadorSiNoExiste(Rol rolCoordinador) {
        String email = "coordinador@rednorte.cl";
        if (usuarioRepository.existsByEmail(email)) {
            log.info("  [USUARIO YA EXISTE]  {}", email);
            return;
        }
        Usuario coordinador = new Usuario();
        coordinador.setRut("11111111-1");
        coordinador.setNombre("Coordinador Quirúrgico");
        coordinador.setEmail(email);
        coordinador.setContrasena(passwordEncoder.encode(seedPasswordCoordinador));
        coordinador.setEstado(true);
        coordinador.setRol(rolCoordinador);
        Usuario guardado = usuarioRepository.save(coordinador);
        Coordinador c = new Coordinador();
        c.setUsuario(guardado);
        c.setAreaAsignada("Quirófanos");
        coordinadorRepository.save(c);
        log.info("  [USUARIO + COORDINADOR CREADOS]  {} (contraseña cifrada)", email);
    }

    private void crearPacientes() {
        if (pacienteRepository.findByRut("11111111-1").isPresent()) {
            log.info("  [PACIENTES YA EXISTEN]");
            return;
        }

        Paciente p1 = new Paciente();
        p1.setRut("11111111-1");
        p1.setNombre("María González");
        p1.setEmail("paciente@rednorte.cl");
        p1.setPrevision("FONASA");
        p1.setGrupoSanguineo("O+");
        p1.setPesoKg(BigDecimal.valueOf(65.5));
        p1.setAlturaCm(BigDecimal.valueOf(168.0));
        p1.setAlergias("penicilina");
        pacienteRepository.save(p1);

        Paciente p2 = new Paciente();
        p2.setRut("22222222-2");
        p2.setNombre("Pedro Soto");
        p2.setEmail("paciente2@rednorte.cl");
        p2.setPrevision("ISAPRE");
        p2.setGrupoSanguineo("A+");
        p2.setPesoKg(BigDecimal.valueOf(82.0));
        p2.setAlturaCm(BigDecimal.valueOf(175.5));
        p2.setEnfermedadesCronicas("hipertension");
        pacienteRepository.save(p2);

        log.info("  [PACIENTES CREADOS]  2 pacientes");
    }

    private void crearEspecialidades() {
        crearEspSiNoExiste("Cirugía General", "2 Anestesistas, 1 Instrumentista, 1 Ayudante");
        crearEspSiNoExiste("Traumatología", "1 Anestesista, 1 Instrumentista");
        crearEspSiNoExiste("Ginecología", "1 Anestesista, 1 Instrumentista, 1 Ayudante");
        crearEspSiNoExiste("Oftalmología", "1 Anestesista, 1 Instrumentista");
        crearEspSiNoExiste("Cardiología", "2 Anestesistas, 1 Instrumentista, 2 Ayudantes");
        crearEspSiNoExiste("Urología", "1 Anestesista, 1 Instrumentista, 1 Ayudante");
    }

    private void crearEspSiNoExiste(String nombre, String descripcion) {
        if (especialidadRepository.findByNombre(nombre).isPresent()) return;
        Especialidad e = new Especialidad();
        e.setNombre(nombre);
        e.setDescripcion(descripcion);
        especialidadRepository.save(e);
        log.info("  [ESPECIALIDAD CREADA]  {}", nombre);
    }
}
