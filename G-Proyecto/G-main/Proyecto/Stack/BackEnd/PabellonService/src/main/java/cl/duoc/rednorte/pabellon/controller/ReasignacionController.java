package cl.duoc.rednorte.pabellon.controller;

import cl.duoc.rednorte.pabellon.dto.ReasignacionDTO;
import cl.duoc.rednorte.pabellon.service.ReasignacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pabellon/reasignaciones")
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
}
