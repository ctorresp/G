package cl.duoc.rednorte.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO de respuesta para datos de un paciente.
 * No incluye la contraseña ni datos internos de seguridad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponseDTO {

    // ── Datos del paciente ────────────────────
    private Long idPaciente;
    private String prevision;
    private String datosClinicosSensibles;

    // ── Datos del usuario asociado ────────────
    private Long idUsuario;
    private String rut;
    private String nombre;
    private String email;
    private Boolean estado;
    private Set<String> roles;
}
