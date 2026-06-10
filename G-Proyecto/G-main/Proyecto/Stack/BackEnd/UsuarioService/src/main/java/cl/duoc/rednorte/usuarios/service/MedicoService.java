package cl.duoc.rednorte.usuarios.service;

import cl.duoc.rednorte.usuarios.dto.MedicoResponseDTO;
import cl.duoc.rednorte.usuarios.model.Especialidad;
import cl.duoc.rednorte.usuarios.model.Medico;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.EspecialidadRepository;
import cl.duoc.rednorte.usuarios.repository.MedicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final UsuarioService usuarioService;

    public MedicoService(MedicoRepository medicoRepository, EspecialidadRepository especialidadRepository, UsuarioService usuarioService) {
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<MedicoResponseDTO> listarTodos() {
        return medicoRepository.findAll().stream()
                .map(MedicoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Medico> buscarPorIdUsuario(Long idUsuario) {
        return medicoRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Medico> buscarPorRut(String rut) {
        return medicoRepository.findByUsuario_Rut(rut);
    }

    public Medico crear(Usuario usuario, Long especialidadId) {
        Medico medico = new Medico();
        medico.setUsuario(usuario);
        if (especialidadId != null) {
            Especialidad esp = especialidadRepository.findById(especialidadId)
                    .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con ID: " + especialidadId));
            medico.setEspecialidad(esp);
        } else {
            Especialidad esp = especialidadRepository.findByNombre("Cirugía General")
                    .orElseThrow(() -> new RuntimeException("Especialidad por defecto no encontrada"));
            medico.setEspecialidad(esp);
        }
        return medicoRepository.save(medico);
    }

    public Medico crearConUsuario(Usuario usuario, Long especialidadId) {
        return crear(usuario, especialidadId);
    }

    public Medico obtenerOCrear(Usuario usuario) {
        return medicoRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .orElseGet(() -> {
                    Especialidad esp = especialidadRepository.findByNombre("Cirugía General")
                            .orElseThrow(() -> new RuntimeException("Especialidad por defecto no encontrada"));
                    Medico nuevo = new Medico();
                    nuevo.setUsuario(usuario);
                    nuevo.setEspecialidad(esp);
                    return medicoRepository.save(nuevo);
                });
    }
}
