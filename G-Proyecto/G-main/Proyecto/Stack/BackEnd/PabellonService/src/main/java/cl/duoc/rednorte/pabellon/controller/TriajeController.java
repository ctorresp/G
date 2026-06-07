package cl.duoc.rednorte.pabellon.controller;

import cl.duoc.rednorte.pabellon.dto.TriajeDTO;
import cl.duoc.rednorte.pabellon.model.Triaje;
import cl.duoc.rednorte.pabellon.service.TriajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pabellon/triajes")
public class TriajeController {

    private final TriajeService service;

    public TriajeController(TriajeService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_TRIAJER')")
    public ResponseEntity<TriajeDTO> crear(@RequestBody Triaje triaje) {
        return ResponseEntity.ok(TriajeDTO.fromEntity(service.crear(triaje)));
    }

    @GetMapping("/paciente/{rut}")
    public ResponseEntity<List<TriajeDTO>> historial(@PathVariable String rut) {
        List<TriajeDTO> dtos = service.historialPorPaciente(rut).stream()
                .map(TriajeDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
