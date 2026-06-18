package cl.duoc.rednorte.usuarios.repository;

import cl.duoc.rednorte.usuarios.model.Coordinador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoordinadorRepository extends JpaRepository<Coordinador, Long> {
}
