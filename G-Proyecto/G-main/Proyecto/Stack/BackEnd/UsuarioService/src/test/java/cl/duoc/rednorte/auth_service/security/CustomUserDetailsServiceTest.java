package cl.duoc.rednorte.auth_service.security;

import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks private CustomUserDetailsService customUserDetailsService;

    private Usuario usuario;
    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombreRol("ROLE_ADMIN");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Admin Test");
        usuario.setEmail("admin@test.cl");
        usuario.setContrasena("encoded");
        usuario.setEstado(true);
        usuario.setRol(rol);
    }

    @Test
    void loadUserByUsername_conEmailValido_deberiaRetornarUserDetails() {
        when(usuarioRepository.findByEmailAndEstadoTrue("admin@test.cl")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin@test.cl");

        assertNotNull(userDetails);
        assertEquals("admin@test.cl", userDetails.getUsername());
        assertEquals("encoded", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_conEmailInexistente_deberiaLanzarExcepcion() {
        when(usuarioRepository.findByEmailAndEstadoTrue("no@test.cl")).thenReturn(Optional.empty());

        Exception ex = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("no@test.cl"));
        assertTrue(ex.getMessage().contains("no@test.cl"));
    }

    @Test
    void loadUserByUsername_conEmailInactivo_deberiaLanzarExcepcion() {
        when(usuarioRepository.findByEmailAndEstadoTrue("inactivo@test.cl")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("inactivo@test.cl"));
    }
}
