package cl.duoc.rednorte.usuarios.service;

import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.auth_service.repository.RolRepository;
import cl.duoc.rednorte.usuarios.dto.UsuarioRequestDTO;
import cl.duoc.rednorte.usuarios.dto.UsuarioResponseDTO;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UsuarioService usuarioService;

    private Usuario usuario;
    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombreRol("ROLE_ADMIN");
        rol.setDescripcion("Administrador");

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
    void listarTodos_deberiaRetornarLista() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("admin@test.cl", resultado.get(0).getEmail());
        assertEquals("ROLE_ADMIN", resultado.get(0).getRol());
    }

    @Test
    void listarTodos_sinUsuarios_deberiaRetornarListaVacia() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorId_conIdValido_deberiaRetornarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("admin@test.cl", resultado.get().getEmail());
    }

    @Test
    void buscarPorId_conIdInexistente_deberiaRetornarVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.buscarPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorEmail_conEmailValido_deberiaRetornarUsuario() {
        when(usuarioRepository.findByEmail("admin@test.cl")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarPorEmail("admin@test.cl");

        assertTrue(resultado.isPresent());
        assertEquals("admin@test.cl", resultado.get().getEmail());
    }

    @Test
    void buscarPorEmail_conEmailInexistente_deberiaRetornarVacio() {
        when(usuarioRepository.findByEmail("no@test.cl")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.buscarPorEmail("no@test.cl");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorRut_conRutValido_deberiaRetornarUsuario() {
        when(usuarioRepository.findByRut("11111111-1")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarPorRut("11111111-1");

        assertTrue(resultado.isPresent());
        assertEquals("Admin Test", resultado.get().getNombre());
    }

    @Test
    void buscarPorRut_conRutInexistente_deberiaRetornarVacio() {
        when(usuarioRepository.findByRut("00000000-0")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.buscarPorRut("00000000-0");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorEmailActivo_conEmailValido_deberiaRetornarUsuario() {
        when(usuarioRepository.findByEmailAndEstadoTrue("admin@test.cl")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarPorEmailActivo("admin@test.cl");

        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().getEstado());
    }

    @Test
    void buscarPorEmailActivo_conEmailInactivo_deberiaRetornarVacio() {
        when(usuarioRepository.findByEmailAndEstadoTrue("inactivo@test.cl")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.buscarPorEmailActivo("inactivo@test.cl");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarPorRol_conRolExistente_deberiaRetornarLista() {
        when(usuarioRepository.findByRol_NombreRol("ROLE_ADMIN")).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.listarPorRol("ROLE_ADMIN");

        assertEquals(1, resultado.size());
        assertEquals("ROLE_ADMIN", resultado.get(0).getRol().getNombreRol());
    }

    @Test
    void listarPorRol_sinUsuarios_deberiaRetornarListaVacia() {
        when(usuarioRepository.findByRol_NombreRol("ROLE_TRIAJER")).thenReturn(List.of());

        List<Usuario> resultado = usuarioService.listarPorRol("ROLE_TRIAJER");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void existePorEmail_conEmailExistente_deberiaRetornarTrue() {
        when(usuarioRepository.existsByEmail("admin@test.cl")).thenReturn(true);

        boolean resultado = usuarioService.existePorEmail("admin@test.cl");

        assertTrue(resultado);
    }

    @Test
    void existePorEmail_conEmailInexistente_deberiaRetornarFalse() {
        when(usuarioRepository.existsByEmail("no@test.cl")).thenReturn(false);

        boolean resultado = usuarioService.existePorEmail("no@test.cl");

        assertFalse(resultado);
    }

    @Test
    void existePorRut_conRutExistente_deberiaRetornarTrue() {
        when(usuarioRepository.existsByRut("11111111-1")).thenReturn(true);

        boolean resultado = usuarioService.existePorRut("11111111-1");

        assertTrue(resultado);
    }

    @Test
    void existePorRut_conRutInexistente_deberiaRetornarFalse() {
        when(usuarioRepository.existsByRut("00000000-0")).thenReturn(false);

        boolean resultado = usuarioService.existePorRut("00000000-0");

        assertFalse(resultado);
    }

    @Test
    void crear_conDatosValidosYRolExistente_deberiaCrearUsuario() {
        UsuarioRequestDTO request = new UsuarioRequestDTO();
        request.setRut("22222222-2");
        request.setNombre("Nuevo Usuario");
        request.setEmail("nuevo@test.cl");
        request.setContrasena("pass123");

        when(usuarioRepository.existsByEmail("nuevo@test.cl")).thenReturn(false);
        when(usuarioRepository.existsByRut("22222222-2")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(rolRepository.findByNombreRol("ROLE_ADMIN")).thenReturn(Optional.of(rol));

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setIdUsuario(2L);
        usuarioGuardado.setRut("22222222-2");
        usuarioGuardado.setNombre("Nuevo Usuario");
        usuarioGuardado.setEmail("nuevo@test.cl");
        usuarioGuardado.setContrasena("encoded");
        usuarioGuardado.setEstado(true);
        usuarioGuardado.setRol(rol);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioService.crear(request, "ROLE_ADMIN");

        assertNotNull(resultado);
        assertEquals("nuevo@test.cl", resultado.getEmail());
        assertEquals(rol, resultado.getRol());
        verify(passwordEncoder).encode("pass123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void crear_conDatosValidosYRolInexistente_deberiaCrearRolYUsuario() {
        UsuarioRequestDTO request = new UsuarioRequestDTO();
        request.setRut("33333333-3");
        request.setNombre("Usuario Sin Rol");
        request.setEmail("sinrol@test.cl");
        request.setContrasena("pass456");

        Rol nuevoRol = new Rol();
        nuevoRol.setIdRol(2L);
        nuevoRol.setNombreRol("ROLE_NUEVO");
        nuevoRol.setDescripcion("Rol administrativo: ROLE_NUEVO");

        when(usuarioRepository.existsByEmail("sinrol@test.cl")).thenReturn(false);
        when(usuarioRepository.existsByRut("33333333-3")).thenReturn(false);
        when(passwordEncoder.encode("pass456")).thenReturn("encoded");
        when(rolRepository.findByNombreRol("ROLE_NUEVO")).thenReturn(Optional.empty());
        when(rolRepository.save(any(Rol.class))).thenReturn(nuevoRol);

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setIdUsuario(3L);
        usuarioGuardado.setRut("33333333-3");
        usuarioGuardado.setNombre("Usuario Sin Rol");
        usuarioGuardado.setEmail("sinrol@test.cl");
        usuarioGuardado.setContrasena("encoded");
        usuarioGuardado.setEstado(true);
        usuarioGuardado.setRol(nuevoRol);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioService.crear(request, "ROLE_NUEVO");

        assertNotNull(resultado);
        assertEquals("sinrol@test.cl", resultado.getEmail());
        verify(rolRepository).save(any(Rol.class));
    }

    @Test
    void crear_conEmailDuplicado_deberiaLanzarExcepcion() {
        UsuarioRequestDTO request = new UsuarioRequestDTO();
        request.setEmail("admin@test.cl");
        request.setRut("55555555-5");
        request.setNombre("Duplicado");
        request.setContrasena("pass");

        when(usuarioRepository.existsByEmail("admin@test.cl")).thenReturn(true);

        Exception ex = assertThrows(RuntimeException.class, () -> usuarioService.crear(request, "ROLE_ADMIN"));
        assertTrue(ex.getMessage().contains("email ya está registrado"));
    }

    @Test
    void crear_conRutDuplicado_deberiaLanzarExcepcion() {
        UsuarioRequestDTO request = new UsuarioRequestDTO();
        request.setEmail("otro@test.cl");
        request.setRut("11111111-1");
        request.setNombre("Duplicado");
        request.setContrasena("pass");

        when(usuarioRepository.existsByEmail("otro@test.cl")).thenReturn(false);
        when(usuarioRepository.existsByRut("11111111-1")).thenReturn(true);

        Exception ex = assertThrows(RuntimeException.class, () -> usuarioService.crear(request, "ROLE_ADMIN"));
        assertTrue(ex.getMessage().contains("RUT ya está registrado"));
    }

    @Test
    void cambiarEstado_conIdValido_deberiaActualizar() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setIdUsuario(1L);
        usuarioActualizado.setRut("11111111-1");
        usuarioActualizado.setNombre("Admin Test");
        usuarioActualizado.setEmail("admin@test.cl");
        usuarioActualizado.setContrasena("encoded");
        usuarioActualizado.setEstado(false);
        usuarioActualizado.setRol(rol);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizado);

        UsuarioResponseDTO resultado = usuarioService.cambiarEstado(1L, false);

        assertNotNull(resultado);
        assertFalse(resultado.getEstado());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void cambiarEstado_conIdInexistente_deberiaLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> usuarioService.cambiarEstado(99L, false));
        assertTrue(ex.getMessage().contains("Usuario no encontrado"));
    }

    @Test
    void eliminar_deberiaEliminarUsuario() {
        usuarioService.eliminar(usuario);

        verify(usuarioRepository).delete(usuario);
    }
}
