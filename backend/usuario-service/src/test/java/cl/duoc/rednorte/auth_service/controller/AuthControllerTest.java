package cl.duoc.rednorte.auth_service.controller;

import cl.duoc.rednorte.auth_service.dto.LoginRequestDTO;
import cl.duoc.rednorte.auth_service.dto.LoginResponseDTO;
import cl.duoc.rednorte.auth_service.dto.RegistroRequestDTO;
import cl.duoc.rednorte.auth_service.service.AuthService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private AuthService authService;
    @InjectMocks private AuthController authController;

    @Test
    void login_conCredencialesValidas_deberiaRetornar200() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("admin@test.cl");
        request.setContrasena("pass");

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setToken("token");
        responseDTO.setTipoToken("Bearer");
        responseDTO.setIdUsuario(1L);
        responseDTO.setRut("12345678-9");
        responseDTO.setNombre("Admin");
        responseDTO.setEmail("admin@test.cl");
        responseDTO.setRol("ROLE_ADMIN");
        responseDTO.setExpiracion(86400000L);

        when(authService.login(request)).thenReturn(responseDTO);

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(LoginResponseDTO.class, response.getBody());
    }

    @Test
    void login_conCredencialesInvalidas_deberiaRetornar401() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("bad@test.cl");
        request.setContrasena("wrong");

        when(authService.login(request)).thenThrow(new RuntimeException("Bad credentials"));

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void registrarPaciente_conExito_deberiaRetornar201() throws Exception {
        RegistroRequestDTO request = new RegistroRequestDTO();
        request.setRut("11111111-1");
        request.setNombre("Test");
        request.setEmail("test@test.cl");
        request.setContrasena("pass");

        when(authService.registrarPaciente(request)).thenReturn("Paciente registrado exitosamente");

        ResponseEntity<?> response = authController.registrarPaciente(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void registrarPaciente_conError_deberiaRetornar400() throws Exception {
        RegistroRequestDTO request = new RegistroRequestDTO();
        when(authService.registrarPaciente(request)).thenThrow(new RuntimeException("Email duplicado"));

        ResponseEntity<?> response = authController.registrarPaciente(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void validarToken_conHeaderValido_deberiaRetornar200() {
        String token = "Bearer token-valido";
        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setToken("token-valido");
        responseDTO.setTipoToken("Bearer");
        responseDTO.setIdUsuario(1L);
        responseDTO.setRut("12345678-9");
        responseDTO.setNombre("Admin");
        responseDTO.setEmail("admin@test.cl");
        responseDTO.setRol("ROLE_ADMIN");
        responseDTO.setExpiracion(86400000L);

        when(authService.validarToken("token-valido")).thenReturn(responseDTO);

        ResponseEntity<?> response = authController.validarToken(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void validarToken_sinHeader_deberiaRetornar400() {
        ResponseEntity<?> response = authController.validarToken(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.get("error").toString().contains("inválido"));
    }
}
