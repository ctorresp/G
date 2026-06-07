package cl.duoc.rednorte.auth_service.dto;

import lombok.Data;

@Data
public class MedicoUpdateRequestDTO {
    private String rut;
    private String nombre;
    private String email;
}
