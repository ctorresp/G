package cl.duoc.rednorte.reasignacion.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reasignaciones")
public class Reasignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reasignacion")
    private Long idReasignacion;

    @Column(name = "cirugia_original_id", nullable = false)
    private Long cirugiaOriginalId;

    @Column(name = "cirugia_reasignada_id")
    private Long cirugiaReasignadaId;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "fecha_reasignacion", nullable = false)
    private LocalDateTime fechaReasignacion;

    public Long getIdReasignacion() { return idReasignacion; }
    public void setIdReasignacion(Long id) { this.idReasignacion = id; }
    public Long getCirugiaOriginalId() { return cirugiaOriginalId; }
    public void setCirugiaOriginalId(Long id) { this.cirugiaOriginalId = id; }
    public Long getCirugiaReasignadaId() { return cirugiaReasignadaId; }
    public void setCirugiaReasignadaId(Long id) { this.cirugiaReasignadaId = id; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String m) { this.motivo = m; }
    public LocalDateTime getFechaReasignacion() { return fechaReasignacion; }
    public void setFechaReasignacion(LocalDateTime f) { this.fechaReasignacion = f; }
}
