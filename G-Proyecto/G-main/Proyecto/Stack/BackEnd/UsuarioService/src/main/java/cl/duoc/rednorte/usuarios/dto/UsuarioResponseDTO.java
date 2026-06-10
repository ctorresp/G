package cl.duoc.rednorte.usuarios.dto;

import cl.duoc.rednorte.usuarios.model.Usuario;

public class UsuarioResponseDTO {
    private Long idUsuario;
    private String rut;
    private String nombre;
    private String email;
    private Boolean estado;
    private String rol;

    public UsuarioResponseDTO() {}

    public static UsuarioResponseDTO fromEntity(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setRut(u.getRut());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setEstado(u.getEstado());
        dto.setRol(u.getRol().getNombreRol());
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
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
