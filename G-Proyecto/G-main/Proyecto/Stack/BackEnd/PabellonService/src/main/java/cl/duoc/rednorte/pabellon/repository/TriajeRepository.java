package cl.duoc.rednorte.pabellon.repository;

import cl.duoc.rednorte.pabellon.model.Triaje;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TriajeRepository extends JpaRepository<Triaje, Long> {
    List<Triaje> findByPacienteRutOrderByFechaTriajeDesc(String pacienteRut);
}
