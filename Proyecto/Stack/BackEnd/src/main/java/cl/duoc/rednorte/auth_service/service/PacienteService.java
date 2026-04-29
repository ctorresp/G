package cl.duoc.rednorte.auth_service.service;

import cl.duoc.rednorte.auth_service.dto.PacienteResponseDTO;
import cl.duoc.rednorte.auth_service.model.DatosClinicos;
import cl.duoc.rednorte.auth_service.model.Paciente;
import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.auth_service.repository.PacienteRepository;
import cl.duoc.rednorte.auth_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de pacientes.
 * Expone operaciones de consulta y administración sobre la tabla pacientes
 * y su relación con usuarios de DB_Pacientes.
 */
@Service
@Transactional(readOnly = true)
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Retorna la lista completa de pacientes registrados.
     * Solo accesible por ROLE_ADMIN o ROLE_MEDICO.
     *
     * @return lista de PacienteResponseDTO
     */
    public List<PacienteResponseDTO> listarTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un paciente por su ID de paciente.
     *
     * @param idPaciente ID de la tabla pacientes
     * @return PacienteResponseDTO si existe
     * @throws RuntimeException si no se encuentra
     */
    public PacienteResponseDTO buscarPorId(Long idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + idPaciente));
        return toDTO(paciente);
    }

    /**
     * Busca un paciente por el ID de su usuario asociado.
     *
     * @param idUsuario ID de la tabla usuarios
     * @return PacienteResponseDTO si existe
     * @throws RuntimeException si no se encuentra
     */
    public PacienteResponseDTO buscarPorIdUsuario(Long idUsuario) {
        Paciente paciente = pacienteRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró ficha de paciente para el usuario ID: " + idUsuario));
        return toDTO(paciente);
    }

    /**
     * Busca un paciente por el RUT del usuario asociado.
     *
     * @param rut RUT del usuario
     * @return PacienteResponseDTO si existe
     * @throws RuntimeException si el usuario o paciente no existe
     */
    public PacienteResponseDTO buscarPorRut(String rut) {
        Long idUsuario = usuarioRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con RUT: " + rut))
                .getIdUsuario();

        return buscarPorIdUsuario(idUsuario);
    }

    /**
     * Actualiza los datos clínicos sensibles de un paciente.
     * Solo accesible por ROLE_ADMIN o ROLE_MEDICO.
     *
     * @param idPaciente             ID del paciente a actualizar
     * @param prevision              nueva previsión (puede ser null para no cambiar)
     * @param datosClinicosSensibles nuevos datos clínicos (puede ser null para no cambiar)
     * @return PacienteResponseDTO actualizado
     * @throws RuntimeException si el paciente no existe
     */
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

    /**
     * Desactiva (bloquea) la cuenta de un paciente.
     * El paciente no podrá iniciar sesión hasta que sea reactivado.
     * Solo accesible por ROLE_ADMIN.
     *
     * @param idPaciente ID del paciente
     * @return mensaje de confirmación
     * @throws RuntimeException si el paciente no existe
     */
    @Transactional
    public String desactivarPaciente(Long idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + idPaciente));

        paciente.getUsuario().setEstado(false);
        usuarioRepository.save(paciente.getUsuario());

        return "Cuenta del paciente ID " + idPaciente + " desactivada exitosamente";
    }

    /**
     * Reactiva la cuenta de un paciente desactivado.
     * Solo accesible por ROLE_ADMIN.
     *
     * @param idPaciente ID del paciente
     * @return mensaje de confirmación
     * @throws RuntimeException si el paciente no existe
     */
    @Transactional
    public String activarPaciente(Long idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + idPaciente));

        paciente.getUsuario().setEstado(true);
        usuarioRepository.save(paciente.getUsuario());

        return "Cuenta del paciente ID " + idPaciente + " activada exitosamente";
    }

    /**
     * Convierte una entidad Paciente al DTO de respuesta.
     * La contraseña NUNCA se incluye en la respuesta.
     *
     * @param paciente entidad Paciente
     * @return PacienteResponseDTO sin datos sensibles de seguridad
     */
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
