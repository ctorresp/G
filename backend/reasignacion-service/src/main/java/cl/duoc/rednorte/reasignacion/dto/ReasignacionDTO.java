package cl.duoc.rednorte.reasignacion.dto;

import cl.duoc.rednorte.reasignacion.model.Reasignacion;
import java.time.LocalDateTime;

public class ReasignacionDTO {
    private Long idReasignacion;
    private Long cirugiaOriginalId;
    private Long cirugiaReasignadaId;
    private String motivo;
    private LocalDateTime fechaReasignacion;

    public static ReasignacionDTO fromEntity(Reasignacion r) {
        ReasignacionDTO dto = new ReasignacionDTO();
        dto.setIdReasignacion(r.getIdReasignacion());
        dto.setCirugiaOriginalId(r.getCirugiaOriginalId());
        dto.setCirugiaReasignadaId(r.getCirugiaReasignadaId());
        dto.setMotivo(r.getMotivo());
        dto.setFechaReasignacion(r.getFechaReasignacion());
        return dto;
    }

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
