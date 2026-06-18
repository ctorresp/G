package cl.duoc.rednorte.usuarios.dto;

import cl.duoc.rednorte.usuarios.model.Coordinador;

public class CoordinadorResponseDTO {
    private Long idUsuario;
    private String rut;
    private String nombre;
    private String email;
    private String areaAsignada;

    public CoordinadorResponseDTO() {}

    public static CoordinadorResponseDTO fromEntity(Coordinador c) {
        CoordinadorResponseDTO dto = new CoordinadorResponseDTO();
        dto.setIdUsuario(c.getIdUsuario());
        dto.setRut(c.getUsuario().getRut());
        dto.setNombre(c.getUsuario().getNombre());
        dto.setEmail(c.getUsuario().getEmail());
        dto.setAreaAsignada(c.getAreaAsignada());
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
    public String getAreaAsignada() { return areaAsignada; }
    public void setAreaAsignada(String areaAsignada) { this.areaAsignada = areaAsignada; }
}
