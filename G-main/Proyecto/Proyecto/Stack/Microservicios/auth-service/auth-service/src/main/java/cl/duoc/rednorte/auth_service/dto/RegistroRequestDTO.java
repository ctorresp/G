package cl.duoc.rednorte.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud de registro de nuevo usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroRequestDTO {

    private String rut;
    private String nombre;
    private String email;
    private String contrasena;

    /** Prevision del paciente (FONASA, ISAPRE, etc.) — opcional */
    private String prevision;

    /** Datos clínicos sensibles — opcional, solo para rol PACIENTE */
    private String datosClinicosSensibles;
}
