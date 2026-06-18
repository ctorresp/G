package cl.duoc.rednorte.reasignacion.service;

import cl.duoc.rednorte.reasignacion.model.Reasignacion;
import cl.duoc.rednorte.reasignacion.repository.ReasignacionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReasignacionServiceTest {

    @Mock private ReasignacionRepository repository;
    @Mock private RestTemplate restTemplate;

    @InjectMocks private ReasignacionService service;

    private Reasignacion reasignacion;

    @BeforeEach
    void setUp() throws Exception {
        Field field = ReasignacionService.class.getDeclaredField("pabellonServiceUrl");
        field.setAccessible(true);
        field.set(service, "http://localhost:8082");

        reasignacion = new Reasignacion();
        reasignacion.setIdReasignacion(1L);
        reasignacion.setCirugiaOriginalId(100L);
        reasignacion.setCirugiaReasignadaId(101L);
        reasignacion.setMotivo("NO_APTO - Reasignación automática");
        reasignacion.setFechaReasignacion(LocalDateTime.now());
    }

    @Test
    void listarTodas_deberiaRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(reasignacion));

        List<Reasignacion> resultado = service.listarTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).getCirugiaOriginalId());
        verify(repository).findAll();
    }

    @Test
    void listarTodas_sinDatos_deberiaRetornarVacio() {
        when(repository.findAll()).thenReturn(List.of());

        List<Reasignacion> resultado = service.listarTodas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarListaEspera_deberiaRetornarTodas() {
        Map<String, Object> cirugia = new HashMap<>();
        cirugia.put("idCirugia", 1);
        cirugia.put("especialidadId", 10);
        cirugia.put("fechaSolicitud", "2026-01-01T10:00:00");
        cirugia.put("pacienteRut", "11111111-1");

        ResponseEntity<List<Map<String, Object>>> response = new ResponseEntity<>(
                List.of(cirugia), HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://localhost:8082/api/pabellon/cirugias/solicitadas"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        List<Map<String, Object>> resultado = service.listarListaEspera(null);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void listarListaEspera_conFiltroEspecialidad_deberiaFiltrar() {
        Map<String, Object> cirugia1 = new HashMap<>();
        cirugia1.put("idCirugia", 1);
        cirugia1.put("especialidadId", 10);
        cirugia1.put("fechaSolicitud", "2026-01-01T10:00:00");

        Map<String, Object> cirugia2 = new HashMap<>();
        cirugia2.put("idCirugia", 2);
        cirugia2.put("especialidadId", 20);
        cirugia2.put("fechaSolicitud", "2026-01-02T10:00:00");

        ResponseEntity<List<Map<String, Object>>> response = new ResponseEntity<>(
                List.of(cirugia1, cirugia2), HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        List<Map<String, Object>> resultado = service.listarListaEspera(10L);

        assertEquals(1, resultado.size());
        assertEquals(10, ((Number) resultado.get(0).get("especialidadId")).intValue());
    }

    @Test
    void listarListaEspera_conRespuestaNula_deberiaRetornarVacio() {
        ResponseEntity<List<Map<String, Object>>> response = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        List<Map<String, Object>> resultado = service.listarListaEspera(null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void marcarNoAptoYReasignar_cirugiaNoEncontrada_deberiaLanzarExcepcion() {
        String url = "http://localhost:8082/api/pabellon/cirugias/99";
        when(restTemplate.exchange(
                eq(url), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Not found"));

        Exception ex = assertThrows(RuntimeException.class,
                () -> service.marcarNoAptoYReasignar(99L));
        assertTrue(ex.getMessage().contains("Cirugía no encontrada con id: 99"));
    }

    @Test
    void marcarNoAptoYReasignar_estadoIncorrecto_deberiaLanzarExcepcion() {
        String url = "http://localhost:8082/api/pabellon/cirugias/1";
        Map<String, Object> cirugia = new HashMap<>();
        cirugia.put("idCirugia", 1);
        cirugia.put("estado", "CONFIRMADA");
        cirugia.put("especialidadId", 10);

        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(cirugia, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(url), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        Exception ex = assertThrows(RuntimeException.class,
                () -> service.marcarNoAptoYReasignar(1L));
        assertTrue(ex.getMessage().contains("Solo se puede marcar NO APTO en cirugías PROGRAMADAS"));
    }

    @Test
    void marcarNoAptoYReasignar_sinPacientesEnEspera_deberiaCancelarYRetornarMensaje() {
        Long cirugiaId = 1L;
        String getUrl = "http://localhost:8082/api/pabellon/cirugias/" + cirugiaId;
        String cancelUrl = "http://localhost:8082/api/pabellon/cirugias/" + cirugiaId + "/cancelar";
        String solicitudesUrl = "http://localhost:8082/api/pabellon/cirugias/solicitadas";

        Map<String, Object> cirugiaOriginal = new HashMap<>();
        cirugiaOriginal.put("idCirugia", cirugiaId);
        cirugiaOriginal.put("estado", "PROGRAMADA");
        cirugiaOriginal.put("especialidadId", 10);
        cirugiaOriginal.put("especialidadNombre", "Cardiología");
        cirugiaOriginal.put("pacienteRut", "11111111-1");
        cirugiaOriginal.put("medicoRut", "22222222-2");
        cirugiaOriginal.put("pabellonId", 5);
        cirugiaOriginal.put("fechaProgramada", "2026-01-15");
        cirugiaOriginal.put("horaInicio", "08:00");
        cirugiaOriginal.put("horaFin", "10:00");

        when(restTemplate.exchange(
                eq(getUrl), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(cirugiaOriginal, HttpStatus.OK));

        when(restTemplate.exchange(
                anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Void.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<List<Map<String, Object>>> solicitudesResponse =
                new ResponseEntity<>(List.of(), HttpStatus.OK);
        when(restTemplate.exchange(
                eq(solicitudesUrl), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(solicitudesResponse);

        Map<String, Object> resultado = service.marcarNoAptoYReasignar(cirugiaId);

        assertNotNull(resultado);
        assertNotNull(resultado.get("cirugiaCancelada"));
        assertNull(resultado.get("cirugiaReasignada"));
        assertTrue(((String) resultado.get("mensaje")).contains("No hay pacientes en espera"));
        verify(repository, never()).save(any());
    }

    @Test
    void marcarNoAptoYReasignar_conPacienteEnEspera_deberiaReasignar() {
        Long cirugiaId = 1L;
        String getUrl = "http://localhost:8082/api/pabellon/cirugias/" + cirugiaId;
        String cancelUrlBase = "http://localhost:8082/api/pabellon/cirugias/" + cirugiaId + "/cancelar";
        String solicitudesUrl = "http://localhost:8082/api/pabellon/cirugias/solicitadas";
        String solicitarUrl = "http://localhost:8082/api/pabellon/cirugias/solicitar";

        Map<String, Object> cirugiaOriginal = new HashMap<>();
        cirugiaOriginal.put("idCirugia", cirugiaId);
        cirugiaOriginal.put("estado", "PROGRAMADA");
        cirugiaOriginal.put("especialidadId", 10);
        cirugiaOriginal.put("especialidadNombre", "Cardiología");
        cirugiaOriginal.put("pacienteRut", "11111111-1");
        cirugiaOriginal.put("medicoRut", "22222222-2");
        cirugiaOriginal.put("pabellonId", 5);
        cirugiaOriginal.put("fechaProgramada", "2026-01-15");
        cirugiaOriginal.put("horaInicio", "08:00");
        cirugiaOriginal.put("horaFin", "10:00");

        Map<String, Object> cirugiaEnEspera = new HashMap<>();
        cirugiaEnEspera.put("idCirugia", 2);
        cirugiaEnEspera.put("especialidadId", 10);
        cirugiaEnEspera.put("fechaSolicitud", "2026-01-01T10:00:00");
        cirugiaEnEspera.put("pacienteRut", "33333333-3");
        cirugiaEnEspera.put("medicoRut", "44444444-4");

        Map<String, Object> cirugiaCreada = new HashMap<>();
        cirugiaCreada.put("idCirugia", 200);

        Map<String, Object> cirugiaProgramada = new HashMap<>();
        cirugiaProgramada.put("idCirugia", 200);
        cirugiaProgramada.put("estado", "PROGRAMADA");

        when(restTemplate.exchange(
                eq(getUrl), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(cirugiaOriginal, HttpStatus.OK));

        when(restTemplate.exchange(
                eq(cancelUrlBase), eq(HttpMethod.PUT), any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<List<Map<String, Object>>> solicitudesResponse =
                new ResponseEntity<>(List.of(cirugiaEnEspera), HttpStatus.OK);
        when(restTemplate.exchange(
                eq(solicitudesUrl), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(solicitudesResponse);

        when(restTemplate.exchange(
                eq(solicitarUrl), eq(HttpMethod.POST), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(cirugiaCreada, HttpStatus.OK));

        String programarUrl = "http://localhost:8082/api/pabellon/cirugias/200/programar";
        when(restTemplate.exchange(
                eq(programarUrl), eq(HttpMethod.PUT), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(cirugiaProgramada, HttpStatus.OK));

        String cancelSiguienteUrl = "http://localhost:8082/api/pabellon/cirugias/2/cancelar";
        when(restTemplate.exchange(
                eq(cancelSiguienteUrl), eq(HttpMethod.PUT), any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        when(repository.save(any(Reasignacion.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> resultado = service.marcarNoAptoYReasignar(cirugiaId);

        assertNotNull(resultado);
        assertNotNull(resultado.get("cirugiaCancelada"));
        assertNotNull(resultado.get("cirugiaReasignada"));
        assertTrue(((String) resultado.get("mensaje")).contains("Slot reasignado a paciente"));
        verify(repository).save(any(Reasignacion.class));
    }
}
