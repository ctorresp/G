package cl.duoc.rednorte.auth_service.service;

import cl.duoc.rednorte.auth_service.dto.MedicoUpdateRequestDTO;
import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminMedicoServiceTest {

    @Mock private UsuarioService usuarioService;
    @InjectMocks private AdminMedicoService adminMedicoService;

    private Usuario medico;

    @BeforeEach
    void setUp() {
        Rol rolMedico = new Rol();
        rolMedico.setIdRol(2L);
        rolMedico.setNombreRol("ROLE_MEDICO");

        medico = new Usuario();
        medico.setIdUsuario(2L);
        medico.setRut("87654321-0");
        medico.setNombre("Dr. Original");
        medico.setEmail("original@clinica.cl");
        medico.setEstado(true);
        medico.setRol(rolMedico);
    }

    @Test
    void listarMedicos_deberiaRetornarLista() {
        when(usuarioService.listarPorRol("ROLE_MEDICO"))
                .thenReturn(List.of(medico));

        List<Usuario> resultado = adminMedicoService.listarMedicos();

        assertEquals(1, resultado.size());
        assertEquals("Dr. Original", resultado.get(0).getNombre());
    }

    @Test
    void actualizarMedico_conDatosValidos_deberiaActualizar() {
        MedicoUpdateRequestDTO dto = new MedicoUpdateRequestDTO();
        dto.setRut("87654321-0");
        dto.setNombre("Dr. Actualizado");
        dto.setEmail("nuevo@clinica.cl");

        when(usuarioService.buscarPorId(2L)).thenReturn(Optional.of(medico));
        when(usuarioService.existePorEmail("nuevo@clinica.cl")).thenReturn(false);

        Usuario resultado = adminMedicoService.actualizarMedico(2L, dto);

        assertEquals("Dr. Actualizado", resultado.getNombre());
        assertEquals("nuevo@clinica.cl", resultado.getEmail());
    }

    @Test
    void actualizarMedico_conIdInvalido_deberiaLanzarExcepcion() {
        when(usuarioService.buscarPorId(999L)).thenReturn(Optional.empty());

        MedicoUpdateRequestDTO dto = new MedicoUpdateRequestDTO();
        dto.setNombre("Test");

        Exception ex = assertThrows(RuntimeException.class,
                () -> adminMedicoService.actualizarMedico(999L, dto));
        assertTrue(ex.getMessage().contains("no encontrado"));
    }

    @Test
    void actualizarMedico_conRutDuplicado_deberiaLanzarExcepcion() {
        MedicoUpdateRequestDTO dto = new MedicoUpdateRequestDTO();
        dto.setRut("99999999-9");

        when(usuarioService.buscarPorId(2L)).thenReturn(Optional.of(medico));
        when(usuarioService.existePorRut("99999999-9")).thenReturn(true);

        Exception ex = assertThrows(RuntimeException.class,
                () -> adminMedicoService.actualizarMedico(2L, dto));
        assertTrue(ex.getMessage().contains("RUT ya está registrado"));
    }

    @Test
    void eliminarMedico_conRutValido_deberiaEliminar() {
        when(usuarioService.buscarPorRut("87654321-0")).thenReturn(Optional.of(medico));

        String resultado = adminMedicoService.eliminarMedico("87654321-0");

        assertTrue(resultado.contains("eliminado exitosamente"));
        verify(usuarioService, times(1)).eliminar(medico);
    }

    @Test
    void eliminarMedico_conRutInvalido_deberiaLanzarExcepcion() {
        when(usuarioService.buscarPorRut("00000000-0")).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class,
                () -> adminMedicoService.eliminarMedico("00000000-0"));
        assertTrue(ex.getMessage().contains("no encontrado"));
    }
}
