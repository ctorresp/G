package cl.duoc.rednorte.pabellon.controller;

import cl.duoc.rednorte.pabellon.dto.ReportePerdidasDTO;
import cl.duoc.rednorte.pabellon.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/pabellon/reportes")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    @GetMapping("/perdidas")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_COORDINADOR')")
    public ResponseEntity<ReportePerdidasDTO> reportePerdidas(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().withDayOfMonth(1)}") LocalDate desde,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate hasta) {
        return ResponseEntity.ok(service.generarReportePerdidas(desde, hasta));
    }
}
