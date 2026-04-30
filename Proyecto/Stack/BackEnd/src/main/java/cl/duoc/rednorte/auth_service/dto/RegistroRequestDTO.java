package cl.duoc.rednorte.auth_service.dto;

import cl.duoc.rednorte.datos_clinicos.model.DatosClinicos;
import lombok.Data;

@Data
public class RegistroRequestDTO {
    private String rut;
    private String nombre;
    private String email;
    private String contrasena;
    private String prevision;
    private DatosClinicos datosClinicosSensibles;
}