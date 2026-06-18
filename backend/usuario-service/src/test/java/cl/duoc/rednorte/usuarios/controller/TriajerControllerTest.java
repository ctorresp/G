package cl.duoc.rednorte.usuarios.controller;

import cl.duoc.rednorte.usuarios.dto.TriajerRequestDTO;
import cl.duoc.rednorte.usuarios.dto.TriajerResponseDTO;
import cl.duoc.rednorte.usuarios.model.Triajer;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.service.TriajerService;
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
class TriajerControllerTest {

    @Mock private TriajerService triajerService;
    @Mock private UsuarioService usuarioService;

    @InjectMocks private TriajerController triajerController;

    @Test
    void listarTodos_deberiaRetornar200() {
        TriajerResponseDTO dto = new TriajerResponseDTO();
        dto.setIdUsuario(1L);
        dto.setEmail("triajer@test.cl");

        when(triajerService.listarTodos()).thenReturn(List.of(dto));

        ResponseEntity<List<TriajerResponseDTO>> response = triajerController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listarTodos_sinTriajers_deberiaRetornar200() {
        when(triajerService.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<TriajerResponseDTO>> response = triajerController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void crear_conDatosValidos_deberiaRetornar201() {
        TriajerRequestDTO request = new TriajerRequestDTO();
        request.setRut("11111111-1");
        request.setNombre("Triajer Nuevo");
        request.setEmail("triajer@test.cl");
        request.setContrasena("pass");
        request.setCertificaciones("Cert A");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Triajer Nuevo");
        usuario.setEmail("triajer@test.cl");

        Triajer triajer = new Triajer();
        triajer.setIdUsuario(1L);
        triajer.setUsuario(usuario);
        triajer.setCertificaciones("Cert A");

        when(usuarioService.crear(any(), eq("ROLE_TRIAJER"))).thenReturn(usuario);
        when(triajerService.crear(usuario, "Cert A")).thenReturn(triajer);

        ResponseEntity<?> response = triajerController.crear(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(TriajerResponseDTO.class, response.getBody());
    }

    @Test
    void crear_conError_deberiaRetornar400() {
        TriajerRequestDTO request = new TriajerRequestDTO();
        request.setRut("11111111-1");
        request.setNombre("Triajer Nuevo");
        request.setEmail("triajer@test.cl");
        request.setContrasena("pass");

        when(usuarioService.crear(any(), eq("ROLE_TRIAJER")))
                .thenThrow(new RuntimeException("Email duplicado"));

        ResponseEntity<?> response = triajerController.crear(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.get("error").toString().contains("Email duplicado"));
    }
}
