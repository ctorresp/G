package cl.duoc.rednorte.datos_clinicos.dto;

import cl.duoc.rednorte.datos_clinicos.model.DatosClinicos;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosClinicosDTO {

    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Grupo sanguíneo inválido (ej. O+, AB-)")
    private String grupoSanguineo;

    private List<String> alergias = new ArrayList<>();

    private List<String> enfermedadesCronicas = new ArrayList<>();

    @Min(value = 0, message = "El peso no puede ser negativo")
    private Double pesoKg;

    @Min(value = 0, message = "La altura no puede ser negativa")
    private Double alturaCm;

    // Método de utilidad para convertir este DTO al Modelo de base de datos
    public DatosClinicos toModel() {
        return new DatosClinicos(
            this.grupoSanguineo,
            this.alergias != null ? this.alergias : new ArrayList<>(),
            this.enfermedadesCronicas != null ? this.enfermedadesCronicas : new ArrayList<>(),
            this.pesoKg,
            this.alturaCm
        );
    }
}