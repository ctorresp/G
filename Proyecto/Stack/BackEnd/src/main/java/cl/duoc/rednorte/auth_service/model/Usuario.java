package cl.duoc.rednorte.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

import cl.duoc.rednorte.paciente.model.Paciente;

/**
 * Entidad que representa a los usuarios del sistema.
 * Tabla: usuarios — DB_Pacientes
 * Gestiona credenciales de acceso (JWT/RBAC).
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"paciente", "roles"})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @EqualsAndHashCode.Include
    private Long idUsuario;

    /** RUT del usuario (identificador nacional chileno) */
    @Column(name = "rut", nullable = false, unique = true, length = 12)
    private String rut;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    /** Contraseña almacenada con BCrypt */
    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    /**
     * Estado de la cuenta:
     * true = activa, false = desactivada/bloqueada
     */
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    /** Relación muchos-a-muchos con Rol a través de la tabla usuario_roles */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles = new HashSet<>();

    /** Referencia opcional al paciente asociado a este usuario */
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, optional = true)
    private Paciente paciente;
}
