package cl.duoc.rednorte.usuarios.model;

import cl.duoc.rednorte.auth_service.model.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @EqualsAndHashCode.Include
    private Long idUsuario;

    @Column(name = "rut", nullable = false, unique = true, length = 12)
    private String rut;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;
}
