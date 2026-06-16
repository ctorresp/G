package cl.duoc.rednorte.auth_service.controller;

import cl.duoc.rednorte.usuarios.dto.UsuarioResponseDTO;
import cl.duoc.rednorte.auth_service.service.AdminService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock private AdminService adminService;

    @InjectMocks private AdminController adminController;

    @Test
    void listarUsuarios_deberiaRetornar200() {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(1L);
        dto.setEmail("admin@test.cl");

        when(adminService.listarUsuarios()).thenReturn(List.of(dto));

        ResponseEntity<List<UsuarioResponseDTO>> response = adminController.listarUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listarUsuarios_sinUsuarios_deberiaRetornar200() {
        when(adminService.listarUsuarios()).thenReturn(List.of());

        ResponseEntity<List<UsuarioResponseDTO>> response = adminController.listarUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void cambiarEstado_conEstadoValido_deberiaRetornar200() {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(1L);
        dto.setEstado(false);

        Map<String, Boolean> body = Map.of("estado", false);
        when(adminService.cambiarEstadoUsuario(1L, false)).thenReturn(dto);

        ResponseEntity<?> response = adminController.cambiarEstado(1L, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("Estado actualizado exitosamente", responseBody.get("mensaje"));
    }

    @Test
    void cambiarEstado_sinCampoEstado_deberiaRetornar400() {
        Map<String, Boolean> body = Map.of();

        ResponseEntity<?> response = adminController.cambiarEstado(1L, body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertTrue(responseBody.get("error").toString().contains("estado"));
    }

    @Test
    void cambiarEstado_conIdInexistente_deberiaRetornar404() {
        Map<String, Boolean> body = Map.of("estado", true);
        when(adminService.cambiarEstadoUsuario(99L, true))
                .thenThrow(new RuntimeException("Usuario no encontrado con ID: 99"));

        ResponseEntity<?> response = adminController.cambiarEstado(99L, body);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
