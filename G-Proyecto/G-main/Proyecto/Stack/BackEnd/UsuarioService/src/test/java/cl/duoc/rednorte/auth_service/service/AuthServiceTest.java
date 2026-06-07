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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private PacienteRepository pacienteRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private Authentication authentication;

    @InjectMocks private AuthService authService;

    private Usuario usuario;
    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombreRol("ROLE_ADMIN");
        rol.setDescripcion("Administrador del sistema");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Admin Test");
        usuario.setEmail("admin@test.cl");
        usuario.setContrasena("encoded");
        usuario.setEstado(true);
        usuario.setRol(rol);
    }

    @Test
    void login_conEmailValido_deberiaRetornarToken() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("admin@test.cl");
        request.setContrasena("pass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn(java.util.List.of());
        when(jwtTokenProvider.generarToken(authentication)).thenReturn("token123");
        when(jwtTokenProvider.getExpiracionMs()).thenReturn(86400000L);
        when(usuarioRepository.findByEmailAndEstadoTrue("admin@test.cl"))
                .thenReturn(Optional.of(usuario));

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("token123", response.getToken());
        assertEquals("Bearer", response.getTipoToken());
        assertEquals("admin@test.cl", response.getEmail());
    }

    @Test
    void login_conRutValido_deberiaResolverEmailYRetornarToken() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setRut("11111111-1");
        request.setContrasena("pass");

        when(usuarioRepository.findByRut("11111111-1")).thenReturn(Optional.of(usuario));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn(java.util.List.of());
        when(jwtTokenProvider.generarToken(authentication)).thenReturn("token456");
        when(jwtTokenProvider.getExpiracionMs()).thenReturn(86400000L);
        when(usuarioRepository.findByEmailAndEstadoTrue("admin@test.cl"))
                .thenReturn(Optional.of(usuario));

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("token456", response.getToken());
    }

    @Test
    void login_sinEmailNiRut_deberiaLanzarExcepcion() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setContrasena("pass");

        Exception ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertTrue(ex.getMessage().contains("Debe proporcionar email o RUT"));
    }

    @Test
    void registrarPaciente_conDatosValidos_deberiaCrearPaciente() {
        RegistroRequestDTO request = new RegistroRequestDTO();
        request.setRut("22222222-2");
        request.setNombre("Paciente Test");
        request.setEmail("paciente@test.cl");
        request.setPrevision("FONASA");

        when(pacienteRepository.findByRut("22222222-2")).thenReturn(Optional.empty());
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(i -> i.getArgument(0));

        String resultado = authService.registrarPaciente(request);

        assertTrue(resultado.contains("Paciente registrado exitosamente"));
        verify(pacienteRepository, times(1)).save(any(Paciente.class));
    }

    @Test
    void registrarPaciente_conRutDuplicado_deberiaLanzarExcepcion() {
        RegistroRequestDTO request = new RegistroRequestDTO();
        request.setRut("33333333-3");
        request.setNombre("Test");
        request.setEmail("existente@test.cl");

        when(pacienteRepository.findByRut("33333333-3")).thenReturn(Optional.of(new Paciente()));

        Exception ex = assertThrows(RuntimeException.class, () -> authService.registrarPaciente(request));
        assertTrue(ex.getMessage().contains("RUT ya está registrado como paciente"));
    }

    @Test
    void registrarUsuarioAdmin_deberiaCrearUsuarioConRol() {
        RegistroRequestDTO request = new RegistroRequestDTO();
        request.setRut("44444444-4");
        request.setNombre("Dr. Test");
        request.setEmail("medico@test.cl");
        request.setContrasena("pass");

        when(usuarioRepository.existsByEmail("medico@test.cl")).thenReturn(false);
        when(usuarioRepository.existsByRut("44444444-4")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(rolRepository.findByNombreRol("ROLE_MEDICO")).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        String resultado = authService.registrarUsuarioAdmin(request, "ROLE_MEDICO");

        assertTrue(resultado.contains("ROLE_MEDICO"));
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void validarToken_conTokenValido_deberiaRetornarDTO() {
        String token = "token-valido";
        when(jwtTokenProvider.validarToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailDesdeToken(token)).thenReturn("admin@test.cl");
        when(usuarioRepository.findByEmailAndEstadoTrue("admin@test.cl"))
                .thenReturn(Optional.of(usuario));

        LoginResponseDTO response = authService.validarToken(token);

        assertNotNull(response);
        assertEquals("admin@test.cl", response.getEmail());
    }

    @Test
    void validarToken_conTokenInvalido_deberiaLanzarExcepcion() {
        when(jwtTokenProvider.validarToken("token-malo")).thenReturn(false);

        Exception ex = assertThrows(RuntimeException.class, () -> authService.validarToken("token-malo"));
        assertTrue(ex.getMessage().contains("Token JWT inválido"));
    }
}
