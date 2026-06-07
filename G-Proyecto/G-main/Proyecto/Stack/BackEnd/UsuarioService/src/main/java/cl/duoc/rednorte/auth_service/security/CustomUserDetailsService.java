package cl.duoc.rednorte.auth_service.security;

import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailAndEstadoTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado o inactivo con email: " + email));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getContrasena())
                .authorities(List.of(new SimpleGrantedAuthority(usuario.getRol().getNombreRol())))
                .build();
    }
}
