package cl.duoc.rednorte.pabellon.service;

import cl.duoc.rednorte.pabellon.model.EstadoPabellon;
import cl.duoc.rednorte.pabellon.model.Pabellon;
import cl.duoc.rednorte.pabellon.repository.PabellonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class PabellonService {

    private final PabellonRepository repository;

    public PabellonService(PabellonRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Pabellon> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pabellon> listarDisponibles() {
        return repository.findByEstado(EstadoPabellon.DISPONIBLE);
    }

    @Transactional(readOnly = true)
    public Pabellon obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pabellón no encontrado con id: " + id));
    }

    public Pabellon crear(Pabellon pabellon) {
        return repository.save(pabellon);
    }

    public Pabellon actualizar(Long id, Pabellon datos) {
        Pabellon p = obtenerPorId(id);
        p.setNumero(datos.getNumero());
        p.setEstado(datos.getEstado());
        return repository.save(p);
    }

    public Pabellon cambiarEstado(Long id, EstadoPabellon estado) {
        Pabellon p = obtenerPorId(id);
        p.setEstado(estado);
        return repository.save(p);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
