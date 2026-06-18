package cl.duoc.rednorte.pabellon.dto;

import cl.duoc.rednorte.pabellon.model.Cirugia;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CirugiaDTO {
    private Long idCirugia;
    private String pacienteRut;
    private String medicoRut;
    private Long especialidadId;
    private String especialidadNombre;
    private Long pabellonId;
    private String pabellonNumero;
    private LocalDate fechaProgramada;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
    private String motivoCancelacion;
    private LocalDateTime fechaCancelacion;
    private LocalDateTime fechaSolicitud;

    public static CirugiaDTO fromEntity(Cirugia c) {
        CirugiaDTO dto = new CirugiaDTO();
        dto.setIdCirugia(c.getIdCirugia());
        dto.setPacienteRut(c.getPacienteRut());
        dto.setMedicoRut(c.getMedicoRut());
        if (c.getEspecialidad() != null) {
            dto.setEspecialidadId(c.getEspecialidad().getIdEspecialidad());
            dto.setEspecialidadNombre(c.getEspecialidad().getNombre());
        }
        if (c.getPabellon() != null) {
            dto.setPabellonId(c.getPabellon().getIdPabellon());
            dto.setPabellonNumero(c.getPabellon().getNumero());
        }
        dto.setFechaProgramada(c.getFechaProgramada());
        dto.setHoraInicio(c.getHoraInicio());
        dto.setHoraFin(c.getHoraFin());
        dto.setEstado(c.getEstado() != null ? c.getEstado().name() : null);
        dto.setMotivoCancelacion(c.getMotivoCancelacion());
        dto.setFechaCancelacion(c.getFechaCancelacion());
        dto.setFechaSolicitud(c.getFechaSolicitud());
        return dto;
    }

    public Long getIdCirugia() { return idCirugia; }
    public void setIdCirugia(Long id) { this.idCirugia = id; }
    public String getPacienteRut() { return pacienteRut; }
    public void setPacienteRut(String r) { this.pacienteRut = r; }
    public String getMedicoRut() { return medicoRut; }
    public void setMedicoRut(String r) { this.medicoRut = r; }
    public Long getEspecialidadId() { return especialidadId; }
    public void setEspecialidadId(Long id) { this.especialidadId = id; }
    public String getEspecialidadNombre() { return especialidadNombre; }
    public void setEspecialidadNombre(String n) { this.especialidadNombre = n; }
    public Long getPabellonId() { return pabellonId; }
    public void setPabellonId(Long id) { this.pabellonId = id; }
    public String getPabellonNumero() { return pabellonNumero; }
    public void setPabellonNumero(String n) { this.pabellonNumero = n; }
    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate f) { this.fechaProgramada = f; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime h) { this.horaInicio = h; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime h) { this.horaFin = h; }
    public String getEstado() { return estado; }
    public void setEstado(String e) { this.estado = e; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String m) { this.motivoCancelacion = m; }
    public LocalDateTime getFechaCancelacion() { return fechaCancelacion; }
    public void setFechaCancelacion(LocalDateTime f) { this.fechaCancelacion = f; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime f) { this.fechaSolicitud = f; }
}
