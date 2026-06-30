package cl.duoc.rednorte.pabellon.controller;

import cl.duoc.rednorte.pabellon.dto.CirugiaDTO;
import cl.duoc.rednorte.pabellon.model.Cirugia;
import cl.duoc.rednorte.pabellon.service.CirugiaService;
import cl.duoc.rednorte.pabellon.service.PabellonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pabellon/cirugias")
public class CirugiaController {

    private final CirugiaService service;
    private final PabellonService pabellonService;

    public CirugiaController(CirugiaService service, PabellonService pabellonService) {
        this.service = service;
        this.pabellonService = pabellonService;
    }

    @GetMapping("/pendientes-triaje")
    @PreAuthorize("hasAnyAuthority('ROLE_TRIAJER', 'ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<CirugiaDTO>> listarPendientesTriaje() {
        List<CirugiaDTO> dtos = service.listarPendientesTriaje().stream()
                .map(CirugiaDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/triaje-completado")
    @PreAuthorize("hasAuthority('ROLE_TRIAJER')")
    public ResponseEntity<Void> marcarTriajeCompletado(@PathVariable Long id) {
        service.marcarTriajeCompletado(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/solicitadas")
    @PreAuthorize("hasAnyAuthority('ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<CirugiaDTO>> listarSolicitadas() {
        List<CirugiaDTO> dtos = service.listarSolicitadas().stream()
                .map(CirugiaDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/solicitar")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO', 'ROLE_COORDINADOR')")
    public ResponseEntity<CirugiaDTO> solicitar(@RequestBody Cirugia cirugia) {
        return ResponseEntity.ok(CirugiaDTO.fromEntity(service.solicitar(cirugia)));
    }

    @PutMapping("/{id}/programar")
    @PreAuthorize("hasAnyAuthority('ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<CirugiaDTO> programar(@PathVariable Long id,
                                                  @RequestBody Map<String, Object> body) {
        Long pabellonId = Long.valueOf(body.get("pabellonId").toString());
        String fechaStr = (String) body.get("fechaProgramada");
        String horaInicioStr = (String) body.get("horaInicio");
        String horaFinStr = (String) body.get("horaFin");
        var pabellon = pabellonService.obtenerPorId(pabellonId);
        Cirugia resultado = service.programar(id, pabellon,
                java.time.LocalDate.parse(fechaStr),
                java.time.LocalTime.parse(horaInicioStr),
                java.time.LocalTime.parse(horaFinStr));
        return ResponseEntity.ok(CirugiaDTO.fromEntity(resultado));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_COORDINADOR')")
    public ResponseEntity<List<CirugiaDTO>> listarTodas() {
        List<CirugiaDTO> dtos = service.listarTodas().stream()
                .map(CirugiaDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CirugiaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(CirugiaDTO.fromEntity(service.obtenerPorId(id)));
    }

    @GetMapping("/medico/{rut}")
    public ResponseEntity<List<CirugiaDTO>> listarPorMedico(@PathVariable String rut) {
        List<CirugiaDTO> dtos = service.listarPorMedico(rut).stream()
                .map(CirugiaDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/paciente/{rut}")
    public ResponseEntity<List<CirugiaDTO>> listarPorPaciente(@PathVariable String rut) {
        List<CirugiaDTO> dtos = service.listarPorPaciente(rut).stream()
                .map(CirugiaDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/pacientes-con-cirugia")
    public ResponseEntity<List<String>> listarPacientesConCirugiaActiva() {
        return ResponseEntity.ok(service.listarPacientesConCirugiaActiva());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<CirugiaDTO> crear(@RequestBody Cirugia cirugia) {
        return ResponseEntity.ok(CirugiaDTO.fromEntity(service.crear(cirugia)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<CirugiaDTO> actualizar(@PathVariable Long id, @RequestBody Cirugia datos) {
        return ResponseEntity.ok(CirugiaDTO.fromEntity(service.actualizar(id, datos)));
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyAuthority('ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String motivo = body != null ? body.getOrDefault("motivo", "CANCELADO_POR_ADMIN") : "CANCELADO_POR_ADMIN";
        service.cancelar(id, motivo);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/no-show")
    @PreAuthorize("hasAnyAuthority('ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<Void> marcarNoShow(@PathVariable Long id) {
        service.marcarNoShow(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/completar")
    @PreAuthorize("hasAnyAuthority('ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<Void> completar(@PathVariable Long id) {
        service.completar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reasignar")
    @PreAuthorize("hasAnyAuthority('ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<CirugiaDTO> reasignar(@PathVariable Long id,
                                                  @RequestBody Map<String, String> body) {
        String nuevoPacienteRut = body.get("nuevoPacienteRut");
        String adminRut = body.get("adminRut");
        String motivo = body.get("motivo");
        Cirugia resultado = service.reasignar(id, nuevoPacienteRut, adminRut, motivo);
        return ResponseEntity.ok(CirugiaDTO.fromEntity(resultado));
    }

}
