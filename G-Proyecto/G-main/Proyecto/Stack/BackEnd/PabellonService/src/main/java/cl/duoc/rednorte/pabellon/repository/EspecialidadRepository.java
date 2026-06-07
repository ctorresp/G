package cl.duoc.rednorte.pabellon.repository;

import cl.duoc.rednorte.pabellon.model.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    Optional<Especialidad> findByNombre(String nombre);
}
