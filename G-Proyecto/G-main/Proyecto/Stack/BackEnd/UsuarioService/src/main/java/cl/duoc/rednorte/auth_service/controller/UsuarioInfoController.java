package cl.duoc.rednorte.auth_service.controller;

import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.service.MedicoService;
import cl.duoc.rednorte.usuarios.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/usuario")
public class UsuarioInfoController {

    private final UsuarioService usuarioService;
    private final MedicoService medicoService;

    public UsuarioInfoController(UsuarioService usuarioService, MedicoService medicoService) {
        this.usuarioService = usuarioService;
        this.medicoService = medicoService;
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<?> obtenerPorRut(@PathVariable String rut) {
        return usuarioService.buscarPorRut(rut)
                .map(u -> ResponseEntity.ok(Map.of(
                        "rut", u.getRut(),
                        "nombre", u.getNombre(),
                        "email", u.getEmail(),
                        "rol", u.getRol().getNombreRol()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/medico/perfil")
    public ResponseEntity<?> obtenerPerfilMedico() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmailActivo(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        var medico = medicoService.obtenerOCrear(usuario);

        return ResponseEntity.ok(Map.of(
                "idUsuario", medico.getIdUsuario(),
                "rut", usuario.getRut(),
                "nombre", usuario.getNombre(),
                "email", usuario.getEmail(),
                "especialidadId", medico.getEspecialidad().getIdEspecialidad(),
                "especialidadNombre", medico.getEspecialidad().getNombre()
        ));
    }
}
