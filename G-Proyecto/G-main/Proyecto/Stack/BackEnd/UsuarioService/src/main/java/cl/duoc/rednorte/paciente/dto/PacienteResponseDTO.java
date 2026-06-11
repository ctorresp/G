package cl.duoc.rednorte.paciente.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponseDTO {
    private Long idPaciente;
    private String rut;
    private String nombre;
    private String email;
    private String prevision;
    private Long idMedicoAsignado;
    private String nombreMedicoAsignado;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    private String observacionMedico;
    private Map<String, Object> datosClinicosSensibles;
    private Boolean activo;
    private String especialidadNombre;
}
