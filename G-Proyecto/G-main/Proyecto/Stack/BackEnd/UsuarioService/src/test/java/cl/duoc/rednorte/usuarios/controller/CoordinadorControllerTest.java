package cl.duoc.rednorte.usuarios.controller;

import cl.duoc.rednorte.usuarios.dto.CoordinadorRequestDTO;
import cl.duoc.rednorte.usuarios.dto.CoordinadorResponseDTO;
import cl.duoc.rednorte.usuarios.model.Coordinador;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.service.CoordinadorService;
import cl.duoc.rednorte.usuarios.service.UsuarioService;

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
class CoordinadorControllerTest {

    @Mock private CoordinadorService coordinadorService;
    @Mock private UsuarioService usuarioService;

    @InjectMocks private CoordinadorController coordinadorController;

    @Test
    void listarTodos_deberiaRetornar200() {
        CoordinadorResponseDTO dto = new CoordinadorResponseDTO();
        dto.setIdUsuario(1L);
        dto.setEmail("coordinador@test.cl");

        when(coordinadorService.listarTodos()).thenReturn(List.of(dto));

        ResponseEntity<List<CoordinadorResponseDTO>> response = coordinadorController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listarTodos_sinCoordinadores_deberiaRetornar200() {
        when(coordinadorService.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<CoordinadorResponseDTO>> response = coordinadorController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void crear_conDatosValidos_deberiaRetornar201() {
        CoordinadorRequestDTO request = new CoordinadorRequestDTO();
        request.setRut("11111111-1");
        request.setNombre("Coordinador Nuevo");
        request.setEmail("coordinador@test.cl");
        request.setContrasena("pass");
        request.setAreaAsignada("Urgencias");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Coordinador Nuevo");
        usuario.setEmail("coordinador@test.cl");

        Coordinador coordinador = new Coordinador();
        coordinador.setIdUsuario(1L);
        coordinador.setUsuario(usuario);
        coordinador.setAreaAsignada("Urgencias");

        when(usuarioService.crear(any(), eq("ROLE_COORDINADOR"))).thenReturn(usuario);
        when(coordinadorService.crear(usuario, "Urgencias")).thenReturn(coordinador);

        ResponseEntity<?> response = coordinadorController.crear(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(CoordinadorResponseDTO.class, response.getBody());
    }

    @Test
    void crear_conError_deberiaRetornar400() {
        CoordinadorRequestDTO request = new CoordinadorRequestDTO();
        request.setRut("11111111-1");
        request.setNombre("Coordinador Nuevo");
        request.setEmail("coordinador@test.cl");
        request.setContrasena("pass");

        when(usuarioService.crear(any(), eq("ROLE_COORDINADOR")))
                .thenThrow(new RuntimeException("Email duplicado"));

        ResponseEntity<?> response = coordinadorController.crear(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.get("error").toString().contains("Email duplicado"));
    }
}
