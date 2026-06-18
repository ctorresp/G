package cl.duoc.rednorte.pabellon.repository;

import cl.duoc.rednorte.pabellon.model.Cirugia;
import cl.duoc.rednorte.pabellon.model.EstadoCirugia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CirugiaRepository extends JpaRepository<Cirugia, Long> {
    List<Cirugia> findByMedicoRutOrderByFechaProgramadaAsc(String medicoRut);
    List<Cirugia> findByPacienteRutOrderByFechaProgramadaDesc(String pacienteRut);
    List<Cirugia> findByEstado(EstadoCirugia estado);
    List<Cirugia> findByEstadoAndTriajeCompletado(EstadoCirugia estado, boolean triajeCompletado);
    List<Cirugia> findByPabellonIdPabellonOrderByFechaProgramadaAsc(Long pabellonId);
}
