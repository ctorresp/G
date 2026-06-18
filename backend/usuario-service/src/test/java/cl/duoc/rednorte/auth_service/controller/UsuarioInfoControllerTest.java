package cl.duoc.rednorte.auth_service.controller;

import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.usuarios.model.Especialidad;
import cl.duoc.rednorte.usuarios.model.Medico;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.service.MedicoService;
import cl.duoc.rednorte.usuarios.service.UsuarioService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioInfoControllerTest {

    @Mock private UsuarioService usuarioService;
    @Mock private MedicoService medicoService;

    @InjectMocks private UsuarioInfoController usuarioInfoController;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void obtenerPorRut_conRutValido_deberiaRetornar200() {
        Rol rol = new Rol();
        rol.setNombreRol("ROLE_MEDICO");

        Usuario usuario = new Usuario();
        usuario.setRut("11111111-1");
        usuario.setNombre("Medico Test");
        usuario.setEmail("medico@test.cl");
        usuario.setRol(rol);

        when(usuarioService.buscarPorRut("11111111-1")).thenReturn(Optional.of(usuario));

        ResponseEntity<?> response = usuarioInfoController.obtenerPorRut("11111111-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("11111111-1", body.get("rut"));
        assertEquals("Medico Test", body.get("nombre"));
        assertEquals("medico@test.cl", body.get("email"));
        assertEquals("ROLE_MEDICO", body.get("rol"));
    }

    @Test
    void obtenerPorRut_conRutInexistente_deberiaRetornar404() {
        when(usuarioService.buscarPorRut("00000000-0")).thenReturn(Optional.empty());

        ResponseEntity<?> response = usuarioInfoController.obtenerPorRut("00000000-0");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void obtenerPerfilMedico_conUsuarioValido_deberiaRetornar200() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("medico@test.cl");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Rol rol = new Rol();
        rol.setNombreRol("ROLE_MEDICO");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Medico Test");
        usuario.setEmail("medico@test.cl");
        usuario.setRol(rol);

        Especialidad especialidad = new Especialidad();
        especialidad.setIdEspecialidad(1L);
        especialidad.setNombre("Cardiología");

        Medico medico = new Medico();
        medico.setIdUsuario(1L);
        medico.setUsuario(usuario);
        medico.setEspecialidad(especialidad);

        when(usuarioService.buscarPorEmailActivo("medico@test.cl")).thenReturn(Optional.of(usuario));
        when(medicoService.obtenerOCrear(usuario)).thenReturn(medico);

        ResponseEntity<?> response = usuarioInfoController.obtenerPerfilMedico();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(1L, body.get("idUsuario"));
        assertEquals("11111111-1", body.get("rut"));
        assertEquals("Cardiología", body.get("especialidadNombre"));
    }

    @Test
    void obtenerPerfilMedico_conUsuarioInexistente_deberiaLanzarExcepcion() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("no@test.cl");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(usuarioService.buscarPorEmailActivo("no@test.cl")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioInfoController.obtenerPerfilMedico());
    }
}
