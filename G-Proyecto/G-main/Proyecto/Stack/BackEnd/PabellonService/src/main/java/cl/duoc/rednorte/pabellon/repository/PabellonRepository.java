package cl.duoc.rednorte.pabellon.repository;

import cl.duoc.rednorte.pabellon.model.EstadoPabellon;
import cl.duoc.rednorte.pabellon.model.Pabellon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PabellonRepository extends JpaRepository<Pabellon, Long> {
    Optional<Pabellon> findByNumero(String numero);
    List<Pabellon> findByEstado(EstadoPabellon estado);
}
