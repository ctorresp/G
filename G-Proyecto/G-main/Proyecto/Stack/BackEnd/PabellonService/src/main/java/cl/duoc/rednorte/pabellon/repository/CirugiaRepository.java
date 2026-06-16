package cl.duoc.rednorte.pabellon.repository;

import cl.duoc.rednorte.pabellon.model.Cirugia;
import cl.duoc.rednorte.pabellon.model.EstadoCirugia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface CirugiaRepository extends JpaRepository<Cirugia, Long> {
    List<Cirugia> findByMedicoRutOrderByFechaProgramadaAsc(String medicoRut);
    List<Cirugia> findByPacienteRutOrderByFechaProgramadaDesc(String pacienteRut);
    List<Cirugia> findByEstado(EstadoCirugia estado);
    List<Cirugia> findByEstadoAndTriajeCompletado(EstadoCirugia estado, boolean triajeCompletado);
    List<Cirugia> findByPabellonIdPabellonOrderByFechaProgramadaAsc(Long pabellonId);

    @Query("SELECT c FROM Cirugia c WHERE c.estado = 'SOLICITADA' AND c.triajeCompletado = true AND c.especialidad.idEspecialidad = :especialidadId ORDER BY c.fechaSolicitud ASC")
    List<Cirugia> findSolicitadasTriajeCompletasByEspecialidad(@Param("especialidadId") Long especialidadId);

    @Query("SELECT c FROM Cirugia c WHERE c.estado = 'SOLICITADA' AND c.triajeCompletado = true ORDER BY c.fechaSolicitud ASC")
    List<Cirugia> findSolicitadasTriajeCompletas();
}
