package cl.duoc.rednorte.reasignacion.controller;

import cl.duoc.rednorte.reasignacion.dto.ReasignacionDTO;
import cl.duoc.rednorte.reasignacion.service.ReasignacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reasignaciones")
public class ReasignacionController {

    private final ReasignacionService service;

    public ReasignacionController(ReasignacionService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ReasignacionDTO>> listarTodas() {
        List<ReasignacionDTO> dtos = service.listarTodas().stream()
                .map(ReasignacionDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/lista-espera")
    @PreAuthorize("hasAnyAuthority('ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> listarListaEspera(
            @RequestParam(required = false) Long especialidadId) {
        return ResponseEntity.ok(service.listarListaEspera(especialidadId));
    }

    @PutMapping("/no-apto/{cirugiaId}")
    @PreAuthorize("hasAnyAuthority('ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> marcarNoApto(@PathVariable Long cirugiaId) {
        Map<String, Object> resultado = service.marcarNoAptoYReasignar(cirugiaId);
        return ResponseEntity.ok(resultado);
    }
}
