package cl.duoc.rednorte.usuarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coordinadores")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Coordinador {

    @Id
    @Column(name = "id_usuario")
    @EqualsAndHashCode.Include
    private Long idUsuario;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "area_asignada", length = 100)
    private String areaAsignada;
}
