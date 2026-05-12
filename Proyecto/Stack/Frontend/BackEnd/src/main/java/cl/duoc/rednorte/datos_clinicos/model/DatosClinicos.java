package cl.duoc.rednorte.datos_clinicos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosClinicos {
    private String grupoSanguineo;
    private List<String> alergias = new ArrayList<>();
    private List<String> enfermedadesCronicas = new ArrayList<>();
    private Double pesoKg;
    private Double alturaCm;
}