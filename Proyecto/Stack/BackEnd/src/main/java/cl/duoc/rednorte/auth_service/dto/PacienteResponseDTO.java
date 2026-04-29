package cl.duoc.rednorte.auth_service.dto;

import cl.duoc.rednorte.auth_service.model.DatosClinicos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponseDTO {
    private Long idPaciente;
    private String prevision;
    private DatosClinicos datosClinicosSensibles;
    private Long idUsuario;
    private String rut;
    private String nombre;
    private String email;
    private Boolean estado;
    private Set<String> roles;
}