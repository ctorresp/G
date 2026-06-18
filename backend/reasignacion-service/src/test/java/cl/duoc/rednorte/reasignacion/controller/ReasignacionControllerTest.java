package cl.duoc.rednorte.reasignacion.controller;

import cl.duoc.rednorte.reasignacion.dto.ReasignacionDTO;
import cl.duoc.rednorte.reasignacion.model.Reasignacion;
import cl.duoc.rednorte.reasignacion.service.ReasignacionService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReasignacionControllerTest {

    @Mock private ReasignacionService service;
    @InjectMocks private ReasignacionController controller;

    @Test
    void listarTodas_deberiaRetornar200() {
        Reasignacion r = new Reasignacion();
        r.setIdReasignacion(1L);
        r.setCirugiaOriginalId(100L);
        r.setCirugiaReasignadaId(101L);
        r.setMotivo("Test");
        r.setFechaReasignacion(LocalDateTime.now());

        when(service.listarTodas()).thenReturn(List.of(r));

        ResponseEntity<List<ReasignacionDTO>> response = controller.listarTodas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(100L, response.getBody().get(0).getCirugiaOriginalId());
    }

    @Test
    void listarTodas_sinDatos_deberiaRetornar200Vacio() {
        when(service.listarTodas()).thenReturn(List.of());

        ResponseEntity<List<ReasignacionDTO>> response = controller.listarTodas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void listarListaEspera_deberiaRetornar200() {
        Map<String, Object> item = Map.of("idCirugia", 1);
        when(service.listarListaEspera(null)).thenReturn(List.of(item));

        ResponseEntity<List<Map<String, Object>>> response = controller.listarListaEspera(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listarListaEspera_conEspecialidad_deberiaRetornar200() {
        Map<String, Object> item = Map.of("idCirugia", 1, "especialidadId", 5);
        when(service.listarListaEspera(5L)).thenReturn(List.of(item));

        ResponseEntity<List<Map<String, Object>>> response = controller.listarListaEspera(5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, ((Number) response.getBody().get(0).get("especialidadId")).intValue());
    }

    @Test
    void marcarNoApto_deberiaRetornar200() {
        Map<String, Object> resultado = Map.of(
                "cirugiaCancelada", Map.of("idCirugia", 1),
                "cirugiaReasignada", Map.of("idCirugia", 2),
                "mensaje", "Slot reasignado a paciente"
        );
        when(service.marcarNoAptoYReasignar(1L)).thenReturn(resultado);

        ResponseEntity<Map<String, Object>> response = controller.marcarNoApto(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Slot reasignado a paciente", response.getBody().get("mensaje"));
    }

    @Test
    void marcarNoApto_conError_deberiaLanzarExcepcion() {
        when(service.marcarNoAptoYReasignar(99L))
                .thenThrow(new RuntimeException("Cirugía no encontrada con id: 99"));

        assertThrows(RuntimeException.class, () -> controller.marcarNoApto(99L));
    }
}
