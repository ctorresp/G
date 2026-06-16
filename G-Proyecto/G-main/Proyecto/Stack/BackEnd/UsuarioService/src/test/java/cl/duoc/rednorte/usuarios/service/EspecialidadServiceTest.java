package cl.duoc.rednorte.usuarios.service;

import cl.duoc.rednorte.usuarios.dto.EspecialidadResponseDTO;
import cl.duoc.rednorte.usuarios.model.Especialidad;
import cl.duoc.rednorte.usuarios.repository.EspecialidadRepository;

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
class EspecialidadServiceTest {

    @Mock private EspecialidadRepository especialidadRepository;

    @InjectMocks private EspecialidadService especialidadService;

    private Especialidad especialidad;

    @BeforeEach
    void setUp() {
        especialidad = new Especialidad();
        especialidad.setIdEspecialidad(1L);
        especialidad.setNombre("Cardiología");
        especialidad.setDescripcion("Especialidad en cardiología");
    }

    @Test
    void listarTodas_deberiaRetornarLista() {
        when(especialidadRepository.findAll()).thenReturn(List.of(especialidad));

        List<EspecialidadResponseDTO> resultado = especialidadService.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals("Cardiología", resultado.get(0).getNombre());
    }

    @Test
    void listarTodas_sinEspecialidades_deberiaRetornarListaVacia() {
        when(especialidadRepository.findAll()).thenReturn(List.of());

        List<EspecialidadResponseDTO> resultado = especialidadService.listarTodas();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorId_conIdValido_deberiaRetornarEspecialidad() {
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));

        Optional<Especialidad> resultado = especialidadService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Cardiología", resultado.get().getNombre());
    }

    @Test
    void buscarPorId_conIdInexistente_deberiaRetornarVacio() {
        when(especialidadRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Especialidad> resultado = especialidadService.buscarPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorNombre_conNombreValido_deberiaRetornarEspecialidad() {
        when(especialidadRepository.findByNombre("Cardiología")).thenReturn(Optional.of(especialidad));

        Optional<Especialidad> resultado = especialidadService.buscarPorNombre("Cardiología");

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdEspecialidad());
    }

    @Test
    void buscarPorNombre_conNombreInexistente_deberiaRetornarVacio() {
        when(especialidadRepository.findByNombre("Neurología")).thenReturn(Optional.empty());

        Optional<Especialidad> resultado = especialidadService.buscarPorNombre("Neurología");

        assertTrue(resultado.isEmpty());
    }
}
