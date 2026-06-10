package cl.duoc.rednorte.usuarios.controller;

import cl.duoc.rednorte.usuarios.dto.TriajerRequestDTO;
import cl.duoc.rednorte.usuarios.dto.TriajerResponseDTO;
import cl.duoc.rednorte.usuarios.dto.UsuarioRequestDTO;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.service.TriajerService;
import cl.duoc.rednorte.usuarios.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios/triajers")
public class TriajerController {

    private final TriajerService triajerService;
    private final UsuarioService usuarioService;

    public TriajerController(TriajerService triajerService, UsuarioService usuarioService) {
        this.triajerService = triajerService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<TriajerResponseDTO>> listarTodos() {
        return ResponseEntity.ok(triajerService.listarTodos());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> crear(@RequestBody TriajerRequestDTO request) {
        try {
            UsuarioRequestDTO userReq = new UsuarioRequestDTO();
            userReq.setRut(request.getRut());
            userReq.setNombre(request.getNombre());
            userReq.setEmail(request.getEmail());
            userReq.setContrasena(request.getContrasena());
            Usuario usuario = usuarioService.crear(userReq, "ROLE_TRIAJER");
            var triajer = triajerService.crear(usuario, request.getCertificaciones());
            return ResponseEntity.status(HttpStatus.CREATED).body(TriajerResponseDTO.fromEntity(triajer));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
