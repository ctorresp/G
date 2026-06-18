package cl.duoc.rednorte.pabellon.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pabellones")
public class Pabellon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pabellon")
    private Long idPabellon;

    @NotBlank(message = "El número de pabellón es obligatorio")
    @Size(max = 10, message = "El número no debe exceder 10 caracteres")
    @Column(nullable = false, unique = true, length = 10)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPabellon estado;

    public Long getIdPabellon() { return idPabellon; }
    public void setIdPabellon(Long id) { this.idPabellon = id; }
    public String getNumero() { return numero; }
    public void setNumero(String n) { this.numero = n; }
    public EstadoPabellon getEstado() { return estado; }
    public void setEstado(EstadoPabellon e) { this.estado = e; }
}
