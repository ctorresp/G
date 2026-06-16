package cl.duoc.rednorte.pabellon.controller;

import cl.duoc.rednorte.pabellon.dto.CirugiaDTO;
import cl.duoc.rednorte.pabellon.model.Cirugia;
import cl.duoc.rednorte.pabellon.model.Especialidad;
import cl.duoc.rednorte.pabellon.model.EstadoCirugia;
import cl.duoc.rednorte.pabellon.model.EstadoPabellon;
import cl.duoc.rednorte.pabellon.model.Pabellon;
import cl.duoc.rednorte.pabellon.service.CirugiaService;
import cl.duoc.rednorte.pabellon.service.PabellonService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CirugiaControllerTest {

    @Mock private CirugiaService cirugiaService;
    @Mock private PabellonService pabellonService;

    @InjectMocks
    private CirugiaController cirugiaController;

    private Cirugia cirugia;
    private Cirugia cirugiaSolicitada;
    private Pabellon pabellon;
    private Especialidad especialidad;

    @BeforeEach
    void setUp() {
        especialidad = new Especialidad();
        especialidad.setIdEspecialidad(1L);
        especialidad.setNombre("Cirugía General");

        pabellon = new Pabellon();
        pabellon.setIdPabellon(1L);
        pabellon.setNumero("Pabellón 1");
        pabellon.setEstado(EstadoPabellon.DISPONIBLE);

        cirugia = new Cirugia();
        cirugia.setIdCirugia(1L);
        cirugia.setPacienteRut("11111111-1");
        cirugia.setMedicoRut("22222222-2");
        cirugia.setEspecialidad(especialidad);
        cirugia.setPabellon(pabellon);
        cirugia.setEstado(EstadoCirugia.PROGRAMADA);

        cirugiaSolicitada = new Cirugia();
        cirugiaSolicitada.setIdCirugia(2L);
        cirugiaSolicitada.setPacienteRut("33333333-3");
        cirugiaSolicitada.setMedicoRut("44444444-4");
        cirugiaSolicitada.setEspecialidad(especialidad);
        cirugiaSolicitada.setEstado(EstadoCirugia.SOLICITADA);
        cirugiaSolicitada.setTriajeCompletado(true);
    }

    @Test
    void listarTodas_deberiaRetornar200ConLista() {
        when(cirugiaService.listarTodas()).thenReturn(List.of(cirugia));

        ResponseEntity<List<CirugiaDTO>> response = cirugiaController.listarTodas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void obtenerPorId_deberiaRetornar200() {
        when(cirugiaService.obtenerPorId(1L)).thenReturn(cirugia);

        ResponseEntity<CirugiaDTO> response = cirugiaController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("11111111-1", response.getBody().getPacienteRut());
    }

    @Test
    void listarPorMedico_deberiaRetornar200() {
        when(cirugiaService.listarPorMedico("22222222-2")).thenReturn(List.of(cirugia));

        ResponseEntity<List<CirugiaDTO>> response = cirugiaController.listarPorMedico("22222222-2");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listarPorPaciente_deberiaRetornar200() {
        when(cirugiaService.listarPorPaciente("11111111-1")).thenReturn(List.of(cirugia));

        ResponseEntity<List<CirugiaDTO>> response = cirugiaController.listarPorPaciente("11111111-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void crear_deberiaRetornar200() {
        when(cirugiaService.crear(any(Cirugia.class))).thenReturn(cirugia);

        ResponseEntity<CirugiaDTO> response = cirugiaController.crear(cirugia);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getIdCirugia());
    }

    @Test
    void actualizar_deberiaRetornar200() {
        when(cirugiaService.actualizar(eq(1L), any(Cirugia.class))).thenReturn(cirugia);

        ResponseEntity<CirugiaDTO> response = cirugiaController.actualizar(1L, cirugia);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void cancelar_conMotivo_deberiaRetornar204() {
        Map<String, String> body = Map.of("motivo", "Motivo de prueba");

        ResponseEntity<Void> response = cirugiaController.cancelar(1L, body);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cirugiaService).cancelar(1L, "Motivo de prueba");
    }

    @Test
    void cancelar_sinMotivo_deberiaUsarDefault() {
        ResponseEntity<Void> response = cirugiaController.cancelar(1L, null);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cirugiaService).cancelar(1L, "CANCELADO_POR_ADMIN");
    }

    @Test
    void marcarNoShow_deberiaRetornar204() {
        ResponseEntity<Void> response = cirugiaController.marcarNoShow(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cirugiaService).marcarNoShow(1L);
    }

    @Test
    void completar_deberiaRetornar204() {
        ResponseEntity<Void> response = cirugiaController.completar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cirugiaService).completar(1L);
    }

    @Test
    void reasignar_deberiaRetornar200() {
        Map<String, String> body = Map.of(
                "nuevoPacienteRut", "55555555-5",
                "adminRut", "admin",
                "motivo", "reasignación");

        when(cirugiaService.reasignar(1L, "55555555-5", "admin", "reasignación"))
                .thenReturn(cirugia);

        ResponseEntity<CirugiaDTO> response = cirugiaController.reasignar(1L, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void solicitar_deberiaRetornar200() {
        when(cirugiaService.solicitar(any(Cirugia.class))).thenReturn(cirugiaSolicitada);

        ResponseEntity<CirugiaDTO> response = cirugiaController.solicitar(cirugiaSolicitada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void listarSolicitadas_deberiaRetornar200() {
        when(cirugiaService.listarSolicitadas()).thenReturn(List.of(cirugiaSolicitada));

        ResponseEntity<List<CirugiaDTO>> response = cirugiaController.listarSolicitadas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listarPendientesTriaje_deberiaRetornar200() {
        when(cirugiaService.listarPendientesTriaje()).thenReturn(List.of(cirugiaSolicitada));

        ResponseEntity<List<CirugiaDTO>> response = cirugiaController.listarPendientesTriaje();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void marcarTriajeCompletado_deberiaRetornar204() {
        ResponseEntity<Void> response = cirugiaController.marcarTriajeCompletado(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cirugiaService).marcarTriajeCompletado(1L);
    }

    @Test
    void programar_deberiaRetornar200() {
        Map<String, Object> body = Map.of(
                "pabellonId", 1L,
                "fechaProgramada", "2025-07-01",
                "horaInicio", "08:00",
                "horaFin", "10:00");

        when(pabellonService.obtenerPorId(1L)).thenReturn(pabellon);
        when(cirugiaService.programar(eq(1L), eq(pabellon), eq(LocalDate.of(2025, 7, 1)),
                eq(LocalTime.of(8, 0)), eq(LocalTime.of(10, 0))))
                .thenReturn(cirugia);

        ResponseEntity<CirugiaDTO> response = cirugiaController.programar(1L, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void obtenerPorId_conIdInvalido_lanzaExcepcion() {
        when(cirugiaService.obtenerPorId(999L)).thenThrow(new RuntimeException("Cirugía no encontrada con id: 999"));

        Exception ex = assertThrows(RuntimeException.class, () -> cirugiaController.obtenerPorId(999L));
        assertTrue(ex.getMessage().contains("no encontrada"));
    }
}
