package cl.duoc.rednorte.auth_service.service;

import cl.duoc.rednorte.auth_service.dto.LoginRequestDTO;
import cl.duoc.rednorte.auth_service.dto.LoginResponseDTO;
import cl.duoc.rednorte.auth_service.dto.RegistroRequestDTO;
import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.auth_service.model.Usuario;
import cl.duoc.rednorte.auth_service.repository.RolRepository;
import cl.duoc.rednorte.auth_service.repository.UsuarioRepository;
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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    // Autentica al usuario (por email o RUT) y genera su token JWT
    public LoginResponseDTO login(LoginRequestDTO request) {
        String emailResuelto = resolverEmail(request);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailResuelto, request.getContrasena())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generarToken(authentication);

        Set<String> roles = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
        Usuario usuario = usuarioRepository.findByEmailAndEstadoTrue(emailResuelto)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                roles,
                jwtTokenProvider.getExpiracionMs()
        );
    }

    // Registra un nuevo usuario y crea su ficha médica asociada
    public String registrarPaciente(RegistroRequestDTO request) {
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

        Rol rolPaciente = rolRepository.findByNombreRol("ROLE_PACIENTE")
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombreRol("ROLE_PACIENTE");
                    nuevoRol.setDescripcion("Paciente del sistema de toma de horas");
                    return rolRepository.save(nuevoRol);
                });

        Set<Rol> roles = new HashSet<>();
        roles.add(rolPaciente);
        usuario.setRoles(roles);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Paciente paciente = new Paciente();
        paciente.setUsuario(usuarioGuardado);
        paciente.setPrevision(request.getPrevision());
        
        if (request.getDatosClinicosSensibles() != null) {
            paciente.setDatosClinicosSensibles(request.getDatosClinicosSensibles());
        }
        
        pacienteRepository.save(paciente);

        return "Paciente registrado exitosamente con ID: " + usuarioGuardado.getIdUsuario();
    }

    // Registra personal administrativo (ROLE_ADMIN o ROLE_MEDICO)
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

        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);

        Usuario guardado = usuarioRepository.save(usuario);
        return "Usuario " + nombreRol + " registrado con ID: " + guardado.getIdUsuario();
    }

    // Verifica la validez del token (útil para comunicación interna entre microservicios)
    @Transactional(readOnly = true)
    public LoginResponseDTO validarToken(String token) {
        if (!jwtTokenProvider.validarToken(token)) {
            throw new RuntimeException("Token JWT inválido o expirado");
        }

        String email = jwtTokenProvider.getEmailDesdeToken(token);
        Usuario usuario = usuarioRepository.findByEmailAndEstadoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o inactivo"));

        Set<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombreRol)
                .collect(Collectors.toSet());

        return new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                roles,
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
