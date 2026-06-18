package cl.duoc.rednorte.usuarios.service;

import cl.duoc.rednorte.usuarios.dto.MedicoResponseDTO;
import cl.duoc.rednorte.usuarios.model.Especialidad;
import cl.duoc.rednorte.usuarios.model.Medico;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.usuarios.repository.EspecialidadRepository;
import cl.duoc.rednorte.usuarios.repository.MedicoRepository;

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
class MedicoServiceTest {

    @Mock private MedicoRepository medicoRepository;
    @Mock private EspecialidadRepository especialidadRepository;
    @Mock private UsuarioService usuarioService;

    @InjectMocks private MedicoService medicoService;

    private Usuario usuario;
    private Medico medico;
    private Especialidad especialidad;

    @BeforeEach
    void setUp() {
        especialidad = new Especialidad();
        especialidad.setIdEspecialidad(1L);
        especialidad.setNombre("Cirugía General");
        especialidad.setDescripcion("Cirugía General");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRut("11111111-1");
        usuario.setNombre("Medico Test");
        usuario.setEmail("medico@test.cl");
        usuario.setContrasena("encoded");

        medico = new Medico();
        medico.setIdUsuario(1L);
        medico.setUsuario(usuario);
        medico.setEspecialidad(especialidad);
    }

    @Test
    void listarTodos_deberiaRetornarLista() {
        when(medicoRepository.findAll()).thenReturn(List.of(medico));

        List<MedicoResponseDTO> resultado = medicoService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("medico@test.cl", resultado.get(0).getEmail());
        assertEquals(1L, resultado.get(0).getEspecialidadId());
        assertEquals("Cirugía General", resultado.get(0).getEspecialidadNombre());
    }

    @Test
    void listarTodos_sinMedicos_deberiaRetornarListaVacia() {
        when(medicoRepository.findAll()).thenReturn(List.of());

        List<MedicoResponseDTO> resultado = medicoService.listarTodos();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorIdUsuario_conIdValido_deberiaRetornarMedico() {
        when(medicoRepository.findByUsuario_IdUsuario(1L)).thenReturn(Optional.of(medico));

        Optional<Medico> resultado = medicoService.buscarPorIdUsuario(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Medico Test", resultado.get().getUsuario().getNombre());
    }

    @Test
    void buscarPorIdUsuario_conIdInexistente_deberiaRetornarVacio() {
        when(medicoRepository.findByUsuario_IdUsuario(99L)).thenReturn(Optional.empty());

        Optional<Medico> resultado = medicoService.buscarPorIdUsuario(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorRut_conRutValido_deberiaRetornarMedico() {
        when(medicoRepository.findByUsuario_Rut("11111111-1")).thenReturn(Optional.of(medico));

        Optional<Medico> resultado = medicoService.buscarPorRut("11111111-1");

        assertTrue(resultado.isPresent());
        assertEquals("medico@test.cl", resultado.get().getUsuario().getEmail());
    }

    @Test
    void buscarPorRut_conRutInexistente_deberiaRetornarVacio() {
        when(medicoRepository.findByUsuario_Rut("00000000-0")).thenReturn(Optional.empty());

        Optional<Medico> resultado = medicoService.buscarPorRut("00000000-0");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_conEspecialidadIdValido_deberiaCrearMedico() {
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));
        when(medicoRepository.save(any(Medico.class))).thenAnswer(i -> i.getArgument(0));

        Medico resultado = medicoService.crear(usuario, 1L);

        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(especialidad, resultado.getEspecialidad());
        verify(medicoRepository).save(any(Medico.class));
    }

    @Test
    void crear_conEspecialidadIdInexistente_deberiaLanzarExcepcion() {
        when(especialidadRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> medicoService.crear(usuario, 99L));
        assertTrue(ex.getMessage().contains("Especialidad no encontrada"));
    }

    @Test
    void crear_conEspecialidadIdNull_deberiaUsarDefecto() {
        when(especialidadRepository.findByNombre("Cirugía General")).thenReturn(Optional.of(especialidad));
        when(medicoRepository.save(any(Medico.class))).thenAnswer(i -> i.getArgument(0));

        Medico resultado = medicoService.crear(usuario, null);

        assertNotNull(resultado);
        assertEquals(especialidad, resultado.getEspecialidad());
        verify(especialidadRepository).findByNombre("Cirugía General");
    }

    @Test
    void crear_conEspecialidadIdNullYDefectoInexistente_deberiaLanzarExcepcion() {
        when(especialidadRepository.findByNombre("Cirugía General")).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> medicoService.crear(usuario, null));
        assertTrue(ex.getMessage().contains("Especialidad por defecto no encontrada"));
    }

    @Test
    void crearConUsuario_deberiaDelegarACrear() {
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));
        when(medicoRepository.save(any(Medico.class))).thenAnswer(i -> i.getArgument(0));

        Medico resultado = medicoService.crearConUsuario(usuario, 1L);

        assertNotNull(resultado);
        assertEquals(especialidad, resultado.getEspecialidad());
    }

    @Test
    void obtenerOCrear_conMedicoExistente_deberiaRetornarExistente() {
        when(medicoRepository.findByUsuario_IdUsuario(1L)).thenReturn(Optional.of(medico));

        Medico resultado = medicoService.obtenerOCrear(usuario);

        assertNotNull(resultado);
        assertEquals("Medico Test", resultado.getUsuario().getNombre());
        verify(medicoRepository, never()).save(any());
    }

    @Test
    void obtenerOCrear_conMedicoInexistente_deberiaCrearNuevo() {
        when(medicoRepository.findByUsuario_IdUsuario(1L)).thenReturn(Optional.empty());
        when(especialidadRepository.findByNombre("Cirugía General")).thenReturn(Optional.of(especialidad));
        when(medicoRepository.save(any(Medico.class))).thenAnswer(i -> i.getArgument(0));

        Medico resultado = medicoService.obtenerOCrear(usuario);

        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(especialidad, resultado.getEspecialidad());
        verify(medicoRepository).save(any(Medico.class));
    }
}
