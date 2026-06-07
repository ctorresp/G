package cl.duoc.rednorte.usuarios.repository;

import cl.duoc.rednorte.usuarios.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Optional<Medico> findByUsuario_Rut(String rut);
}
