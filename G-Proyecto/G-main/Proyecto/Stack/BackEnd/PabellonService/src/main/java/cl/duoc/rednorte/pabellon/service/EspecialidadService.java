package cl.duoc.rednorte.pabellon.service;

import cl.duoc.rednorte.pabellon.model.Especialidad;
import cl.duoc.rednorte.pabellon.repository.EspecialidadRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EspecialidadService {

    private final EspecialidadRepository repository;

    public EspecialidadService(EspecialidadRepository repository) {
        this.repository = repository;
    }

    public List<Especialidad> listarTodas() {
        return repository.findAll();
    }

    public Especialidad obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con id: " + id));
    }

    public Especialidad crear(Especialidad especialidad) {
        return repository.save(especialidad);
    }

    public Especialidad actualizar(Long id, Especialidad datos) {
        Especialidad e = obtenerPorId(id);
        e.setNombre(datos.getNombre());
        e.setDescripcion(datos.getDescripcion());
        return repository.save(e);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
