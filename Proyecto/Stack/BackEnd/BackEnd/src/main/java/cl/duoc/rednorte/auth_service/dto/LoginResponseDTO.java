package cl.duoc.rednorte.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO de respuesta tras un login exitoso.
 * Contiene el token JWT y la información básica del usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    /** Token JWT firmado para autorización de peticiones */
    private String token;

    /** Tipo de token (siempre "Bearer") */
    private String tipoToken = "Bearer";

    /** ID del usuario autenticado */
    private Long idUsuario;

    /** Nombre del usuario */
    private String nombre;

    /** Email del usuario */
    private String email;

    /** Roles asignados al usuario (RBAC) */
    private Set<String> roles;

    /** Tiempo de expiración del token en milisegundos */
    private Long expiracion;
}
