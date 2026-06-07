package cl.duoc.rednorte.pabellon.service;

import cl.duoc.rednorte.pabellon.model.Reasignacion;
import cl.duoc.rednorte.pabellon.repository.ReasignacionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReasignacionService {

    private final ReasignacionRepository repository;

    public ReasignacionService(ReasignacionRepository repository) {
        this.repository = repository;
    }

    public List<Reasignacion> listarTodas() {
        return repository.findAll();
    }
}
