package cl.duoc.rednorte.usuarios.service;

import cl.duoc.rednorte.usuarios.dto.TriajerResponseDTO;
import cl.duoc.rednorte.usuarios.model.Triajer;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.TriajerRepository;

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
class TriajerServiceTest {

    @Mock private TriajerRepository triajerRepository;

    @InjectMocks private TriajerService triajerService;

    private Usuario usuario;
    private Triajer triajer;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Triajer Test");
        usuario.setEmail("triajer@test.cl");
        usuario.setContrasena("encoded");

        triajer = new Triajer();
        triajer.setIdUsuario(1L);
        triajer.setUsuario(usuario);
        triajer.setCertificaciones("Certificación A, Certificación B");
    }

    @Test
    void listarTodos_deberiaRetornarLista() {
        when(triajerRepository.findAll()).thenReturn(List.of(triajer));

        List<TriajerResponseDTO> resultado = triajerService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("triajer@test.cl", resultado.get(0).getEmail());
        assertEquals("Certificación A, Certificación B", resultado.get(0).getCertificaciones());
    }

    @Test
    void listarTodos_sinTriajers_deberiaRetornarListaVacia() {
        when(triajerRepository.findAll()).thenReturn(List.of());

        List<TriajerResponseDTO> resultado = triajerService.listarTodos();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorIdUsuario_conIdValido_deberiaRetornarTriajer() {
        when(triajerRepository.findById(1L)).thenReturn(Optional.of(triajer));

        Optional<Triajer> resultado = triajerService.buscarPorIdUsuario(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Triajer Test", resultado.get().getUsuario().getNombre());
    }

    @Test
    void buscarPorIdUsuario_conIdInexistente_deberiaRetornarVacio() {
        when(triajerRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Triajer> resultado = triajerService.buscarPorIdUsuario(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_conDatosValidos_deberiaCrearTriajer() {
        when(triajerRepository.save(any(Triajer.class))).thenAnswer(i -> i.getArgument(0));

        Triajer resultado = triajerService.crear(usuario, "Certificación C");

        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals("Certificación C", resultado.getCertificaciones());
        verify(triajerRepository).save(any(Triajer.class));
    }
}
