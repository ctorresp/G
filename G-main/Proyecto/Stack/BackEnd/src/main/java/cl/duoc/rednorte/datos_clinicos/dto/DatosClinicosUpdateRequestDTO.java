package cl.duoc.rednorte.datos_clinicos.dto;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class DatosClinicosUpdateRequestDTO {
    private String prevision;
    private String email; 
    
    @Valid
    private DatosClinicosDTO datosClinicosSensibles;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}