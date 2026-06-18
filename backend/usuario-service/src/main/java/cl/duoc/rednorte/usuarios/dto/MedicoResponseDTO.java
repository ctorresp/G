package cl.duoc.rednorte.usuarios.dto;

import cl.duoc.rednorte.usuarios.model.Medico;

public class MedicoResponseDTO {
    private Long idUsuario;
    private String rut;
    private String nombre;
    private String email;
    private Long especialidadId;
    private String especialidadNombre;

    public MedicoResponseDTO() {}

    public static MedicoResponseDTO fromEntity(Medico m) {
        MedicoResponseDTO dto = new MedicoResponseDTO();
        dto.setIdUsuario(m.getIdUsuario());
        dto.setRut(m.getUsuario().getRut());
        dto.setNombre(m.getUsuario().getNombre());
        dto.setEmail(m.getUsuario().getEmail());
        if (m.getEspecialidad() != null) {
            dto.setEspecialidadId(m.getEspecialidad().getIdEspecialidad());
            dto.setEspecialidadNombre(m.getEspecialidad().getNombre());
        }
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
    public Long getEspecialidadId() { return especialidadId; }
    public void setEspecialidadId(Long especialidadId) { this.especialidadId = especialidadId; }
    public String getEspecialidadNombre() { return especialidadNombre; }
    public void setEspecialidadNombre(String especialidadNombre) { this.especialidadNombre = especialidadNombre; }
}
