package cl.duoc.rednorte.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String tipoToken = "Bearer";
    private Long idUsuario;
    private String rut;
    private String nombre;
    private String email;
    private String rol;
    private Long expiracion;
}
