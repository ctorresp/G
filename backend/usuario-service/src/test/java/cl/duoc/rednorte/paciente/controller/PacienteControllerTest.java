package cl.duoc.rednorte.paciente.controller;

import cl.duoc.rednorte.paciente.dto.PacienteResponseDTO;
import cl.duoc.rednorte.paciente.service.PacienteService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteControllerTest {

    @Mock private PacienteService pacienteService;
    @InjectMocks private PacienteController pacienteController;

    @Test
    void listarTodos_deberiaRetornar200() {
        when(pacienteService.listarTodos()).thenReturn(List.of());
        ResponseEntity<?> response = pacienteController.listarTodos();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void buscarPorRut_existente_deberiaRetornar200() {
        PacienteResponseDTO dto = new PacienteResponseDTO();
        when(pacienteService.buscarPorRut("12345678-9")).thenReturn(dto);
        ResponseEntity<?> response = pacienteController.buscarPorRut("12345678-9");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void buscarPorRut_inexistente_deberiaRetornar404() {
        when(pacienteService.buscarPorRut("00000000-0"))
                .thenThrow(new RuntimeException("no encontrado"));
        ResponseEntity<?> response = pacienteController.buscarPorRut("00000000-0");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void eliminar_deberiaRetornar200() {
        when(pacienteService.eliminarPaciente("12345678-9"))
                .thenReturn("Paciente eliminado");
        ResponseEntity<?> response = pacienteController.eliminar("12345678-9");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
