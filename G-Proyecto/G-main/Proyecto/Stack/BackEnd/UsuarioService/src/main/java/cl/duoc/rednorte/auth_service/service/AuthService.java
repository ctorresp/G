package cl.duoc.rednorte.auth_service.service;

import cl.duoc.rednorte.auth_service.dto.LoginRequestDTO;
import cl.duoc.rednorte.auth_service.dto.LoginResponseDTO;
import cl.duoc.rednorte.auth_service.dto.RegistroRequestDTO;
import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.auth_service.repository.RolRepository;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.UsuarioRepository;
import cl.duoc.rednorte.auth_service.security.JwtTokenProvider;
import cl.duoc.rednorte.paciente.model.Paciente;
import cl.duoc.rednorte.paciente.repository.PacienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public LoginResponseDTO login(LoginRequestDTO request) {
        String emailResuelto = resolverEmail(request);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailResuelto, request.getContrasena())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generarToken(authentication);

        Usuario usuario = usuarioRepository.findByEmailAndEstadoTrue(emailResuelto)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getRut(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getNombreRol(),
                jwtTokenProvider.getExpiracionMs()
        );
    }

    public String registrarPaciente(RegistroRequestDTO request) {
        if (pacienteRepository.findByRut(request.getRut()).isPresent()) {
            throw new RuntimeException("El RUT ya está registrado como paciente: " + request.getRut());
        }

        Paciente paciente = new Paciente();
        paciente.setRut(request.getRut());
        paciente.setNombre(request.getNombre());
        paciente.setEmail(request.getEmail());
        paciente.setPrevision(request.getPrevision());
        paciente.setGrupoSanguineo(request.getGrupoSanguineo());
        if (request.getPesoKg() != null) paciente.setPesoKg(BigDecimal.valueOf(request.getPesoKg()));
        if (request.getAlturaCm() != null) paciente.setAlturaCm(BigDecimal.valueOf(request.getAlturaCm()));
        paciente.setAlergias(request.getAlergias());
        paciente.setEnfermedadesCronicas(request.getEnfermedadesCronicas());
        paciente.setContactoEmergenciaNombre(request.getContactoEmergenciaNombre());
        paciente.setContactoEmergenciaTelefono(request.getContactoEmergenciaTelefono());

        if (request.getIdMedico() != null) {
            Usuario medico = usuarioRepository.findById(request.getIdMedico())
                    .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + request.getIdMedico()));
            paciente.setMedicoAsignado(medico);
        }

        pacienteRepository.save(paciente);

        return "Paciente registrado exitosamente con RUT: " + request.getRut();
    }

    public String registrarUsuarioAdmin(RegistroRequestDTO request, String nombreRol) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + request.getEmail());
        }
        if (usuarioRepository.existsByRut(request.getRut())) {
            throw new RuntimeException("El RUT ya está registrado: " + request.getRut());
        }

        Usuario usuario = new Usuario();
        usuario.setRut(request.getRut());
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        usuario.setEstado(true);

        Rol rol = rolRepository.findByNombreRol(nombreRol)
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombreRol(nombreRol);
                    nuevoRol.setDescripcion("Rol administrativo: " + nombreRol);
                    return rolRepository.save(nuevoRol);
                });

        usuario.setRol(rol);

        Usuario guardado = usuarioRepository.save(usuario);
        return "Usuario " + nombreRol + " registrado con ID: " + guardado.getIdUsuario();
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO validarToken(String token) {
        if (!jwtTokenProvider.validarToken(token)) {
            throw new RuntimeException("Token JWT inválido o expirado");
        }

        String email = jwtTokenProvider.getEmailDesdeToken(token);
        Usuario usuario = usuarioRepository.findByEmailAndEstadoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o inactivo"));

        return new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getRut(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getNombreRol(),
                jwtTokenProvider.getExpiracionMs()
        );
    }

    private String resolverEmail(LoginRequestDTO request) {
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            return request.getEmail();
        }
        if (request.getRut() != null && !request.getRut().isBlank()) {
            return usuarioRepository.findByRut(request.getRut())
                    .map(Usuario::getEmail)
                    .orElseThrow(() -> new RuntimeException(
                            "No existe usuario con RUT: " + request.getRut()));
        }
        throw new RuntimeException("Debe proporcionar email o RUT para iniciar sesión");
    }
}
