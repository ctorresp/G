package cl.duoc.rednorte.auth_service.dto;

import lombok.Data;

@Data
public class RegistroRequestDTO {
    private String rut;
    private String nombre;
    private String email;
    private String contrasena;
    private String prevision;
    private String grupoSanguineo;
    private Double pesoKg;
    private Double alturaCm;
    private String alergias;
    private String enfermedadesCronicas;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    private Long idMedico;
}
