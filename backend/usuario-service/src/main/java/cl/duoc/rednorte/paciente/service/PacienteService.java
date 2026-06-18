package cl.duoc.rednorte.paciente.service;

import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.MedicoRepository;
import cl.duoc.rednorte.usuarios.repository.UsuarioRepository;
import cl.duoc.rednorte.paciente.dto.PacienteResponseDTO;
import cl.duoc.rednorte.paciente.model.Paciente;
import cl.duoc.rednorte.paciente.repository.PacienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    public List<PacienteResponseDTO> listarTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PacienteResponseDTO> listarPorMedico(Long idMedico) {
        return pacienteRepository.findByMedicoAsignado_IdUsuarioOrderByIdPacienteDesc(idMedico)
                .stream()
                .filter(p -> p.getActivo() == null || p.getActivo())
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PacienteResponseDTO> listarPorMedicoRut(String rutMedico) {
        Long idUsuario = usuarioRepository.findByRut(rutMedico)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con RUT: " + rutMedico))
                .getIdUsuario();
        return listarPorMedico(idUsuario);
    }

    public PacienteResponseDTO buscarPorRut(String rut) {
        Paciente paciente = pacienteRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con RUT: " + rut));
        return toDTO(paciente);
    }

    @Transactional
    public PacienteResponseDTO actualizarDatosClinicos(
            String rut,
            String prevision,
            String email,
            String grupoSanguineo,
            Double pesoKg,
            Double alturaCm,
            String alergias,
            String enfermedadesCronicas) {

        Paciente paciente = pacienteRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con RUT: " + rut));

        if (prevision != null && !prevision.isBlank()) {
            paciente.setPrevision(prevision);
        }
        if (email != null && !email.isBlank()) {
            paciente.setEmail(email);
        }
        if (grupoSanguineo != null) paciente.setGrupoSanguineo(grupoSanguineo);
        if (pesoKg != null) paciente.setPesoKg(java.math.BigDecimal.valueOf(pesoKg));
        if (alturaCm != null) paciente.setAlturaCm(java.math.BigDecimal.valueOf(alturaCm));
        if (alergias != null) paciente.setAlergias(alergias);
        if (enfermedadesCronicas != null) paciente.setEnfermedadesCronicas(enfermedadesCronicas);

        return toDTO(pacienteRepository.save(paciente));
    }

    @Transactional
    public PacienteResponseDTO actualizarContactoEmergencia(
            String rut,
            Long idMedico,
            String contactoEmergenciaNombre,
            String contactoEmergenciaTelefono) {

        Paciente paciente = pacienteRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con RUT: " + rut));

        if (idMedico != null) {
            Usuario medico = usuarioRepository.findById(idMedico)
                    .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + idMedico));
            paciente.setMedicoAsignado(medico);
        }
        if (contactoEmergenciaNombre != null && !contactoEmergenciaNombre.isBlank()) {
            paciente.setContactoEmergenciaNombre(contactoEmergenciaNombre);
        }
        if (contactoEmergenciaTelefono != null && !contactoEmergenciaTelefono.isBlank()) {
            paciente.setContactoEmergenciaTelefono(contactoEmergenciaTelefono);
        }

        return toDTO(pacienteRepository.save(paciente));
    }

    @Transactional
    public PacienteResponseDTO actualizarObservacion(String rut, String observacion) {
        Paciente paciente = pacienteRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con RUT: " + rut));
        paciente.setObservacionMedico(observacion);
        return toDTO(pacienteRepository.save(paciente));
    }

    @Transactional
    public PacienteResponseDTO cambiarEstado(String rut, Boolean nuevoEstado) {
        Paciente paciente = pacienteRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con RUT: " + rut));
        paciente.setActivo(nuevoEstado);
        return toDTO(pacienteRepository.save(paciente));
    }

    @Transactional
    public String eliminarPaciente(String rut) {
        Paciente paciente = pacienteRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con RUT: " + rut));
        pacienteRepository.delete(paciente);
        return "Paciente con RUT " + rut + " eliminado exitosamente";
    }

    private PacienteResponseDTO toDTO(Paciente paciente) {
        List<String> alergiasList = paciente.getAlergias() != null && !paciente.getAlergias().isBlank()
                ? Arrays.asList(paciente.getAlergias().split("\\s*,\\s*"))
                : List.of();
        List<String> enfermedadesList = paciente.getEnfermedadesCronicas() != null && !paciente.getEnfermedadesCronicas().isBlank()
                ? Arrays.asList(paciente.getEnfermedadesCronicas().split("\\s*,\\s*"))
                : List.of();

        Map<String, Object> datosClinicos = new HashMap<>();
        datosClinicos.put("grupoSanguineo", paciente.getGrupoSanguineo());
        datosClinicos.put("pesoKg", paciente.getPesoKg() != null ? paciente.getPesoKg().doubleValue() : null);
        datosClinicos.put("alturaCm", paciente.getAlturaCm() != null ? paciente.getAlturaCm().doubleValue() : null);
        datosClinicos.put("alergias", alergiasList);
        datosClinicos.put("enfermedadesCronicas", enfermedadesList);

        String especialidadNombre = null;
        if (paciente.getMedicoAsignado() != null) {
            especialidadNombre = medicoRepository.findByUsuario_IdUsuario(paciente.getMedicoAsignado().getIdUsuario())
                    .map(m -> m.getEspecialidad().getNombre())
                    .orElse(null);
        }

        String nombreMedico = paciente.getMedicoAsignado() != null
                ? paciente.getMedicoAsignado().getNombre()
                : null;

        return new PacienteResponseDTO(
                paciente.getIdPaciente(),
                paciente.getRut(),
                paciente.getNombre(),
                paciente.getEmail(),
                paciente.getPrevision(),
                paciente.getMedicoAsignado() != null ? paciente.getMedicoAsignado().getIdUsuario() : null,
                nombreMedico,
                paciente.getContactoEmergenciaNombre(),
                paciente.getContactoEmergenciaTelefono(),
                paciente.getObservacionMedico(),
                datosClinicos,
                paciente.getActivo() == null || paciente.getActivo(),
                especialidadNombre
        );
    }
}
