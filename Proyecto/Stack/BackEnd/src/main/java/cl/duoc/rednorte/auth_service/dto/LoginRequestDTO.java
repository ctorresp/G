package cl.duoc.rednorte.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud de login.
 * El usuario puede autenticarse con email o RUT + contraseña.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    /** Email del usuario (opción principal de login) */
    private String email;

    /** RUT del usuario (alternativa al email) */
    private String rut;

    /** Contraseña en texto plano (se verifica contra BCrypt en BD) */
    private String contrasena;
}
