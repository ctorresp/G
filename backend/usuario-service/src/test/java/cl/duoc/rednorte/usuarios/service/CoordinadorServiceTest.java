package cl.duoc.rednorte.usuarios.service;

import cl.duoc.rednorte.usuarios.dto.CoordinadorResponseDTO;
import cl.duoc.rednorte.usuarios.model.Coordinador;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.CoordinadorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoordinadorServiceTest {

    @Mock private CoordinadorRepository coordinadorRepository;

    @InjectMocks private CoordinadorService coordinadorService;

    private Usuario usuario;
    private Coordinador coordinador;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Coordinador Test");
        usuario.setEmail("coordinador@test.cl");
        usuario.setContrasena("encoded");

        coordinador = new Coordinador();
        coordinador.setIdUsuario(1L);
        coordinador.setUsuario(usuario);
        coordinador.setAreaAsignada("Urgencias");
    }

    @Test
    void listarTodos_deberiaRetornarLista() {
        when(coordinadorRepository.findAll()).thenReturn(List.of(coordinador));

        List<CoordinadorResponseDTO> resultado = coordinadorService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("coordinador@test.cl", resultado.get(0).getEmail());
        assertEquals("Urgencias", resultado.get(0).getAreaAsignada());
    }

    @Test
    void listarTodos_sinCoordinadores_deberiaRetornarListaVacia() {
        when(coordinadorRepository.findAll()).thenReturn(List.of());

        List<CoordinadorResponseDTO> resultado = coordinadorService.listarTodos();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorIdUsuario_conIdValido_deberiaRetornarCoordinador() {
        when(coordinadorRepository.findById(1L)).thenReturn(Optional.of(coordinador));

        Optional<Coordinador> resultado = coordinadorService.buscarPorIdUsuario(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Coordinador Test", resultado.get().getUsuario().getNombre());
    }

    @Test
    void buscarPorIdUsuario_conIdInexistente_deberiaRetornarVacio() {
        when(coordinadorRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Coordinador> resultado = coordinadorService.buscarPorIdUsuario(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_conDatosValidos_deberiaCrearCoordinador() {
        when(coordinadorRepository.save(any(Coordinador.class))).thenAnswer(i -> i.getArgument(0));

        Coordinador resultado = coordinadorService.crear(usuario, "Pabellón");

        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals("Pabellón", resultado.getAreaAsignada());
        verify(coordinadorRepository).save(any(Coordinador.class));
    }
}
