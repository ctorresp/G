package cl.duoc.rednorte.usuarios.controller;

import cl.duoc.rednorte.usuarios.dto.EspecialidadResponseDTO;
import cl.duoc.rednorte.usuarios.service.EspecialidadService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/especialidades")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO', 'ROLE_COORDINADOR')")
    public ResponseEntity<List<EspecialidadResponseDTO>> listarTodas() {
        return ResponseEntity.ok(especialidadService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO', 'ROLE_COORDINADOR')")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return especialidadService.buscarPorId(id)
                .map(e -> ResponseEntity.ok(EspecialidadResponseDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO', 'ROLE_COORDINADOR')")
    public ResponseEntity<?> obtenerPorNombre(@PathVariable String nombre) {
        return especialidadService.buscarPorNombre(nombre)
                .map(e -> ResponseEntity.ok(EspecialidadResponseDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }
}
