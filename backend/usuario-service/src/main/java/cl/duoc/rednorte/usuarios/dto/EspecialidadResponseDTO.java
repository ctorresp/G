package cl.duoc.rednorte.usuarios.dto;

import cl.duoc.rednorte.usuarios.model.Especialidad;

public class EspecialidadResponseDTO {
    private Long idEspecialidad;
    private String nombre;
    private String descripcion;

    public EspecialidadResponseDTO() {}

    public static EspecialidadResponseDTO fromEntity(Especialidad e) {
        EspecialidadResponseDTO dto = new EspecialidadResponseDTO();
        dto.setIdEspecialidad(e.getIdEspecialidad());
        dto.setNombre(e.getNombre());
        dto.setDescripcion(e.getDescripcion());
        return dto;
    }

    public Long getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(Long idEspecialidad) { this.idEspecialidad = idEspecialidad; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
