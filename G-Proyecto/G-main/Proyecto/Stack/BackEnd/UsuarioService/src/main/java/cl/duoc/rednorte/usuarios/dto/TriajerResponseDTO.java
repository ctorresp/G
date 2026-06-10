package cl.duoc.rednorte.usuarios.dto;

import cl.duoc.rednorte.usuarios.model.Triajer;

public class TriajerResponseDTO {
    private Long idUsuario;
    private String rut;
    private String nombre;
    private String email;
    private String certificaciones;

    public TriajerResponseDTO() {}

    public static TriajerResponseDTO fromEntity(Triajer t) {
        TriajerResponseDTO dto = new TriajerResponseDTO();
        dto.setIdUsuario(t.getIdUsuario());
        dto.setRut(t.getUsuario().getRut());
        dto.setNombre(t.getUsuario().getNombre());
        dto.setEmail(t.getUsuario().getEmail());
        dto.setCertificaciones(t.getCertificaciones());
        return dto;
    }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCertificaciones() { return certificaciones; }
    public void setCertificaciones(String certificaciones) { this.certificaciones = certificaciones; }
}
