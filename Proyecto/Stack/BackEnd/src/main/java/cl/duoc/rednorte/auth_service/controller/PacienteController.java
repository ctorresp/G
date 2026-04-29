package cl.duoc.rednorte.auth_service.controller;

import cl.duoc.rednorte.auth_service.dto.DatosClinicosUpdateRequestDTO;
import cl.duoc.rednorte.auth_service.dto.PacienteResponseDTO;
import cl.duoc.rednorte.auth_service.model.Usuario;
import cl.duoc.rednorte.auth_service.repository.UsuarioRepository;
import cl.duoc.rednorte.auth_service.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de pacientes.
 *
 * Base URL: /api/pacientes
 *
 * Todos los endpoints requieren autenticación JWT.
 * Los roles requeridos están indicados en cada método.
 */
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ─────────────────────────────────────────────
    // GET — LISTADO Y BÚSQUEDAS
    // ─────────────────────────────────────────────

    /**
     * Lista todos los pacientes registrados en el sistema.
     * Solo accesible por administradores y médicos.
     *
     * GET /api/pacientes
     *
     * @return 200 con lista de PacienteResponseDTO
     */
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

    /**
     * Obtiene la ficha del paciente autenticado usando su propio Token.
     * No requiere pasar IDs, extrae el usuario directamente del JWT.
     *
     * GET /api/pacientes/me
     *
     * @param authentication objeto de autenticación
     * @return 200 con PacienteResponseDTO
     */
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

    /**
     * Obtiene un paciente por su ID de paciente.
     * - ROLE_ADMIN y ROLE_MEDICO pueden ver cualquier ficha.
     * - ROLE_PACIENTE solo puede ver su propia ficha.
     *
     * GET /api/pacientes/{id}
     *
     * @param id             ID del paciente
     * @param authentication objeto de autenticación inyectado por Spring Security
     * @return 200 con PacienteResponseDTO, 403 si el paciente intenta ver otra ficha, 404 si no existe
     */
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

    /**
     * Obtiene la ficha del paciente asociado a un usuario por su ID de usuario.
     * - ROLE_ADMIN y ROLE_MEDICO pueden buscar cualquier usuario.
     * - ROLE_PACIENTE solo puede consultar su propio idUsuario.
     *
     * GET /api/pacientes/usuario/{idUsuario}
     *
     * @param idUsuario      ID del usuario
     * @param authentication objeto de autenticación inyectado por Spring Security
     * @return 200 con PacienteResponseDTO, 403 si el paciente intenta ver otra ficha, 404 si no existe
     */
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

    /**
     * Busca un paciente por RUT del usuario.
     *
     * GET /api/pacientes/rut/{rut}
     * Ejemplo: GET /api/pacientes/rut/11111111-1
     *
     * @param rut RUT del usuario (con dígito verificador)
     * @return 200 con PacienteResponseDTO o 404 si no existe
     */
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

    // ─────────────────────────────────────────────
    // PUT — ACTUALIZACIÓN DE DATOS CLÍNICOS
    // ─────────────────────────────────────────────

    /**
     * Actualiza la previsión y/o datos clínicos sensibles de un paciente.
     * Solo accesible por administradores y médicos.
     *
     * PUT /api/pacientes/{id}/clinicos
     * Body (campos opcionales, se actualiza solo lo que se envíe):
     * {
     *   "prevision": "ISAPRE",
     *   "datosClinicosSensibles": "{...}"
     * }
     *
     * @param id      ID del paciente
     * @param body    mapa con prevision y/o datosClinicosSensibles
     * @return 200 con PacienteResponseDTO actualizado o 404/400 con error
     */
    @PutMapping("/{id}/clinicos")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO')")
    public ResponseEntity<?> actualizarDatosClinicos(
            @PathVariable Long id,
            @RequestBody DatosClinicosUpdateRequestDTO request) {
        try {
            PacienteResponseDTO actualizado = pacienteService.actualizarDatosClinicos(
                    id,
                    request.getPrevision(),
                    request.getDatosClinicosSensibles()
            );
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al actualizar"));
        }
    }

    // ─────────────────────────────────────────────
    // PATCH — ACTIVAR / DESACTIVAR CUENTA
    // ─────────────────────────────────────────────

    /**
     * Desactiva la cuenta de un paciente (bloquea acceso al sistema).
     * Solo accesible por administradores.
     *
     * PATCH /api/pacientes/{id}/desactivar
     *
     * @param id ID del paciente
     * @return 200 con mensaje de confirmación o 404 con error
     */
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

    /**
     * Reactiva la cuenta de un paciente desactivado.
     * Solo accesible por administradores.
     *
     * PATCH /api/pacientes/{id}/activar
     *
     * @param id ID del paciente
     * @return 200 con mensaje de confirmación o 404 con error
     */
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
    // ─────────────────────────────────────────────
    // UTILIDADES PRIVADAS
    // ─────────────────────────────────────────────

    /**
     * Verifica si el usuario autenticado SOLO tiene ROLE_PACIENTE
     * (es decir, no tiene ROLE_ADMIN ni ROLE_MEDICO).
     *
     * @param authentication objeto de autenticación de Spring Security
     * @return true si es exclusivamente paciente
     */
    private boolean esSoloPaciente(Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean esMedico = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MEDICO"));
        return !esAdmin && !esMedico;
    }

    /**
     * Obtiene el ID del usuario autenticado a partir del email en el JWT.
     *
     * @param authentication objeto de autenticación de Spring Security
     * @return ID del usuario autenticado
     * @throws RuntimeException si el usuario no se encuentra en la BD
     */
    private Long getIdUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmailAndEstadoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        return usuario.getIdUsuario();
    }
}
