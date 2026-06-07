package cl.duoc.rednorte.paciente.controller;

import cl.duoc.rednorte.paciente.dto.PacienteResponseDTO;
import cl.duoc.rednorte.paciente.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO', 'ROLE_TRIAJER', 'ROLE_COORDINADOR')")
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

    @GetMapping("/por-medico/{idMedico}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO')")
    public ResponseEntity<?> listarPorMedico(@PathVariable Long idMedico) {
        try {
            var pacientes = pacienteService.listarPorMedico(idMedico);
            return ResponseEntity.ok(pacientes);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al listar pacientes del médico"));
        }
    }

    @GetMapping("/por-medico-rut/{rutMedico}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO')")
    public ResponseEntity<?> listarPorMedicoRut(@PathVariable String rutMedico) {
        try {
            var pacientes = pacienteService.listarPorMedicoRut(rutMedico);
            return ResponseEntity.ok(pacientes);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al listar pacientes del médico"));
        }
    }

    @GetMapping("/rut/{rut}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO', 'ROLE_TRIAJER', 'ROLE_COORDINADOR')")
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

    @PutMapping("/rut/{rut}/clinicos")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO')")
    public ResponseEntity<?> actualizarDatosClinicos(
            @PathVariable String rut,
            @RequestBody Map<String, Object> body) {
        try {
            String prevision = (String) body.get("prevision");
            String email = (String) body.get("email");
            @SuppressWarnings("unchecked")
            Map<String, Object> dc = (Map<String, Object>) body.get("datosClinicosSensibles");

            String grupoSanguineo = dc != null ? (String) dc.get("grupoSanguineo") : null;
            Double pesoKg = dc != null ? dc.get("pesoKg") != null ? ((Number) dc.get("pesoKg")).doubleValue() : null : null;
            Double alturaCm = dc != null ? dc.get("alturaCm") != null ? ((Number) dc.get("alturaCm")).doubleValue() : null : null;
            @SuppressWarnings("unchecked")
            List<String> alergiasList = dc != null ? (List<String>) dc.get("alergias") : null;
            @SuppressWarnings("unchecked")
            List<String> enfermedadesList = dc != null ? (List<String>) dc.get("enfermedadesCronicas") : null;

            String alergias = alergiasList != null ? String.join(",", alergiasList) : null;
            String enfermedades = enfermedadesList != null ? String.join(",", enfermedadesList) : null;

            PacienteResponseDTO actualizado = pacienteService.actualizarDatosClinicos(
                    rut, prevision, email, grupoSanguineo, pesoKg, alturaCm, alergias, enfermedades
            );
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al actualizar"));
        }
    }

    @DeleteMapping("/rut/{rut}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable String rut) {
        try {
            String mensaje = pacienteService.eliminarPaciente(rut);
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al eliminar"));
        }
    }

    @PutMapping("/rut/{rut}/observacion")
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO')")
    public ResponseEntity<?> actualizarObservacion(
            @PathVariable String rut,
            @RequestBody Map<String, String> body) {
        try {
            String observacion = body.getOrDefault("observacionMedico", "");
            var actualizado = pacienteService.actualizarObservacion(rut, observacion);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al actualizar observación"));
        }
    }

    @PutMapping("/rut/{rut}/contacto")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO')")
    public ResponseEntity<?> actualizarContactoEmergencia(
            @PathVariable String rut,
            @RequestBody Map<String, Object> body) {
        try {
            Long idMedico = body.get("idMedico") != null
                    ? Long.valueOf(body.get("idMedico").toString()) : null;
            String contactoNombre = (String) body.get("contactoEmergenciaNombre");
            String contactoTelefono = (String) body.get("contactoEmergenciaTelefono");

            PacienteResponseDTO actualizado = pacienteService.actualizarContactoEmergencia(
                    rut, idMedico, contactoNombre, contactoTelefono);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al actualizar contacto"));
        }
    }
}
