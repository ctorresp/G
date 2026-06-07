package cl.duoc.rednorte.usuarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "triajers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Triajer {

    @Id
    @Column(name = "id_usuario")
    @EqualsAndHashCode.Include
    private Long idUsuario;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(columnDefinition = "TEXT")
    private String certificaciones;
}
