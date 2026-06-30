package cl.duoc.rednorte.auth_service.service;

import cl.duoc.rednorte.auth_service.dto.ForgotPasswordRequestDTO;
import cl.duoc.rednorte.auth_service.dto.LoginRequestDTO;
import cl.duoc.rednorte.auth_service.dto.LoginResponseDTO;
import cl.duoc.rednorte.auth_service.dto.RegistroRequestDTO;
import cl.duoc.rednorte.auth_service.dto.ResetPasswordRequestDTO;
import cl.duoc.rednorte.auth_service.model.PasswordResetToken;
import cl.duoc.rednorte.auth_service.repository.PasswordResetTokenRepository;
import cl.duoc.rednorte.auth_service.security.JwtTokenProvider;
import cl.duoc.rednorte.paciente.model.Paciente;
import cl.duoc.rednorte.paciente.repository.PacienteRepository;
import cl.duoc.rednorte.usuarios.dto.UsuarioRequestDTO;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.UsuarioRepository;
import cl.duoc.rednorte.usuarios.service.MedicoService;
import cl.duoc.rednorte.usuarios.service.UsuarioService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.reset-password.base-url:http://localhost:4200}")
    private String baseUrl;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public LoginResponseDTO login(LoginRequestDTO request) {
        String emailResuelto = resolverEmail(request);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailResuelto, request.getContrasena())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generarToken(authentication);

        Usuario usuario = usuarioService.buscarPorEmailActivo(emailResuelto)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setTipoToken("Bearer");
        response.setIdUsuario(usuario.getIdUsuario());
        response.setRut(usuario.getRut());
        response.setNombre(usuario.getNombre());
        response.setEmail(usuario.getEmail());
        response.setRol(usuario.getRol().getNombreRol());
        response.setExpiracion(jwtTokenProvider.getExpiracionMs());

        if (usuario.getRol().getNombreRol().equals("ROLE_MEDICO")) {
            medicoService.buscarPorIdUsuario(usuario.getIdUsuario()).ifPresent(medico -> {
                response.setEspecialidadId(medico.getEspecialidad().getIdEspecialidad());
                response.setEspecialidadNombre(medico.getEspecialidad().getNombre());
            });
        }

        return response;
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
            Usuario medico = usuarioService.buscarPorId(request.getIdMedico())
                    .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + request.getIdMedico()));
            paciente.setMedicoAsignado(medico);
        }

        pacienteRepository.save(paciente);

        return "Paciente registrado exitosamente con RUT: " + request.getRut();
    }

    public String registrarUsuarioAdmin(RegistroRequestDTO request, String nombreRol) {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setRut(request.getRut());
        dto.setNombre(request.getNombre());
        dto.setEmail(request.getEmail());
        dto.setContrasena(request.getContrasena());
        Usuario guardado = usuarioService.crear(dto, nombreRol);
        return "Usuario " + nombreRol + " registrado con ID: " + guardado.getIdUsuario();
    }

    public String registrarMedicoCompleto(RegistroRequestDTO request, Long especialidadId) {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setRut(request.getRut());
        dto.setNombre(request.getNombre());
        dto.setEmail(request.getEmail());
        dto.setContrasena(request.getContrasena());
        Usuario guardado = usuarioService.crear(dto, "ROLE_MEDICO");
        medicoService.crearConUsuario(guardado, especialidadId);
        return "Médico registrado exitosamente con ID: " + guardado.getIdUsuario();
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO validarToken(String token) {
        if (!jwtTokenProvider.validarToken(token)) {
            throw new RuntimeException("Token JWT inválido o expirado");
        }

        String email = jwtTokenProvider.getEmailDesdeToken(token);
        Usuario usuario = usuarioService.buscarPorEmailActivo(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o inactivo"));

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setTipoToken("Bearer");
        response.setIdUsuario(usuario.getIdUsuario());
        response.setRut(usuario.getRut());
        response.setNombre(usuario.getNombre());
        response.setEmail(usuario.getEmail());
        response.setRol(usuario.getRol().getNombreRol());
        response.setExpiracion(jwtTokenProvider.getExpiracionMs());

        if (usuario.getRol().getNombreRol().equals("ROLE_MEDICO")) {
            medicoService.buscarPorIdUsuario(usuario.getIdUsuario()).ifPresent(medico -> {
                response.setEspecialidadId(medico.getEspecialidad().getIdEspecialidad());
                response.setEspecialidadNombre(medico.getEspecialidad().getNombre());
            });
        }

        return response;
    }

    public Map<String, String> forgotPassword(ForgotPasswordRequestDTO request) {
        String email = request.getEmail();
        if (email == null || email.isBlank()) {
            throw new RuntimeException("El correo electrónico es requerido");
        }

        var usuarioOpt = usuarioService.buscarPorEmailActivo(email);
        if (usuarioOpt.isPresent()) {
            passwordResetTokenRepository.deleteByEmail(email);

            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusHours(1);
            PasswordResetToken resetToken = new PasswordResetToken(email, token, expiry);
            passwordResetTokenRepository.save(resetToken);

            String resetLink = baseUrl + "/reset-password/" + token;
            log.info("========================================");
            log.info("SOLICITUD DE RESTABLECIMIENTO DE CONTRASEÑA");
            log.info("Email: {}", email);
            log.info("Token: {}", token);
            log.info("Link: {}", resetLink);
            log.info("Válido hasta: {}", expiry);
            log.info("========================================");

            return Map.of("mensaje", "Redirigiendo...", "token", token);
        }

        return Map.of("mensaje", "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña.");
    }

    public Map<String, String> resetPassword(ResetPasswordRequestDTO request) {
        String tokenStr = request.getToken();
        String newPassword = request.getNewPassword();

        if (tokenStr == null || tokenStr.isBlank()) {
            throw new RuntimeException("El token es requerido");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new RuntimeException("Token inválido o no encontrado"));

        if (resetToken.isUsed()) {
            throw new RuntimeException("Este token ya ha sido utilizado");
        }
        if (resetToken.isExpired()) {
            throw new RuntimeException("El token ha expirado. Solicita un nuevo restablecimiento.");
        }

        Usuario usuario = usuarioService.buscarPorEmailActivo(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setContrasena(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        log.info("Contraseña restablecida exitosamente para: {}", resetToken.getEmail());

        return Map.of("mensaje", "Contraseña restablecida exitosamente.");
    }

    private String resolverEmail(LoginRequestDTO request) {
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            return request.getEmail();
        }
        if (request.getRut() != null && !request.getRut().isBlank()) {
            return usuarioService.buscarPorRut(request.getRut())
                    .map(Usuario::getEmail)
                    .orElseThrow(() -> new RuntimeException(
                            "No existe usuario con RUT: " + request.getRut()));
        }
        throw new RuntimeException("Debe proporcionar email o RUT para iniciar sesión");
    }
}
