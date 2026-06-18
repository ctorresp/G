package cl.duoc.rednorte.pabellon.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "triajes")
public class Triaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_triaje")
    private Long idTriaje;

    @Column(name = "paciente_rut", nullable = false, length = 12)
    private String pacienteRut;

    @Column(name = "triajer_rut", nullable = false, length = 12)
    private String triajerRut;

    @Column(name = "nivel_urgencia", nullable = false)
    private Integer nivelUrgencia;

    @Column(columnDefinition = "TEXT")
    private String sintomas;

    @Column(name = "presion_arterial", length = 20)
    private String presionArterial;

    @Column(name = "frecuencia_cardiaca")
    private Integer frecuenciaCardiaca;

    @Column(precision = 4, scale = 1)
    private BigDecimal temperatura;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_triaje", nullable = false)
    private LocalDateTime fechaTriaje;

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
