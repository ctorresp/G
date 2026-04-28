package cl.duoc.rednorte.auth_service.security;

import cl.duoc.rednorte.auth_service.model.Usuario;
import cl.duoc.rednorte.auth_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de UserDetailsService de Spring Security.
 * Carga los detalles del usuario desde la base de datos para la autenticación.
 * El "username" en este contexto es el email del usuario.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carga un usuario por su email para que Spring Security
     * pueda autenticar y autorizar la solicitud.
     * Usa una query nativa para cargar los roles, evitando problemas
     * de sesión Hibernate con relaciones @ManyToMany.
     *
     * @param email email del usuario (usado como username)
     * @return UserDetails con credenciales y roles del usuario
     * @throws UsernameNotFoundException si el usuario no existe o está inactivo
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Cargar el usuario básico (sin roles para evitar problemas Hibernate)
        Usuario usuario = usuarioRepository.findByEmailAndEstadoTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado o inactivo con email: " + email));

        // Cargar roles con query nativa SQL — garantiza que estén disponibles
        List<String> roleNames = usuarioRepository.findRoleNamesByEmail(email);

        List<SimpleGrantedAuthority> authorities = roleNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getContrasena())
                .authorities(authorities)
                .build();
    }
}
