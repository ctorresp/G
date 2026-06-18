package cl.duoc.rednorte.usuarios.service;

import cl.duoc.rednorte.usuarios.dto.TriajerResponseDTO;
import cl.duoc.rednorte.usuarios.model.Triajer;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.TriajerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TriajerService {

    private final TriajerRepository triajerRepository;

    public TriajerService(TriajerRepository triajerRepository) {
        this.triajerRepository = triajerRepository;
    }

    @Transactional(readOnly = true)
    public List<TriajerResponseDTO> listarTodos() {
        return triajerRepository.findAll().stream()
                .map(TriajerResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Triajer> buscarPorIdUsuario(Long idUsuario) {
        return triajerRepository.findById(idUsuario);
    }

    public Triajer crear(Usuario usuario, String certificaciones) {
        Triajer triajer = new Triajer();
        triajer.setUsuario(usuario);
        triajer.setCertificaciones(certificaciones);
        return triajerRepository.save(triajer);
    }
}
