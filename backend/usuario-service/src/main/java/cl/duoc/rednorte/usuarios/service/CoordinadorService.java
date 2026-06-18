package cl.duoc.rednorte.usuarios.service;

import cl.duoc.rednorte.usuarios.dto.CoordinadorResponseDTO;
import cl.duoc.rednorte.usuarios.model.Coordinador;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.CoordinadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CoordinadorService {

    private final CoordinadorRepository coordinadorRepository;

    public CoordinadorService(CoordinadorRepository coordinadorRepository) {
        this.coordinadorRepository = coordinadorRepository;
    }

    @Transactional(readOnly = true)
    public List<CoordinadorResponseDTO> listarTodos() {
        return coordinadorRepository.findAll().stream()
                .map(CoordinadorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Coordinador> buscarPorIdUsuario(Long idUsuario) {
        return coordinadorRepository.findById(idUsuario);
    }

    public Coordinador crear(Usuario usuario, String areaAsignada) {
        Coordinador coordinador = new Coordinador();
        coordinador.setUsuario(usuario);
        coordinador.setAreaAsignada(areaAsignada);
        return coordinadorRepository.save(coordinador);
    }
}
