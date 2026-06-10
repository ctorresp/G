package cl.duoc.rednorte.pabellon.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "cirugias", indexes = {
    @Index(name = "idx_cirugias_paciente_rut", columnList = "paciente_rut"),
    @Index(name = "idx_cirugias_medico_rut", columnList = "medico_rut"),
    @Index(name = "idx_cirugias_estado", columnList = "estado")
})
public class Cirugia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cirugia")
    private Long idCirugia;

    @Column(name = "paciente_rut", nullable = false, length = 12)
    private String pacienteRut;

    @Column(name = "medico_rut", nullable = false, length = 12)
    private String medicoRut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pabellon_id", nullable = true)
    private Pabellon pabellon;

    @Column(name = "fecha_programada", nullable = true)
    private LocalDate fechaProgramada;

    @Column(name = "hora_inicio", nullable = true)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = true)
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCirugia estado;

    @Column(name = "motivo_cancelacion", length = 255)
    private String motivoCancelacion;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Column(name = "triaje_completado", nullable = false)
    private boolean triajeCompletado = false;

    public Long getIdCirugia() { return idCirugia; }
    public void setIdCirugia(Long id) { this.idCirugia = id; }
    public String getPacienteRut() { return pacienteRut; }
    public void setPacienteRut(String r) { this.pacienteRut = r; }
    public String getMedicoRut() { return medicoRut; }
    public void setMedicoRut(String r) { this.medicoRut = r; }
    public Especialidad getEspecialidad() { return especialidad; }
    public void setEspecialidad(Especialidad e) { this.especialidad = e; }
    public Pabellon getPabellon() { return pabellon; }
    public void setPabellon(Pabellon p) { this.pabellon = p; }
    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate f) { this.fechaProgramada = f; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime h) { this.horaInicio = h; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime h) { this.horaFin = h; }
    public EstadoCirugia getEstado() { return estado; }
    public void setEstado(EstadoCirugia e) { this.estado = e; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String m) { this.motivoCancelacion = m; }
    public LocalDateTime getFechaCancelacion() { return fechaCancelacion; }
    public void setFechaCancelacion(LocalDateTime f) { this.fechaCancelacion = f; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime f) { this.fechaSolicitud = f; }
    public boolean isTriajeCompletado() { return triajeCompletado; }
    public void setTriajeCompletado(boolean t) { this.triajeCompletado = t; }
}
