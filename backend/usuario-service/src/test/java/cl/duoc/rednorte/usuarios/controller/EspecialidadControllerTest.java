package cl.duoc.rednorte.usuarios.controller;

import cl.duoc.rednorte.usuarios.dto.EspecialidadResponseDTO;
import cl.duoc.rednorte.usuarios.model.Especialidad;
import cl.duoc.rednorte.usuarios.service.EspecialidadService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EspecialidadControllerTest {

    @Mock private EspecialidadService especialidadService;

    @InjectMocks private EspecialidadController especialidadController;

    @Test
    void listarTodas_deberiaRetornar200() {
        EspecialidadResponseDTO dto = new EspecialidadResponseDTO();
        dto.setIdEspecialidad(1L);
        dto.setNombre("Cardiología");

        when(especialidadService.listarTodas()).thenReturn(List.of(dto));

        ResponseEntity<List<EspecialidadResponseDTO>> response = especialidadController.listarTodas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listarTodas_sinEspecialidades_deberiaRetornar200() {
        when(especialidadService.listarTodas()).thenReturn(List.of());

        ResponseEntity<List<EspecialidadResponseDTO>> response = especialidadController.listarTodas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void obtenerPorId_conIdValido_deberiaRetornar200() {
        Especialidad especialidad = mock(Especialidad.class);
        when(especialidadService.buscarPorId(1L)).thenReturn(Optional.of(especialidad));

        ResponseEntity<?> response = especialidadController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerPorId_conIdInexistente_deberiaRetornar404() {
        when(especialidadService.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = especialidadController.obtenerPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void obtenerPorNombre_conNombreValido_deberiaRetornar200() {
        Especialidad especialidad = mock(Especialidad.class);
        when(especialidadService.buscarPorNombre("Cardiología")).thenReturn(Optional.of(especialidad));

        ResponseEntity<?> response = especialidadController.obtenerPorNombre("Cardiología");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerPorNombre_conNombreInexistente_deberiaRetornar404() {
        when(especialidadService.buscarPorNombre("Neurología")).thenReturn(Optional.empty());

        ResponseEntity<?> response = especialidadController.obtenerPorNombre("Neurología");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
