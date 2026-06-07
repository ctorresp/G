package cl.duoc.rednorte.auth_service.service;

import cl.duoc.rednorte.auth_service.dto.MedicoUpdateRequestDTO;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminMedicoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Usuario> listarMedicos() {
        return usuarioRepository.findByRol_NombreRol("ROLE_MEDICO");
    }

    @Transactional
    public String eliminarMedico(String rut) {
        Usuario usuario = usuarioRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con RUT: " + rut));
        usuarioRepository.delete(usuario);
        return "Médico con RUT " + rut + " eliminado exitosamente";
    }

    public Usuario actualizarMedico(Long id, MedicoUpdateRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + id));

        if (dto.getRut() != null && !dto.getRut().isBlank()) {
            if (!usuario.getRut().equals(dto.getRut()) && usuarioRepository.existsByRut(dto.getRut())) {
                throw new RuntimeException("El RUT ya está registrado: " + dto.getRut());
            }
            usuario.setRut(dto.getRut());
        }
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            usuario.setNombre(dto.getNombre());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("El email ya está registrado: " + dto.getEmail());
            }
            usuario.setEmail(dto.getEmail());
        }

        return usuarioRepository.save(usuario);
    }
}
