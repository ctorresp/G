package cl.duoc.rednorte.paciente.model;
import cl.duoc.rednorte.auth_service.model.Usuario;
import cl.duoc.rednorte.datos_clinicos.model.DatosClinicos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

// Entidad que almacena los datos clínicos y la ficha del paciente
@Entity
@Table(name = "pacientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"usuario"})
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    @EqualsAndHashCode.Include
    private Long idPaciente;

    @Column(name = "prevision", length = 100)
    private String prevision;

    // Datos clínicos almacenados como objeto JSON nativo
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_clinicos_sensibles", columnDefinition = "json")
    private DatosClinicos datosClinicosSensibles;

    // Relación 1:1 con las credenciales de acceso
    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;
}
