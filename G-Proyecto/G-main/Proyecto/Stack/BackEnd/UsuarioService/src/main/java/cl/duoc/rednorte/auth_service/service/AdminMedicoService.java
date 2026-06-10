package cl.duoc.rednorte.auth_service.service;

import cl.duoc.rednorte.auth_service.dto.MedicoUpdateRequestDTO;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminMedicoService {

    private final UsuarioService usuarioService;

    public AdminMedicoService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarMedicos() {
        return usuarioService.listarPorRol("ROLE_MEDICO");
    }

    @Transactional
    public String eliminarMedico(String rut) {
        Usuario usuario = usuarioService.buscarPorRut(rut)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con RUT: " + rut));
        usuarioService.eliminar(usuario);
        return "Médico con RUT " + rut + " eliminado exitosamente";
    }

    public Usuario actualizarMedico(Long id, MedicoUpdateRequestDTO dto) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + id));

        if (dto.getRut() != null && !dto.getRut().isBlank()) {
            if (!usuario.getRut().equals(dto.getRut()) && usuarioService.existePorRut(dto.getRut())) {
                throw new RuntimeException("El RUT ya está registrado: " + dto.getRut());
            }
            usuario.setRut(dto.getRut());
        }
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            usuario.setNombre(dto.getNombre());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!usuario.getEmail().equals(dto.getEmail()) && usuarioService.existePorEmail(dto.getEmail())) {
                throw new RuntimeException("El email ya está registrado: " + dto.getEmail());
            }
            usuario.setEmail(dto.getEmail());
        }

        return usuario;
    }
}

