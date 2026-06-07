package cl.duoc.rednorte.usuarios.repository;

import cl.duoc.rednorte.usuarios.model.Triajer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriajerRepository extends JpaRepository<Triajer, Long> {
}
