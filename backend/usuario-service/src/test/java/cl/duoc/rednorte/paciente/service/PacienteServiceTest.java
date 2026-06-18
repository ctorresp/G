package cl.duoc.rednorte.paciente.service;

import cl.duoc.rednorte.paciente.dto.PacienteResponseDTO;
import cl.duoc.rednorte.paciente.model.Paciente;
import cl.duoc.rednorte.paciente.repository.PacienteRepository;

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
class PacienteServiceTest {

    @Mock private PacienteRepository pacienteRepository;
    @InjectMocks private PacienteService pacienteService;

    private Paciente paciente;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setIdPaciente(1L);
        paciente.setRut("12345678-9");
        paciente.setNombre("Juan Pérez");
        paciente.setEmail("juan@correo.cl");
        paciente.setPrevision("FONASA");
        paciente.setGrupoSanguineo("O+");
        paciente.setPesoKg(java.math.BigDecimal.valueOf(70.5));
        paciente.setAlturaCm(java.math.BigDecimal.valueOf(175.0));
        paciente.setAlergias("Penicilina");
        paciente.setEnfermedadesCronicas("Asma");
    }

    @Test
    void listarTodos_deberiaRetornarListaDeDTOs() {
        when(pacienteRepository.findAll()).thenReturn(List.of(paciente));

        List<PacienteResponseDTO> resultado = pacienteService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("12345678-9", resultado.get(0).getRut());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        assertEquals("FONASA", resultado.get(0).getPrevision());
    }

    @Test
    void buscarPorRut_conRutExistente_deberiaRetornarDTO() {
        when(pacienteRepository.findByRut("12345678-9")).thenReturn(Optional.of(paciente));

        PacienteResponseDTO resultado = pacienteService.buscarPorRut("12345678-9");

        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("O+", resultado.getDatosClinicosSensibles().get("grupoSanguineo"));
    }

    @Test
    void buscarPorRut_conRutInexistente_deberiaLanzarExcepcion() {
        when(pacienteRepository.findByRut("00000000-0")).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class,
                () -> pacienteService.buscarPorRut("00000000-0"));
        assertTrue(ex.getMessage().contains("no encontrado"));
    }

    @Test
    void actualizarDatosClinicos_deberiaActualizarCampos() {
        when(pacienteRepository.findByRut("12345678-9")).thenReturn(Optional.of(paciente));
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(i -> i.getArgument(0));

        PacienteResponseDTO resultado = pacienteService.actualizarDatosClinicos(
                "12345678-9", "ISAPRE", "nuevo@correo.cl",
                "A+", 72.0, 176.0, null, null);

        assertEquals("ISAPRE", resultado.getPrevision());
    }

    @Test
    void eliminarPaciente_deberiaEliminar() {
        when(pacienteRepository.findByRut("12345678-9")).thenReturn(Optional.of(paciente));

        String mensaje = pacienteService.eliminarPaciente("12345678-9");

        assertTrue(mensaje.contains("eliminado"));
        verify(pacienteRepository, times(1)).delete(paciente);
    }
}
