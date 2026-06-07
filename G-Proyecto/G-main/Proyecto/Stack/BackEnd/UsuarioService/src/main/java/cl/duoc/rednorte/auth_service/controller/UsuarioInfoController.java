package cl.duoc.rednorte.auth_service.controller;

import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/usuario")
public class UsuarioInfoController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioInfoController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<?> obtenerPorRut(@PathVariable String rut) {
        return usuarioRepository.findByRut(rut)
                .map(u -> ResponseEntity.ok(Map.of(
                        "rut", u.getRut(),
                        "nombre", u.getNombre(),
                        "email", u.getEmail(),
                        "rol", u.getRol().getNombreRol()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}
