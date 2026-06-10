package cl.duoc.rednorte.auth_service.service;

import cl.duoc.rednorte.usuarios.dto.UsuarioResponseDTO;
import cl.duoc.rednorte.usuarios.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminService {

    private final UsuarioService usuarioService;

    public AdminService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioService.listarTodos();
    }

    public UsuarioResponseDTO cambiarEstadoUsuario(Long id, Boolean nuevoEstado) {
        return usuarioService.cambiarEstado(id, nuevoEstado);
    }
}
