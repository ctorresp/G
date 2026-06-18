package cl.duoc.rednorte.auth_service.service;

import cl.duoc.rednorte.auth_service.dto.LoginRequestDTO;
import cl.duoc.rednorte.auth_service.dto.LoginResponseDTO;
import cl.duoc.rednorte.auth_service.dto.RegistroRequestDTO;
import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.service.UsuarioService;
import cl.duoc.rednorte.usuarios.service.MedicoService;
import cl.duoc.rednorte.auth_service.security.JwtTokenProvider;
import cl.duoc.rednorte.paciente.model.Paciente;
import cl.duoc.rednorte.paciente.repository.PacienteRepository;
import cl.duoc.rednorte.usuarios.dto.UsuarioRequestDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UsuarioService usuarioService;
    @Mock private MedicoService medicoService;
    @Mock private PacienteRepository pacienteRepository;
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
        when(jwtTokenProvider.generarToken(authentication)).thenReturn("token123");
        when(jwtTokenProvider.getExpiracionMs()).thenReturn(86400000L);
        when(usuarioService.buscarPorEmailActivo("admin@test.cl"))
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

        when(usuarioService.buscarPorRut("11111111-1")).thenReturn(Optional.of(usuario));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generarToken(authentication)).thenReturn("token456");
        when(jwtTokenProvider.getExpiracionMs()).thenReturn(86400000L);
        when(usuarioService.buscarPorEmailActivo("admin@test.cl"))
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
    void registrarPaciente_conMedicoAsignado_deberiaAsignarMedico() {
        RegistroRequestDTO request = new RegistroRequestDTO();
        request.setRut("55555555-5");
        request.setNombre("Paciente con Medico");
        request.setEmail("pm@test.cl");
        request.setIdMedico(1L);

        when(pacienteRepository.findByRut("55555555-5")).thenReturn(Optional.empty());
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(i -> i.getArgument(0));

        String resultado = authService.registrarPaciente(request);

        assertTrue(resultado.contains("Paciente registrado exitosamente"));
        verify(pacienteRepository, times(1)).save(any(Paciente.class));
    }

    @Test
    void registrarUsuarioAdmin_deberiaCrearUsuarioConRol() {
        RegistroRequestDTO request = new RegistroRequestDTO();
        request.setRut("44444444-4");
        request.setNombre("Dr. Test");
        request.setEmail("medico@test.cl");
        request.setContrasena("pass");

        Usuario usuarioCreado = new Usuario();
        usuarioCreado.setIdUsuario(99L);
        when(usuarioService.crear(any(UsuarioRequestDTO.class), eq("ROLE_MEDICO")))
                .thenReturn(usuarioCreado);

        String resultado = authService.registrarUsuarioAdmin(request, "ROLE_MEDICO");

        assertTrue(resultado.contains("ROLE_MEDICO"));
        verify(usuarioService, times(1)).crear(any(UsuarioRequestDTO.class), eq("ROLE_MEDICO"));
    }

    @Test
    void registrarMedicoCompleto_deberiaCrearUsuarioYMedico() {
        RegistroRequestDTO request = new RegistroRequestDTO();
        request.setRut("66666666-6");
        request.setNombre("Dr. Completo");
        request.setEmail("completo@test.cl");
        request.setContrasena("pass");

        Usuario usuarioCreado = new Usuario();
        usuarioCreado.setIdUsuario(100L);
        when(usuarioService.crear(any(UsuarioRequestDTO.class), eq("ROLE_MEDICO")))
                .thenReturn(usuarioCreado);

        String resultado = authService.registrarMedicoCompleto(request, 1L);

        assertTrue(resultado.contains("Médico registrado exitosamente"));
        verify(medicoService, times(1)).crearConUsuario(usuarioCreado, 1L);
    }

    @Test
    void validarToken_conTokenValido_deberiaRetornarDTO() {
        String token = "token-valido";
        when(jwtTokenProvider.validarToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailDesdeToken(token)).thenReturn("admin@test.cl");
        when(usuarioService.buscarPorEmailActivo("admin@test.cl"))
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

    @Test
    void validarToken_conUsuarioInactivo_deberiaLanzarExcepcion() {
        String token = "token-valido";
        when(jwtTokenProvider.validarToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailDesdeToken(token)).thenReturn("inactivo@test.cl");
        when(usuarioService.buscarPorEmailActivo("inactivo@test.cl")).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> authService.validarToken(token));
        assertTrue(ex.getMessage().contains("Usuario no encontrado"));
    }
}
