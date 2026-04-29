package cl.duoc.rednorte.auth_service.dto;

import cl.duoc.rednorte.auth_service.model.DatosClinicos;
import lombok.Data;

@Data
public class DatosClinicosUpdateRequestDTO {
    private String prevision;
    private DatosClinicos datosClinicosSensibles;
}