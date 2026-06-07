package cl.duoc.rednorte.pabellon.service;

import cl.duoc.rednorte.pabellon.model.Triaje;
import cl.duoc.rednorte.pabellon.repository.TriajeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TriajeService {

    private final TriajeRepository repository;

    public TriajeService(TriajeRepository repository) {
        this.repository = repository;
    }

    public Triaje crear(Triaje triaje) {
        triaje.setFechaTriaje(LocalDateTime.now());
        return repository.save(triaje);
    }

    public List<Triaje> historialPorPaciente(String pacienteRut) {
        return repository.findByPacienteRutOrderByFechaTriajeDesc(pacienteRut);
    }
}
