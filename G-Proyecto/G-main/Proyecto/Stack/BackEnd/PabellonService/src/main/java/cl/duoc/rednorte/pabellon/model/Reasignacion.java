package cl.duoc.rednorte.pabellon.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reasignaciones")
public class Reasignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reasignacion")
    private Long idReasignacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cirugia_original_id", nullable = false)
    private Cirugia cirugiaOriginal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cirugia_reasignada_id")
    private Cirugia cirugiaReasignada;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "fecha_reasignacion", nullable = false)
    private LocalDateTime fechaReasignacion;

    public Long getIdReasignacion() { return idReasignacion; }
    public void setIdReasignacion(Long id) { this.idReasignacion = id; }
    public Cirugia getCirugiaOriginal() { return cirugiaOriginal; }
    public void setCirugiaOriginal(Cirugia c) { this.cirugiaOriginal = c; }
    public Cirugia getCirugiaReasignada() { return cirugiaReasignada; }
    public void setCirugiaReasignada(Cirugia c) { this.cirugiaReasignada = c; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String m) { this.motivo = m; }
    public LocalDateTime getFechaReasignacion() { return fechaReasignacion; }
    public void setFechaReasignacion(LocalDateTime f) { this.fechaReasignacion = f; }
}
