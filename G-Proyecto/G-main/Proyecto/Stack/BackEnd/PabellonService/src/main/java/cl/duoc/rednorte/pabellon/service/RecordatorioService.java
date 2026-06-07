package cl.duoc.rednorte.pabellon.service;

import cl.duoc.rednorte.pabellon.model.Cirugia;
import cl.duoc.rednorte.pabellon.repository.CirugiaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecordatorioService {

    private static final Logger log = LoggerFactory.getLogger(RecordatorioService.class);

    private final CirugiaRepository cirugiaRepository;

    public RecordatorioService(CirugiaRepository cirugiaRepository) {
        this.cirugiaRepository = cirugiaRepository;
    }

    @Scheduled(cron = "0 0 8,14 * * *")
    @Transactional
    public void enviarRecordatorios() {
        LocalDate manana = LocalDate.now().plusDays(1);
        LocalDate dentroDeDosDias = LocalDate.now().plusDays(2);

        List<Cirugia> cirugiasManana = cirugiaRepository
                .findByEstadoAndFechaProgramadaAndNotificadoRecordatorioFalse("PROGRAMADA", manana);

        for (Cirugia c : cirugiasManana) {
            notificar("RECORDATORIO_24H", c, "Recordatorio: su cirugia es MANANA");
            c.setNotificadoRecordatorio(true);
            cirugiaRepository.save(c);
        }

        List<Cirugia> cirugias2Dias = cirugiaRepository
                .findByEstadoAndFechaProgramadaAndNotificadoRecordatorioFalse("PROGRAMADA", dentroDeDosDias);

        for (Cirugia c : cirugias2Dias) {
            notificar("RECORDATORIO_48H", c, "Recordatorio: su cirugia es en 2 dias");
            c.setNotificadoRecordatorio(true);
            cirugiaRepository.save(c);
        }
    }

    private void notificar(String tipo, Cirugia c, String mensaje) {
        log.warn("[NOTIFICACION] {} | Cirugia #{} | Paciente: {} | Medico: {} | Fecha: {} | {}",
                tipo, c.getIdCirugia(), c.getPacienteRut(),
                c.getMedicoRut(), c.getFechaProgramada(), mensaje);
    }
}
