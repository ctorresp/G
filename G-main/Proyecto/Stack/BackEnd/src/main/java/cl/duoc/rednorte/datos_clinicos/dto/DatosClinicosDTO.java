package cl.duoc.rednorte.datos_clinicos.dto;

import cl.duoc.rednorte.datos_clinicos.model.DatosClinicos;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
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

    @DecimalMin(value = "0.1", message = "El peso debe ser mayor a 0")
    @DecimalMax(value = "999.9", message = "El peso debe ser menor a 1000")
    @Digits(integer = 3, fraction = 1, message = "El peso solo acepta hasta 1 decimal")
    private Double pesoKg;

    @Min(value = 2, message = "La altura mínima es de 2 cm")
    @Max(value = 250, message = "La altura máxima es de 250 cm")
    @Digits(integer = 3, fraction = 0, message = "La altura debe ser un número entero (sin decimales)")
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