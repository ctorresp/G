package cl.duoc.rednorte.pabellon.dto;

import cl.duoc.rednorte.pabellon.model.Triaje;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TriajeDTO {
    private Long idTriaje;
    private String pacienteRut;
    private String triajerRut;
    private Integer nivelUrgencia;
    private String sintomas;
    private String presionArterial;
    private Integer frecuenciaCardiaca;
    private BigDecimal temperatura;
    private String observaciones;
    private LocalDateTime fechaTriaje;

    public static TriajeDTO fromEntity(Triaje t) {
        TriajeDTO dto = new TriajeDTO();
        dto.setIdTriaje(t.getIdTriaje());
        dto.setPacienteRut(t.getPacienteRut());
        dto.setTriajerRut(t.getTriajerRut());
        dto.setNivelUrgencia(t.getNivelUrgencia());
        dto.setSintomas(t.getSintomas());
        dto.setPresionArterial(t.getPresionArterial());
        dto.setFrecuenciaCardiaca(t.getFrecuenciaCardiaca());
        dto.setTemperatura(t.getTemperatura());
        dto.setObservaciones(t.getObservaciones());
        dto.setFechaTriaje(t.getFechaTriaje());
        return dto;
    }

    public Long getIdTriaje() { return idTriaje; }
    public void setIdTriaje(Long id) { this.idTriaje = id; }
    public String getPacienteRut() { return pacienteRut; }
    public void setPacienteRut(String r) { this.pacienteRut = r; }
    public String getTriajerRut() { return triajerRut; }
    public void setTriajerRut(String r) { this.triajerRut = r; }
    public Integer getNivelUrgencia() { return nivelUrgencia; }
    public void setNivelUrgencia(Integer n) { this.nivelUrgencia = n; }
    public String getSintomas() { return sintomas; }
    public void setSintomas(String s) { this.sintomas = s; }
    public String getPresionArterial() { return presionArterial; }
    public void setPresionArterial(String p) { this.presionArterial = p; }
    public Integer getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(Integer f) { this.frecuenciaCardiaca = f; }
    public BigDecimal getTemperatura() { return temperatura; }
    public void setTemperatura(BigDecimal t) { this.temperatura = t; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String o) { this.observaciones = o; }
    public LocalDateTime getFechaTriaje() { return fechaTriaje; }
    public void setFechaTriaje(LocalDateTime f) { this.fechaTriaje = f; }
}
