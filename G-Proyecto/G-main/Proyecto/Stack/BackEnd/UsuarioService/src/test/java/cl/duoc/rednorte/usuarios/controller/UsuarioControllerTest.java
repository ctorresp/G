package cl.duoc.rednorte.usuarios.controller;

import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.usuarios.dto.UsuarioResponseDTO;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.service.UsuarioService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock private UsuarioService usuarioService;

    @InjectMocks private UsuarioController usuarioController;

    @Test
    void listarTodos_deberiaRetornar200() {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(1L);
        dto.setEmail("admin@test.cl");

        when(usuarioService.listarTodos()).thenReturn(List.of(dto));

        ResponseEntity<List<UsuarioResponseDTO>> response = usuarioController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listarTodos_sinUsuarios_deberiaRetornar200() {
        when(usuarioService.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<UsuarioResponseDTO>> response = usuarioController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void obtenerPorId_conIdValido_deberiaRetornar200() {
        Rol rol = new Rol();
        rol.setNombreRol("ROLE_ADMIN");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Admin Test");
        usuario.setEmail("admin@test.cl");
        usuario.setEstado(true);
        usuario.setRol(rol);

        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<?> response = usuarioController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerPorId_conIdInexistente_deberiaRetornar404() {
        when(usuarioService.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = usuarioController.obtenerPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void cambiarEstado_conEstadoValido_deberiaRetornar200() {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(1L);
        dto.setEstado(false);

        Map<String, Boolean> body = Map.of("estado", false);
        when(usuarioService.cambiarEstado(1L, false)).thenReturn(dto);

        ResponseEntity<?> response = usuarioController.cambiarEstado(1L, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("Estado actualizado exitosamente", responseBody.get("mensaje"));
    }

    @Test
    void cambiarEstado_sinCampoEstado_deberiaRetornar400() {
        Map<String, Boolean> body = Map.of();

        ResponseEntity<?> response = usuarioController.cambiarEstado(1L, body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertTrue(responseBody.get("error").toString().contains("estado"));
    }

    @Test
    void cambiarEstado_conIdInexistente_deberiaRetornar404() {
        Map<String, Boolean> body = Map.of("estado", true);
        when(usuarioService.cambiarEstado(99L, true)).thenThrow(new RuntimeException("Usuario no encontrado con ID: 99"));

        ResponseEntity<?> response = usuarioController.cambiarEstado(99L, body);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
