package cl.duoc.rednorte.usuarios.controller;

import cl.duoc.rednorte.usuarios.dto.MedicoRequestDTO;
import cl.duoc.rednorte.usuarios.dto.MedicoResponseDTO;
import cl.duoc.rednorte.usuarios.dto.UsuarioRequestDTO;
import cl.duoc.rednorte.usuarios.model.Medico;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.service.MedicoService;
import cl.duoc.rednorte.usuarios.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios/medicos")
public class MedicoController {

    private final MedicoService medicoService;
    private final UsuarioService usuarioService;

    public MedicoController(MedicoService medicoService, UsuarioService usuarioService) {
        this.medicoService = medicoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO', 'ROLE_COORDINADOR')")
    public ResponseEntity<List<MedicoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(medicoService.listarTodos());
    }

    @GetMapping("/rut/{rut}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO', 'ROLE_COORDINADOR')")
    public ResponseEntity<?> obtenerPorRut(@PathVariable String rut) {
        return medicoService.buscarPorRut(rut)
                .map(m -> ResponseEntity.ok(MedicoResponseDTO.fromEntity(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO', 'ROLE_COORDINADOR')")
    public ResponseEntity<?> obtenerPorIdUsuario(@PathVariable Long idUsuario) {
        return medicoService.buscarPorIdUsuario(idUsuario)
                .map(m -> ResponseEntity.ok(MedicoResponseDTO.fromEntity(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> crear(@RequestBody MedicoRequestDTO request) {
        try {
            UsuarioRequestDTO userReq = new UsuarioRequestDTO();
            userReq.setRut(request.getRut());
            userReq.setNombre(request.getNombre());
            userReq.setEmail(request.getEmail());
            userReq.setContrasena(request.getContrasena());
            Usuario usuario = usuarioService.crear(userReq, "ROLE_MEDICO");
            Medico medico = medicoService.crearConUsuario(usuario, request.getEspecialidadId());
            return ResponseEntity.status(HttpStatus.CREATED).body(MedicoResponseDTO.fromEntity(medico));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
