package cl.duoc.rednorte.pabellon.service;

import cl.duoc.rednorte.pabellon.dto.ReportePerdidasDTO;
import cl.duoc.rednorte.pabellon.dto.ReportePerdidasDTO.TopMedicoCancelacion;
import cl.duoc.rednorte.pabellon.model.Cirugia;
import cl.duoc.rednorte.pabellon.repository.CirugiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReporteService {

    private final CirugiaRepository cirugiaRepository;

    public ReporteService(CirugiaRepository cirugiaRepository) {
        this.cirugiaRepository = cirugiaRepository;
    }

    public ReportePerdidasDTO generarReportePerdidas(LocalDate desde, LocalDate hasta) {
        List<Cirugia> todas = cirugiaRepository.findAll().stream()
                .filter(c -> c.getFechaProgramada() != null)
                .filter(c -> !c.getFechaProgramada().isBefore(desde) && !c.getFechaProgramada().isAfter(hasta))
                .collect(Collectors.toList());

        long totalPeriodo = todas.size();

        List<Cirugia> canceladas = todas.stream()
                .filter(c -> "CANCELADA".equals(c.getEstado().name()))
                .collect(Collectors.toList());

        long totalCanceladas = canceladas.size();

        Map<String, Long> cancelacionesPorMedico = canceladas.stream()
                .collect(Collectors.groupingBy(Cirugia::getMedicoRut, Collectors.counting()));

        List<TopMedicoCancelacion> topMedicos = cancelacionesPorMedico.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    TopMedicoCancelacion t = new TopMedicoCancelacion();
                    t.setMedicoRut(entry.getKey());
                    t.setMedicoNombre(entry.getKey());
                    t.setTotalCancelaciones(entry.getValue());
                    return t;
                })
                .collect(Collectors.toList());

        ReportePerdidasDTO reporte = new ReportePerdidasDTO();
        reporte.setTotalCirugiasPeriodo(totalPeriodo);
        reporte.setTotalCirugiasCanceladas(totalCanceladas);
        reporte.setIngresosBrutosPerdidos(BigDecimal.ZERO);
        reporte.setIngresosNetosPerdidos(BigDecimal.ZERO);
        reporte.setTopMedicos(topMedicos);
        return reporte;
    }
}
