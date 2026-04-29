package cl.duoc.rednorte.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Entidad que representa los datos del paciente.
 * Tabla: pacientes — DB_Pacientes
 * Contiene datos sensibles clínicos y la ficha única digital.
 */
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

    /**
     * Tipo de previsión de salud del paciente
     * (ej. FONASA, ISAPRE, etc.)
     */
    @Column(name = "prevision", length = 100)
    private String prevision;

    /**
     * Datos clínicos sensibles del paciente
     * almacenados como un objeto JSON nativo en la base de datos.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_clinicos_sensibles", columnDefinition = "json")
    private DatosClinicos datosClinicosSensibles;

    /**
     * Relación uno-a-uno con Usuario:
     * cada paciente tiene exactamente un usuario de acceso.
     */
    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;
}
