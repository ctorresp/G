package cl.duoc.rednorte.paciente.service;

import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.auth_service.repository.UsuarioRepository;
import cl.duoc.rednorte.datos_clinicos.model.DatosClinicos;
import cl.duoc.rednorte.paciente.dto.PacienteResponseDTO;
import cl.duoc.rednorte.paciente.model.Paciente;
import cl.duoc.rednorte.paciente.repository.PacienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Servicio principal para la gestión de pacientes
@Service
@Transactional(readOnly = true)
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Retorna todos los pacientes (Solo para Admin y Médico)
    public List<PacienteResponseDTO> listarTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PacienteResponseDTO buscarPorId(Long idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + idPaciente));
        return toDTO(paciente);
    }

    public PacienteResponseDTO buscarPorIdUsuario(Long idUsuario) {
        Paciente paciente = pacienteRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró ficha de paciente para el usuario ID: " + idUsuario));
        return toDTO(paciente);
    }

    public PacienteResponseDTO buscarPorRut(String rut) {
        Long idUsuario = usuarioRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con RUT: " + rut))
                .getIdUsuario();

        return buscarPorIdUsuario(idUsuario);
    }

    // Actualiza los datos clínicos específicos de un paciente
    @Transactional
    public PacienteResponseDTO actualizarDatosClinicos(
            Long idPaciente,
            String prevision,
            DatosClinicos datosClinicosSensibles) {

        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + idPaciente));

        if (prevision != null && !prevision.isBlank()) {
            paciente.setPrevision(prevision);
        }
        if (datosClinicosSensibles != null) {
            paciente.setDatosClinicosSensibles(datosClinicosSensibles);
        }

        return toDTO(pacienteRepository.save(paciente));
    }

    // Bloquea el acceso del paciente al sistema
    @Transactional
    public String desactivarPaciente(Long idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + idPaciente));

        paciente.getUsuario().setEstado(false);
        usuarioRepository.save(paciente.getUsuario());

        return "Cuenta del paciente ID " + idPaciente + " desactivada exitosamente";
    }

    // Restaura el acceso de un paciente desactivado
    @Transactional
    public String activarPaciente(Long idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + idPaciente));

        paciente.getUsuario().setEstado(true);
        usuarioRepository.save(paciente.getUsuario());

        return "Cuenta del paciente ID " + idPaciente + " activada exitosamente";
    }

    // Convierte la entidad a DTO omitiendo datos sensibles como la contraseña
    private PacienteResponseDTO toDTO(Paciente paciente) {
        return new PacienteResponseDTO(
                paciente.getIdPaciente(),
                paciente.getPrevision(),
                paciente.getDatosClinicosSensibles(),
                paciente.getUsuario().getIdUsuario(),
                paciente.getUsuario().getRut(),
                paciente.getUsuario().getNombre(),
                paciente.getUsuario().getEmail(),
                paciente.getUsuario().getEstado(),
                paciente.getUsuario().getRoles().stream()
                        .map(Rol::getNombreRol)
                        .collect(Collectors.toSet())
        );
    }
}
