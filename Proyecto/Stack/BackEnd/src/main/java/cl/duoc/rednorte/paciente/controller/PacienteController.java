package cl.duoc.rednorte.paciente.controller;

import cl.duoc.rednorte.auth_service.model.Usuario;
import cl.duoc.rednorte.auth_service.repository.UsuarioRepository;
import cl.duoc.rednorte.datos_clinicos.dto.DatosClinicosUpdateRequestDTO;
import cl.duoc.rednorte.paciente.dto.PacienteResponseDTO;
import cl.duoc.rednorte.datos_clinicos.model.DatosClinicos;
import cl.duoc.rednorte.paciente.service.PacienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Controlador REST para gestión de pacientes. Requiere autenticación JWT.
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO')")
    public ResponseEntity<?> listarTodos() {
        try {
            List<PacienteResponseDTO> pacientes = pacienteService.listarTodos();
            return ResponseEntity.ok(pacientes);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error interno al cargar la lista de pacientes.",
                            "detalle", e.getMessage() != null ? e.getMessage() : "Error desconocido"
                    ));
        }
    }

    // Obtiene la ficha del paciente autenticado extrayendo sus datos directamente del Token JWT
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_PACIENTE')")
    public ResponseEntity<?> obtenerMiFicha(Authentication authentication) {
        try {
            Long idUsuarioToken = getIdUsuarioAutenticado(authentication);
            PacienteResponseDTO paciente = pacienteService.buscarPorIdUsuario(idUsuarioToken);
            return ResponseEntity.ok(paciente);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al obtener ficha"));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, Authentication authentication) {
        try {
            PacienteResponseDTO paciente = pacienteService.buscarPorId(id);

            // Si es ROLE_PACIENTE, verificar que solo acceda a su propia ficha
            if (esSoloPaciente(authentication)) {
                Long idUsuarioToken = getIdUsuarioAutenticado(authentication);
                if (!paciente.getIdUsuario().equals(idUsuarioToken)) {
                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "No tienes permiso para ver la ficha de otro paciente"));
                }
            }

            return ResponseEntity.ok(paciente);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al buscar paciente"));
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO', 'ROLE_PACIENTE')")
    public ResponseEntity<?> buscarPorIdUsuario(@PathVariable Long idUsuario, Authentication authentication) {
        try {
            // Si es ROLE_PACIENTE, solo puede consultar su propio idUsuario
            if (esSoloPaciente(authentication)) {
                Long idUsuarioToken = getIdUsuarioAutenticado(authentication);
                if (!idUsuario.equals(idUsuarioToken)) {
                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Solo puedes consultar tu propia ficha"));
                }
            }

            PacienteResponseDTO paciente = pacienteService.buscarPorIdUsuario(idUsuario);
            return ResponseEntity.ok(paciente);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al buscar usuario"));
        }
    }

    @GetMapping("/rut/{rut}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO')")
    public ResponseEntity<?> buscarPorRut(@PathVariable String rut) {
        try {
            PacienteResponseDTO paciente = pacienteService.buscarPorRut(rut);
            return ResponseEntity.ok(paciente);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al buscar rut"));
        }
    }

    // Actualiza datos clínicos (los campos que no se envíen en el request se mantienen igual)
    @PutMapping("/{id}/clinicos")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO')")
    public ResponseEntity<?> actualizarDatosClinicos(
            @PathVariable Long id,
            @Valid @RequestBody DatosClinicosUpdateRequestDTO request) {
        try {
            DatosClinicos datosModel = null;
            if (request.getDatosClinicosSensibles() != null) {
                datosModel = request.getDatosClinicosSensibles().toModel();
            }

            PacienteResponseDTO actualizado = pacienteService.actualizarDatosClinicos(
                    id,
                    request.getPrevision(),
                    datosModel
            );
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al actualizar"));
        }
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        try {
            String mensaje = pacienteService.desactivarPaciente(id);
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al desactivar"));
        }
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> activar(@PathVariable Long id) {
        try {
            String mensaje = pacienteService.activarPaciente(id);
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al activar"));
        }
    }

    // Verifica si el usuario autenticado tiene exclusivamente el rol de paciente
    private boolean esSoloPaciente(Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean esMedico = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MEDICO"));
        return !esAdmin && !esMedico;
    }

    private Long getIdUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmailAndEstadoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        return usuario.getIdUsuario();
    }
}
