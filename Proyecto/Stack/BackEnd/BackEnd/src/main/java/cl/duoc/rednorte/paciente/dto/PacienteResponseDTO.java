package cl.duoc.rednorte.paciente.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import cl.duoc.rednorte.datos_clinicos.model.DatosClinicos;

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