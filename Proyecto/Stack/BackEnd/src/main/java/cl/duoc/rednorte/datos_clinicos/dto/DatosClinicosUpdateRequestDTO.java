package cl.duoc.rednorte.datos_clinicos.dto;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class DatosClinicosUpdateRequestDTO {
    private String prevision;
    
    @Valid
    private DatosClinicosDTO datosClinicosSensibles;
}