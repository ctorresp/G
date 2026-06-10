package cl.duoc.rednorte.usuarios.controller;

import cl.duoc.rednorte.usuarios.dto.CoordinadorRequestDTO;
import cl.duoc.rednorte.usuarios.dto.CoordinadorResponseDTO;
import cl.duoc.rednorte.usuarios.dto.UsuarioRequestDTO;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.service.CoordinadorService;
import cl.duoc.rednorte.usuarios.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios/coordinadores")
public class CoordinadorController {

    private final CoordinadorService coordinadorService;
    private final UsuarioService usuarioService;

    public CoordinadorController(CoordinadorService coordinadorService, UsuarioService usuarioService) {
        this.coordinadorService = coordinadorService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<CoordinadorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(coordinadorService.listarTodos());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> crear(@RequestBody CoordinadorRequestDTO request) {
        try {
            UsuarioRequestDTO userReq = new UsuarioRequestDTO();
            userReq.setRut(request.getRut());
            userReq.setNombre(request.getNombre());
            userReq.setEmail(request.getEmail());
            userReq.setContrasena(request.getContrasena());
            Usuario usuario = usuarioService.crear(userReq, "ROLE_COORDINADOR");
            var coordinador = coordinadorService.crear(usuario, request.getAreaAsignada());
            return ResponseEntity.status(HttpStatus.CREATED).body(CoordinadorResponseDTO.fromEntity(coordinador));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
