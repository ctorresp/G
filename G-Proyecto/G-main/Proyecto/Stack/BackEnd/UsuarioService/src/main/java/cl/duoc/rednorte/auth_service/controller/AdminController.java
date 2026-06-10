package cl.duoc.rednorte.auth_service.controller;

import cl.duoc.rednorte.usuarios.dto.UsuarioResponseDTO;
import cl.duoc.rednorte.auth_service.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        try {
            Boolean nuevoEstado = body.get("estado");
            if (nuevoEstado == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Campo 'estado' requerido"));
            }
            UsuarioResponseDTO actualizado = adminService.cambiarEstadoUsuario(id, nuevoEstado);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Estado actualizado exitosamente",
                    "usuario", actualizado
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
