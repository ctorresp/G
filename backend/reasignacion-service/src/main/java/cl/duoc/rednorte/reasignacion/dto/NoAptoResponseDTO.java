package cl.duoc.rednorte.reasignacion.dto;

import java.util.Map;

public class NoAptoResponseDTO {
    private Object cirugiaCancelada;
    private Object cirugiaReasignada;
    private String mensaje;

    public static NoAptoResponseDTO fromMap(Map<String, Object> map) {
        NoAptoResponseDTO dto = new NoAptoResponseDTO();
        dto.setCirugiaCancelada(map.get("cirugiaCancelada"));
        dto.setCirugiaReasignada(map.get("cirugiaReasignada"));
        dto.setMensaje((String) map.get("mensaje"));
        return dto;
    }

    public Object getCirugiaCancelada() { return cirugiaCancelada; }
    public void setCirugiaCancelada(Object c) { this.cirugiaCancelada = c; }
    public Object getCirugiaReasignada() { return cirugiaReasignada; }
    public void setCirugiaReasignada(Object c) { this.cirugiaReasignada = c; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String m) { this.mensaje = m; }
}
