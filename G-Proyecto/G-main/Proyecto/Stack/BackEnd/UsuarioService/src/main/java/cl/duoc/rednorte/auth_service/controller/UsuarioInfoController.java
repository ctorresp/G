package cl.duoc.rednorte.auth_service.controller;

import cl.duoc.rednorte.usuarios.model.Especialidad;
import cl.duoc.rednorte.usuarios.model.Medico;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.EspecialidadRepository;
import cl.duoc.rednorte.usuarios.repository.MedicoRepository;
import cl.duoc.rednorte.usuarios.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/usuario")
public class UsuarioInfoController {

    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;

    public UsuarioInfoController(UsuarioRepository usuarioRepository, MedicoRepository medicoRepository,
            EspecialidadRepository especialidadRepository) {
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
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

    @GetMapping("/medico/perfil")
    public ResponseEntity<?> obtenerPerfilMedico() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmailAndEstadoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Medico medico = medicoRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .orElseGet(() -> {
                    Especialidad esp = especialidadRepository.findByNombre("Cirugía General")
                            .orElseThrow(() -> new RuntimeException("Especialidad por defecto no encontrada"));
                    Medico nuevo = new Medico();
                    nuevo.setUsuario(usuario);
                    nuevo.setEspecialidad(esp);
                    return medicoRepository.save(nuevo);
                });

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
