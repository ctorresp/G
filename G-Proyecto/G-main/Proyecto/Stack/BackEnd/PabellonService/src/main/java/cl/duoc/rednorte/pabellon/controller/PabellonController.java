package cl.duoc.rednorte.pabellon.controller;

import cl.duoc.rednorte.pabellon.model.EstadoPabellon;
import cl.duoc.rednorte.pabellon.model.Pabellon;
import cl.duoc.rednorte.pabellon.service.PabellonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pabellon/pabellones")
public class PabellonController {

    private final PabellonService service;

    public PabellonController(PabellonService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Pabellon>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Pabellon>> listarDisponibles() {
        return ResponseEntity.ok(service.listarDisponibles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pabellon> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_COORDINADOR')")
    public ResponseEntity<Pabellon> crear(@Valid @RequestBody Pabellon pabellon) {
        return ResponseEntity.ok(service.crear(pabellon));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<Pabellon> actualizar(@PathVariable Long id, @RequestBody Pabellon datos) {
        return ResponseEntity.ok(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyAuthority('ROLE_COORDINADOR', 'ROLE_ADMIN')")
    public ResponseEntity<Pabellon> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.cambiarEstado(id, EstadoPabellon.valueOf(body.get("estado"))));
    }
}
