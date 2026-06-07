package cl.duoc.rednorte.paciente.model;

import cl.duoc.rednorte.usuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pacientes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    @EqualsAndHashCode.Include
    private Long idPaciente;

    @Column(nullable = false, unique = true, length = 12)
    private String rut;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 150)
    private String email;

    @Column(length = 100)
    private String prevision;

    @Column(name = "grupo_sanguineo", length = 3)
    private String grupoSanguineo;

    @Column(name = "peso_kg", precision = 5, scale = 1)
    private java.math.BigDecimal pesoKg;

    @Column(name = "altura_cm", precision = 5, scale = 1)
    private java.math.BigDecimal alturaCm;

    @Column(columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "enfermedades_cronicas", columnDefinition = "TEXT")
    private String enfermedadesCronicas;

    @Column(name = "contacto_emergencia_nombre", length = 255)
    private String contactoEmergenciaNombre;

    @Column(name = "contacto_emergencia_telefono", length = 20)
    private String contactoEmergenciaTelefono;

    @Column(name = "observacion_medico", columnDefinition = "TEXT")
    private String observacionMedico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medico_asignado")
    private Usuario medicoAsignado;
}
