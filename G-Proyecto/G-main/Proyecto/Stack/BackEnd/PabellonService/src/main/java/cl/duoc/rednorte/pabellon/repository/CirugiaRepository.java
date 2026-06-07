package cl.duoc.rednorte.pabellon.repository;

import cl.duoc.rednorte.pabellon.model.Cirugia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface CirugiaRepository extends JpaRepository<Cirugia, Long> {
    List<Cirugia> findByMedicoRutOrderByFechaProgramadaAsc(String medicoRut);
    List<Cirugia> findByPacienteRutOrderByFechaProgramadaDesc(String pacienteRut);
    List<Cirugia> findByEstado(String estado);
    List<Cirugia> findByEstadoAndTriajeCompletado(String estado, boolean triajeCompletado);
    List<Cirugia> findByPabellonIdPabellonOrderByFechaProgramadaAsc(Long pabellonId);
    List<Cirugia> findByEstadoAndFechaProgramadaAndNotificadoRecordatorioFalse(String estado, LocalDate fecha);
}
