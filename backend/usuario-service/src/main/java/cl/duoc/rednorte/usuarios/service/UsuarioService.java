package cl.duoc.rednorte.usuarios.service;

import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.auth_service.repository.RolRepository;
import cl.duoc.rednorte.usuarios.dto.UsuarioRequestDTO;
import cl.duoc.rednorte.usuarios.dto.UsuarioResponseDTO;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorRut(String rut) {
        return usuarioRepository.findByRut(rut);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmailActivo(String email) {
        return usuarioRepository.findByEmailAndEstadoTrue(email);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarPorRol(String nombreRol) {
        return usuarioRepository.findByRol_NombreRol(nombreRol);
    }

    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existePorRut(String rut) {
        return usuarioRepository.existsByRut(rut);
    }

    public Usuario crear(UsuarioRequestDTO request, String nombreRol) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + request.getEmail());
        }
        if (usuarioRepository.existsByRut(request.getRut())) {
            throw new RuntimeException("El RUT ya está registrado: " + request.getRut());
        }

        Usuario usuario = new Usuario();
        usuario.setRut(request.getRut());
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        usuario.setEstado(true);

        Rol rol = rolRepository.findByNombreRol(nombreRol)
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombreRol(nombreRol);
                    nuevoRol.setDescripcion("Rol administrativo: " + nombreRol);
                    return rolRepository.save(nuevoRol);
                });
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    public UsuarioResponseDTO cambiarEstado(Long id, Boolean nuevoEstado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setEstado(nuevoEstado);
        Usuario guardado = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(guardado);
    }

    public void eliminar(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }
}
