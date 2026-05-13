package cl.duoc.rednorte.paciente.controller;

import cl.duoc.rednorte.datos_clinicos.dto.DatosClinicosUpdateRequestDTO;
import cl.duoc.rednorte.paciente.dto.PacienteResponseDTO;
import cl.duoc.rednorte.datos_clinicos.model.DatosClinicos;
import cl.duoc.rednorte.paciente.service.PacienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Controlador REST para gestión de pacientes. Requiere autenticación JWT.
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

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
    @PutMapping("/rut/{rut}/clinicos")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEDICO')")
    public ResponseEntity<?> actualizarDatosClinicos(
            @PathVariable String rut,
            @Valid @RequestBody DatosClinicosUpdateRequestDTO request) {
        try {
            DatosClinicos datosModel = null;
            if (request.getDatosClinicosSensibles() != null) {
                datosModel = request.getDatosClinicosSensibles().toModel();
            }

            PacienteResponseDTO actualizado = pacienteService.actualizarDatosClinicos(
                    rut,
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

    @PatchMapping("/rut/{rut}/desactivar")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> desactivar(@PathVariable String rut) {
        try {
            String mensaje = pacienteService.desactivarPaciente(rut);
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al desactivar"));
        }
    }

    @PatchMapping("/rut/{rut}/activar")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> activar(@PathVariable String rut) {
        try {
            String mensaje = pacienteService.activarPaciente(rut);
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al activar"));
        }
    }
}
