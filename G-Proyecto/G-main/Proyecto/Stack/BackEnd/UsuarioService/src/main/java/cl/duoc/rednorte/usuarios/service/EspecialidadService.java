package cl.duoc.rednorte.usuarios.service;

import cl.duoc.rednorte.usuarios.dto.EspecialidadResponseDTO;
import cl.duoc.rednorte.usuarios.model.Especialidad;
import cl.duoc.rednorte.usuarios.repository.EspecialidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadService(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    @Transactional(readOnly = true)
    public List<EspecialidadResponseDTO> listarTodas() {
        return especialidadRepository.findAll().stream()
                .map(EspecialidadResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Especialidad> buscarPorId(Long id) {
        return especialidadRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Especialidad> buscarPorNombre(String nombre) {
        return especialidadRepository.findByNombre(nombre);
    }
}
