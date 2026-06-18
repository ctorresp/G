package cl.duoc.rednorte.usuarios.controller;

import cl.duoc.rednorte.usuarios.dto.MedicoRequestDTO;
import cl.duoc.rednorte.usuarios.dto.MedicoResponseDTO;
import cl.duoc.rednorte.usuarios.model.Especialidad;
import cl.duoc.rednorte.usuarios.model.Medico;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.service.MedicoService;
import cl.duoc.rednorte.usuarios.service.UsuarioService;
import cl.duoc.rednorte.auth_service.model.Rol;

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
class MedicoControllerTest {

    @Mock private MedicoService medicoService;
    @Mock private UsuarioService usuarioService;

    @InjectMocks private MedicoController medicoController;

    @Test
    void listarTodos_deberiaRetornar200() {
        MedicoResponseDTO dto = new MedicoResponseDTO();
        dto.setIdUsuario(1L);
        dto.setEmail("medico@test.cl");

        when(medicoService.listarTodos()).thenReturn(List.of(dto));

        ResponseEntity<List<MedicoResponseDTO>> response = medicoController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listarTodos_sinMedicos_deberiaRetornar200() {
        when(medicoService.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<MedicoResponseDTO>> response = medicoController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void obtenerPorRut_conRutValido_deberiaRetornar200() {
        Rol rol = new Rol();
        rol.setNombreRol("ROLE_MEDICO");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Medico Test");
        usuario.setEmail("medico@test.cl");
        usuario.setRol(rol);

        Medico medico = new Medico();
        medico.setIdUsuario(1L);
        medico.setUsuario(usuario);

        when(medicoService.buscarPorRut("11111111-1")).thenReturn(Optional.of(medico));

        ResponseEntity<?> response = medicoController.obtenerPorRut("11111111-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerPorRut_conRutInexistente_deberiaRetornar404() {
        when(medicoService.buscarPorRut("00000000-0")).thenReturn(Optional.empty());

        ResponseEntity<?> response = medicoController.obtenerPorRut("00000000-0");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void obtenerPorIdUsuario_conIdValido_deberiaRetornar200() {
        Rol rol = new Rol();
        rol.setNombreRol("ROLE_MEDICO");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Medico Test");
        usuario.setEmail("medico@test.cl");
        usuario.setRol(rol);

        Medico medico = new Medico();
        medico.setIdUsuario(1L);
        medico.setUsuario(usuario);

        when(medicoService.buscarPorIdUsuario(1L)).thenReturn(Optional.of(medico));

        ResponseEntity<?> response = medicoController.obtenerPorIdUsuario(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerPorIdUsuario_conIdInexistente_deberiaRetornar404() {
        when(medicoService.buscarPorIdUsuario(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = medicoController.obtenerPorIdUsuario(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void crear_conDatosValidos_deberiaRetornar201() {
        MedicoRequestDTO request = new MedicoRequestDTO();
        request.setRut("11111111-1");
        request.setNombre("Medico Nuevo");
        request.setEmail("medico@test.cl");
        request.setContrasena("pass");
        request.setEspecialidadId(1L);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Medico Nuevo");
        usuario.setEmail("medico@test.cl");

        Especialidad especialidad = new Especialidad();
        especialidad.setIdEspecialidad(1L);
        especialidad.setNombre("Cardiología");

        Medico medico = new Medico();
        medico.setIdUsuario(1L);
        medico.setUsuario(usuario);
        medico.setEspecialidad(especialidad);

        when(usuarioService.crear(any(), eq("ROLE_MEDICO"))).thenReturn(usuario);
        when(medicoService.crearConUsuario(usuario, 1L)).thenReturn(medico);

        ResponseEntity<?> response = medicoController.crear(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(MedicoResponseDTO.class, response.getBody());
    }

    @Test
    void crear_conError_deberiaRetornar400() {
        MedicoRequestDTO request = new MedicoRequestDTO();
        request.setRut("11111111-1");
        request.setNombre("Medico Nuevo");
        request.setEmail("medico@test.cl");
        request.setContrasena("pass");

        when(usuarioService.crear(any(), eq("ROLE_MEDICO")))
                .thenThrow(new RuntimeException("RUT ya está registrado"));

        ResponseEntity<?> response = medicoController.crear(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.get("error").toString().contains("RUT ya está registrado"));
    }
}
