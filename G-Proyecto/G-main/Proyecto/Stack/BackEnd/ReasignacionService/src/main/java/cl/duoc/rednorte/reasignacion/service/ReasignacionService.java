package cl.duoc.rednorte.reasignacion.service;

import cl.duoc.rednorte.reasignacion.model.Reasignacion;
import cl.duoc.rednorte.reasignacion.repository.ReasignacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Transactional
public class ReasignacionService {

    private static final Logger log = LoggerFactory.getLogger(ReasignacionService.class);

    private final ReasignacionRepository repository;
    private final RestTemplate restTemplate;

    @Value("${pabellon.service.url}")
    private String pabellonServiceUrl;

    public ReasignacionService(ReasignacionRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    @Transactional(readOnly = true)
    public List<Reasignacion> listarTodas() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarListaEspera(Long especialidadId) {
        String url = pabellonServiceUrl + "/api/pabellon/cirugias/solicitadas";
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, HttpMethod.GET, crearRequestConToken(),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        List<Map<String, Object>> solicitadas = response.getBody();
        if (solicitadas == null) return List.of();

        return solicitadas.stream()
                .filter(c -> especialidadId == null
                        || (c.get("especialidadId") != null
                            && ((Number) c.get("especialidadId")).longValue() == especialidadId))
                .sorted(Comparator.comparing(
                        c -> c.get("fechaSolicitud") != null ? (String) c.get("fechaSolicitud") : "",
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public Map<String, Object> marcarNoAptoYReasignar(Long cirugiaId) {
        log.info("No apto + reasignar para cirugía ID: {}", cirugiaId);

        Map<String, Object> original = obtenerCirugia(cirugiaId);
        if (original == null) {
            throw new RuntimeException("Cirugía no encontrada con id: " + cirugiaId);
        }

        String estado = (String) original.get("estado");
        if (!"PROGRAMADA".equals(estado)) {
            throw new RuntimeException("Solo se puede marcar NO APTO en cirugías PROGRAMADAS");
        }

        Number especialidadIdNum = (Number) original.get("especialidadId");
        Long especialidadId = especialidadIdNum != null ? especialidadIdNum.longValue() : null;
        Number pabellonIdNum = (Number) original.get("pabellonId");
        Long pabellonId = pabellonIdNum != null ? pabellonIdNum.longValue() : null;
        String fechaStr = original.get("fechaProgramada") != null ? original.get("fechaProgramada").toString() : null;
        String horaInicioStr = original.get("horaInicio") != null ? original.get("horaInicio").toString() : null;
        String horaFinStr = original.get("horaFin") != null ? original.get("horaFin").toString() : null;
        String pacienteRut = (String) original.get("pacienteRut");
        String medicoRut = (String) original.get("medicoRut");
        String especialidadNombre = (String) original.get("especialidadNombre");

        cancelarCirugia(cirugiaId, "PACIENTE_NO_APTO");

        List<Map<String, Object>> listaEspera = listarListaEspera(especialidadId);

        Map<String, Object> cirugiaReasignada = null;
        String mensaje = "No hay pacientes en espera para " + (especialidadNombre != null ? especialidadNombre : "la especialidad");

        if (!listaEspera.isEmpty()) {
            Map<String, Object> siguiente = listaEspera.get(0);
            Number siguienteIdNum = (Number) siguiente.get("idCirugia");
            Long siguienteId = siguienteIdNum != null ? siguienteIdNum.longValue() : null;
            String siguientePacienteRut = (String) siguiente.get("pacienteRut");
            String siguienteMedicoRut = (String) siguiente.get("medicoRut");

            Map<String, Object> nuevaCirugia = new HashMap<>();
            nuevaCirugia.put("pacienteRut", siguientePacienteRut);
            nuevaCirugia.put("medicoRut", siguienteMedicoRut);
            nuevaCirugia.put("especialidad", Map.of("idEspecialidad", especialidadId));

            String solicitarUrl = pabellonServiceUrl + "/api/pabellon/cirugias/solicitar";
            ResponseEntity<Map<String, Object>> solicitarResponse = restTemplate.exchange(
                    solicitarUrl, HttpMethod.POST, crearRequestConJson(nuevaCirugia),
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            Map<String, Object> creada = solicitarResponse.getBody();
            Number nuevaIdNum = creada != null ? (Number) creada.get("idCirugia") : null;
            Long nuevaId = nuevaIdNum != null ? nuevaIdNum.longValue() : null;

            if (nuevaId != null && pabellonId != null && fechaStr != null) {
                Map<String, Object> programarPayload = new HashMap<>();
                programarPayload.put("pabellonId", pabellonId);
                programarPayload.put("fechaProgramada", fechaStr);
                programarPayload.put("horaInicio", horaInicioStr != null ? horaInicioStr : "");
                programarPayload.put("horaFin", horaFinStr != null ? horaFinStr : "");

                String programarUrl = pabellonServiceUrl + "/api/pabellon/cirugias/" + nuevaId + "/programar";
                ResponseEntity<Map<String, Object>> programarResponse = restTemplate.exchange(
                        programarUrl, HttpMethod.PUT, crearRequestConJson(programarPayload),
                        new ParameterizedTypeReference<Map<String, Object>>() {}
                );
                cirugiaReasignada = programarResponse.getBody();
            }

            if (siguienteId != null) {
                cancelarCirugia(siguienteId, "REASIGNADO_A_OTRO_PACIENTE");
            }

            Reasignacion reasig = new Reasignacion();
            reasig.setCirugiaOriginalId(cirugiaId);
            reasig.setCirugiaReasignadaId(nuevaId);
            reasig.setMotivo("NO_APTO - Reasignación automática desde paciente " + pacienteRut);
            reasig.setFechaReasignacion(LocalDateTime.now());
            repository.save(reasig);

            mensaje = "Slot reasignado a paciente " + siguientePacienteRut;
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("cirugiaCancelada", original);
        resultado.put("cirugiaReasignada", cirugiaReasignada);
        resultado.put("mensaje", mensaje);
        return resultado;
    }

    private Map<String, Object> obtenerCirugia(Long id) {
        String url = pabellonServiceUrl + "/api/pabellon/cirugias/" + id;
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, crearRequestConToken(),
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error al obtener cirugía {}: {}", id, e.getMessage());
            return null;
        }
    }

    private void cancelarCirugia(Long id, String motivo) {
        String url = pabellonServiceUrl + "/api/pabellon/cirugias/" + id + "/cancelar";
        Map<String, String> body = Map.of("motivo", motivo);
        try {
            restTemplate.exchange(url, HttpMethod.PUT, crearRequestConJson(body), Void.class);
        } catch (Exception e) {
            log.error("Error al cancelar cirugía {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al cancelar cirugía: " + e.getMessage());
        }
    }

    private HttpHeaders crearHeadersConAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = extraerTokenActual();
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        return headers;
    }

    private String extraerTokenActual() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String auth = request.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                return auth.substring(7);
            }
        }
        return null;
    }

    private HttpEntity<?> crearRequestConToken() {
        return new HttpEntity<>(crearHeadersConAuth());
    }

    private <T> HttpEntity<T> crearRequestConJson(T body) {
        return new HttpEntity<>(body, crearHeadersConAuth());
    }
}
