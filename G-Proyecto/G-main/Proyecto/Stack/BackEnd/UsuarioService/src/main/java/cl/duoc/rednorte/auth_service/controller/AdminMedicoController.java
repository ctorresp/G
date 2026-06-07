package cl.duoc.rednorte.auth_service.controller;

import cl.duoc.rednorte.auth_service.dto.MedicoUpdateRequestDTO;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.auth_service.service.AdminMedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/medicos")
public class AdminMedicoController {

    @Autowired
    private AdminMedicoService adminMedicoService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO', 'ROLE_COORDINADOR')")
    public ResponseEntity<List<Usuario>> listarMedicos() {
        return ResponseEntity.ok(adminMedicoService.listarMedicos());
    }

    @DeleteMapping("/rut/{rut}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable String rut) {
        try {
            String mensaje = adminMedicoService.eliminarMedico(rut);
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al eliminar"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> actualizarMedico(@PathVariable Long id, @RequestBody MedicoUpdateRequestDTO request) {
        try {
            Usuario actualizado = adminMedicoService.actualizarMedico(id, request);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Médico actualizado exitosamente",
                    "usuario", actualizado
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
