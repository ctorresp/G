package cl.duoc.rednorte.auth_service.service;

import cl.duoc.rednorte.usuarios.dto.UsuarioResponseDTO;
import cl.duoc.rednorte.usuarios.service.UsuarioService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private UsuarioService usuarioService;

    @InjectMocks private AdminService adminService;

    @Test
    void listarUsuarios_deberiaDelegarAUsuarioService() {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(1L);
        dto.setRut("11111111-1");
        dto.setNombre("Admin Test");
        dto.setEmail("admin@test.cl");
        dto.setEstado(true);
        dto.setRol("ROLE_ADMIN");

        when(usuarioService.listarTodos()).thenReturn(List.of(dto));

        List<UsuarioResponseDTO> resultado = adminService.listarUsuarios();

        assertEquals(1, resultado.size());
        assertEquals("admin@test.cl", resultado.get(0).getEmail());
        verify(usuarioService).listarTodos();
    }

    @Test
    void listarUsuarios_sinUsuarios_deberiaRetornarListaVacia() {
        when(usuarioService.listarTodos()).thenReturn(List.of());

        List<UsuarioResponseDTO> resultado = adminService.listarUsuarios();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void cambiarEstadoUsuario_conDatosValidos_deberiaDelegar() {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(1L);
        dto.setEstado(false);

        when(usuarioService.cambiarEstado(1L, false)).thenReturn(dto);

        UsuarioResponseDTO resultado = adminService.cambiarEstadoUsuario(1L, false);

        assertNotNull(resultado);
        assertFalse(resultado.getEstado());
        verify(usuarioService).cambiarEstado(1L, false);
    }

    @Test
    void cambiarEstadoUsuario_conIdInexistente_deberiaPropagarExcepcion() {
        when(usuarioService.cambiarEstado(99L, false)).thenThrow(new RuntimeException("Usuario no encontrado"));

        Exception ex = assertThrows(RuntimeException.class, () -> adminService.cambiarEstadoUsuario(99L, false));
        assertTrue(ex.getMessage().contains("Usuario no encontrado"));
    }
}
