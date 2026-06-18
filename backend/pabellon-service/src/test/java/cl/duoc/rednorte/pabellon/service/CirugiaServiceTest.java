package cl.duoc.rednorte.pabellon.service;

import cl.duoc.rednorte.pabellon.model.Cirugia;
import cl.duoc.rednorte.pabellon.model.Especialidad;
import cl.duoc.rednorte.pabellon.model.EstadoCirugia;
import cl.duoc.rednorte.pabellon.model.EstadoPabellon;
import cl.duoc.rednorte.pabellon.model.Pabellon;
import cl.duoc.rednorte.pabellon.repository.CirugiaRepository;
import cl.duoc.rednorte.pabellon.repository.EspecialidadRepository;
import cl.duoc.rednorte.pabellon.repository.PabellonRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CirugiaServiceTest {

    @Mock private CirugiaRepository cirugiaRepository;
    @Mock private PabellonRepository pabellonRepository;
    @Mock private EspecialidadRepository especialidadRepository;

    @InjectMocks
    private CirugiaService cirugiaService;

    private Cirugia cirugia;
    private Cirugia cirugia2;
    private Pabellon pabellon;
    private Especialidad especialidad;

    @BeforeEach
    void setUp() {
        especialidad = new Especialidad();
        especialidad.setIdEspecialidad(1L);
        especialidad.setNombre("Cirugía General");
        especialidad.setDescripcion("Equipo general");

        pabellon = new Pabellon();
        pabellon.setIdPabellon(1L);
        pabellon.setNumero("Pabellón 1");
        pabellon.setEstado(EstadoPabellon.DISPONIBLE);

        cirugia = new Cirugia();
        cirugia.setIdCirugia(1L);
        cirugia.setPacienteRut("11111111-1");
        cirugia.setMedicoRut("22222222-2");
        cirugia.setEspecialidad(especialidad);
        cirugia.setPabellon(pabellon);
        cirugia.setEstado(EstadoCirugia.PROGRAMADA);

        cirugia2 = new Cirugia();
        cirugia2.setIdCirugia(2L);
        cirugia2.setPacienteRut("33333333-3");
        cirugia2.setMedicoRut("44444444-4");
        cirugia2.setEspecialidad(especialidad);
        cirugia2.setEstado(EstadoCirugia.SOLICITADA);
    }

    @Test
    void listarTodas_deberiaRetornarLista() {
        when(cirugiaRepository.findAll()).thenReturn(List.of(cirugia, cirugia2));

        List<Cirugia> resultado = cirugiaService.listarTodas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(cirugiaRepository).findAll();
    }

    @Test
    void obtenerPorId_conIdValido_deberiaRetornarCirugia() {
        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(cirugia));

        Cirugia resultado = cirugiaService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCirugia());
        assertEquals("11111111-1", resultado.getPacienteRut());
    }

    @Test
    void obtenerPorId_conIdInvalido_deberiaLanzarExcepcion() {
        when(cirugiaRepository.findById(999L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> cirugiaService.obtenerPorId(999L));
        assertTrue(ex.getMessage().contains("no encontrada"));
    }

    @Test
    void listarPorMedico_deberiaRetornarCirugiasDelMedico() {
        when(cirugiaRepository.findByMedicoRutOrderByFechaProgramadaAsc("22222222-2"))
                .thenReturn(List.of(cirugia));

        List<Cirugia> resultado = cirugiaService.listarPorMedico("22222222-2");

        assertEquals(1, resultado.size());
        assertEquals("22222222-2", resultado.get(0).getMedicoRut());
    }

    @Test
    void listarPorPaciente_deberiaRetornarCirugiasDelPaciente() {
        when(cirugiaRepository.findByPacienteRutOrderByFechaProgramadaDesc("11111111-1"))
                .thenReturn(List.of(cirugia));

        List<Cirugia> resultado = cirugiaService.listarPorPaciente("11111111-1");

        assertEquals(1, resultado.size());
        assertEquals("11111111-1", resultado.get(0).getPacienteRut());
    }

    @Test
    void crear_deberiaGuardarYRetornarCirugiaConEstadoProgramada() {
        when(cirugiaRepository.save(any(Cirugia.class))).thenAnswer(i -> {
            Cirugia c = i.getArgument(0);
            c.setIdCirugia(10L);
            return c;
        });

        Cirugia resultado = cirugiaService.crear(cirugia);

        assertNotNull(resultado);
        assertEquals(EstadoCirugia.PROGRAMADA, resultado.getEstado());
        verify(cirugiaRepository).save(any(Cirugia.class));
    }

    @Test
    void actualizar_deberiaActualizarCamposYGuardar() {
        Cirugia existente = new Cirugia();
        existente.setIdCirugia(1L);
        existente.setEstado(EstadoCirugia.PROGRAMADA);

        Cirugia datos = new Cirugia();
        datos.setPacienteRut("99999999-9");
        datos.setMedicoRut("88888888-8");
        datos.setEspecialidad(especialidad);
        datos.setPabellon(pabellon);
        datos.setFechaProgramada(LocalDate.of(2025, 6, 15));
        datos.setHoraInicio(LocalTime.of(8, 0));
        datos.setHoraFin(LocalTime.of(10, 0));

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(cirugiaRepository.save(any(Cirugia.class))).thenAnswer(i -> i.getArgument(0));

        Cirugia resultado = cirugiaService.actualizar(1L, datos);

        assertNotNull(resultado);
        assertEquals("99999999-9", resultado.getPacienteRut());
        assertEquals("88888888-8", resultado.getMedicoRut());
        assertEquals(LocalDate.of(2025, 6, 15), resultado.getFechaProgramada());
    }

    @Test
    void cancelar_conCirugiaProgramada_deberiaCancelarYLiberarPabellon() {
        Cirugia c = new Cirugia();
        c.setIdCirugia(1L);
        c.setEstado(EstadoCirugia.PROGRAMADA);
        c.setPabellon(pabellon);
        c.setPacienteRut("11111111-1");

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(c));
        when(pabellonRepository.findById(1L)).thenReturn(Optional.of(pabellon));
        when(cirugiaRepository.save(any(Cirugia.class))).thenReturn(c);
        when(pabellonRepository.save(any(Pabellon.class))).thenReturn(pabellon);

        cirugiaService.cancelar(1L, "Por motivos médicos");

        assertEquals(EstadoCirugia.CANCELADA, c.getEstado());
        assertEquals("Por motivos médicos", c.getMotivoCancelacion());
        assertNotNull(c.getFechaCancelacion());
        assertEquals(EstadoPabellon.DISPONIBLE, pabellon.getEstado());
        verify(cirugiaRepository).save(any(Cirugia.class));
        verify(pabellonRepository).save(any(Pabellon.class));
    }

    @Test
    void cancelar_conCirugiaCancelada_deberiaLanzarExcepcion() {
        Cirugia c = new Cirugia();
        c.setIdCirugia(1L);
        c.setEstado(EstadoCirugia.CANCELADA);

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(c));

        Exception ex = assertThrows(RuntimeException.class, () -> cirugiaService.cancelar(1L, "motivo"));
        assertTrue(ex.getMessage().contains("No se puede cancelar"));
    }

    @Test
    void cancelar_conCirugiaRealizada_deberiaLanzarExcepcion() {
        Cirugia c = new Cirugia();
        c.setIdCirugia(1L);
        c.setEstado(EstadoCirugia.REALIZADA);

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(c));

        Exception ex = assertThrows(RuntimeException.class, () -> cirugiaService.cancelar(1L, "motivo"));
        assertTrue(ex.getMessage().contains("No se puede cancelar"));
    }

    @Test
    void marcarNoShow_conCirugiaProgramada_deberiaCancelarComoNoShow() {
        Cirugia c = new Cirugia();
        c.setIdCirugia(1L);
        c.setEstado(EstadoCirugia.PROGRAMADA);
        c.setPabellon(pabellon);
        c.setPacienteRut("11111111-1");

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(c));
        when(pabellonRepository.findById(1L)).thenReturn(Optional.of(pabellon));
        when(cirugiaRepository.save(any(Cirugia.class))).thenReturn(c);
        when(pabellonRepository.save(any(Pabellon.class))).thenReturn(pabellon);

        cirugiaService.marcarNoShow(1L);

        assertEquals(EstadoCirugia.CANCELADA, c.getEstado());
        assertEquals("PACIENTE_NO_ASISTIO", c.getMotivoCancelacion());
        assertNotNull(c.getFechaCancelacion());
        assertEquals(EstadoPabellon.DISPONIBLE, pabellon.getEstado());
    }

    @Test
    void marcarNoShow_conCirugiaNoProgramada_deberiaLanzarExcepcion() {
        Cirugia c = new Cirugia();
        c.setIdCirugia(1L);
        c.setEstado(EstadoCirugia.SOLICITADA);

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(c));

        Exception ex = assertThrows(RuntimeException.class, () -> cirugiaService.marcarNoShow(1L));
        assertTrue(ex.getMessage().contains("Solo se puede marcar NO_SHOW"));
    }

    @Test
    void completar_conCirugiaProgramada_deberiaMarcarRealizadaYLiberarPabellon() {
        Cirugia c = new Cirugia();
        c.setIdCirugia(1L);
        c.setEstado(EstadoCirugia.PROGRAMADA);
        c.setPabellon(pabellon);
        c.setPacienteRut("11111111-1");

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(c));
        when(pabellonRepository.findById(1L)).thenReturn(Optional.of(pabellon));
        when(cirugiaRepository.save(any(Cirugia.class))).thenReturn(c);
        when(pabellonRepository.save(any(Pabellon.class))).thenReturn(pabellon);

        cirugiaService.completar(1L);

        assertEquals(EstadoCirugia.REALIZADA, c.getEstado());
        assertEquals(EstadoPabellon.DISPONIBLE, pabellon.getEstado());
    }

    @Test
    void completar_conCirugiaSolicitada_deberiaLanzarExcepcion() {
        Cirugia c = new Cirugia();
        c.setIdCirugia(1L);
        c.setEstado(EstadoCirugia.SOLICITADA);

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(c));

        Exception ex = assertThrows(RuntimeException.class, () -> cirugiaService.completar(1L));
        assertTrue(ex.getMessage().contains("Solo se pueden completar"));
    }

    @Test
    void reasignar_conCirugiaCancelada_deberiaCrearNuevaYMarcarReprogramada() {
        Cirugia cancelada = new Cirugia();
        cancelada.setIdCirugia(1L);
        cancelada.setEstado(EstadoCirugia.CANCELADA);
        cancelada.setPacienteRut("11111111-1");
        cancelada.setMedicoRut("22222222-2");
        cancelada.setEspecialidad(especialidad);
        cancelada.setPabellon(pabellon);

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(cancelada));
        when(pabellonRepository.findById(1L)).thenReturn(Optional.of(pabellon));
        when(cirugiaRepository.save(any(Cirugia.class))).thenAnswer(i -> i.getArgument(0));

        Cirugia resultado = cirugiaService.reasignar(1L, "55555555-5", "admin", "reasignación");

        assertNotNull(resultado);
        assertEquals("55555555-5", resultado.getPacienteRut());
        assertEquals(EstadoCirugia.PROGRAMADA, resultado.getEstado());
        assertEquals(EstadoCirugia.REPROGRAMADA, cancelada.getEstado());
        assertEquals(EstadoPabellon.OCUPADO, pabellon.getEstado());
        verify(cirugiaRepository, times(2)).save(any(Cirugia.class));
    }

    @Test
    void reasignar_conCirugiaNoCancelada_deberiaLanzarExcepcion() {
        Cirugia c = new Cirugia();
        c.setIdCirugia(1L);
        c.setEstado(EstadoCirugia.PROGRAMADA);

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(c));

        Exception ex = assertThrows(RuntimeException.class,
                () -> cirugiaService.reasignar(1L, "55555555-5", "admin", "motivo"));
        assertTrue(ex.getMessage().contains("reasignar cirugías canceladas"));
    }

    @Test
    void solicitar_conDatosValidos_deberiaGuardarSolicitada() {
        Cirugia input = new Cirugia();
        input.setPacienteRut("11111111-1");
        input.setMedicoRut("22222222-2");
        input.setEspecialidad(especialidad);

        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));
        when(cirugiaRepository.save(any(Cirugia.class))).thenAnswer(i -> {
            Cirugia c = i.getArgument(0);
            c.setIdCirugia(99L);
            return c;
        });

        Cirugia resultado = cirugiaService.solicitar(input);

        assertNotNull(resultado);
        assertEquals(EstadoCirugia.SOLICITADA, resultado.getEstado());
        assertFalse(resultado.isTriajeCompletado());
        assertNull(resultado.getPabellon());
        assertNotNull(resultado.getFechaSolicitud());
    }

    @Test
    void solicitar_conEspecialidadPorNombre_deberiaResolverYGuardar() {
        Cirugia input = new Cirugia();
        input.setPacienteRut("11111111-1");
        input.setMedicoRut("22222222-2");
        Especialidad espRef = new Especialidad();
        espRef.setNombre("Cirugía General");
        input.setEspecialidad(espRef);

        when(especialidadRepository.findByNombre("Cirugía General")).thenReturn(Optional.of(especialidad));
        when(cirugiaRepository.save(any(Cirugia.class))).thenAnswer(i -> i.getArgument(0));

        Cirugia resultado = cirugiaService.solicitar(input);

        assertNotNull(resultado);
        assertEquals(EstadoCirugia.SOLICITADA, resultado.getEstado());
        assertNotNull(resultado.getEspecialidad().getIdEspecialidad());
    }

    @Test
    void solicitar_sinEspecialidad_deberiaLanzarExcepcion() {
        Cirugia input = new Cirugia();
        input.setPacienteRut("11111111-1");
        input.setMedicoRut("22222222-2");

        Exception ex = assertThrows(RuntimeException.class, () -> cirugiaService.solicitar(input));
        assertTrue(ex.getMessage().contains("especialidad es obligatoria"));
    }

    @Test
    void listarSolicitadas_deberiaRetornarCirugiasConTriajeCompletado() {
        when(cirugiaRepository.findByEstadoAndTriajeCompletado(EstadoCirugia.SOLICITADA, true))
                .thenReturn(List.of(cirugia2));

        List<Cirugia> resultado = cirugiaService.listarSolicitadas();

        assertEquals(1, resultado.size());
        verify(cirugiaRepository).findByEstadoAndTriajeCompletado(EstadoCirugia.SOLICITADA, true);
    }

    @Test
    void listarPendientesTriaje_deberiaRetornarCirugiasSinTriajeCompletado() {
        when(cirugiaRepository.findByEstadoAndTriajeCompletado(EstadoCirugia.SOLICITADA, false))
                .thenReturn(List.of(cirugia2));

        List<Cirugia> resultado = cirugiaService.listarPendientesTriaje();

        assertEquals(1, resultado.size());
        verify(cirugiaRepository).findByEstadoAndTriajeCompletado(EstadoCirugia.SOLICITADA, false);
    }

    @Test
    void marcarTriajeCompletado_deberiaActualizarFlag() {
        Cirugia c = new Cirugia();
        c.setIdCirugia(1L);
        c.setTriajeCompletado(false);

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(c));
        when(cirugiaRepository.save(any(Cirugia.class))).thenAnswer(i -> i.getArgument(0));

        cirugiaService.marcarTriajeCompletado(1L);

        assertTrue(c.isTriajeCompletado());
        verify(cirugiaRepository).save(c);
    }

    @Test
    void programar_conDatosValidos_deberiaOcuparPabellonYProgramar() {
        Cirugia c = new Cirugia();
        c.setIdCirugia(1L);
        c.setEstado(EstadoCirugia.SOLICITADA);
        c.setPacienteRut("11111111-1");

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(c));
        when(pabellonRepository.findById(1L)).thenReturn(Optional.of(pabellon));
        when(cirugiaRepository.save(any(Cirugia.class))).thenReturn(c);
        when(pabellonRepository.save(any(Pabellon.class))).thenReturn(pabellon);

        LocalDate fecha = LocalDate.of(2025, 7, 1);
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fin = LocalTime.of(10, 0);

        Cirugia resultado = cirugiaService.programar(1L, pabellon, fecha, inicio, fin);

        assertNotNull(resultado);
        assertEquals(EstadoCirugia.PROGRAMADA, c.getEstado());
        assertEquals(EstadoPabellon.OCUPADO, pabellon.getEstado());
        assertEquals(fecha, c.getFechaProgramada());
        assertEquals(inicio, c.getHoraInicio());
        assertEquals(fin, c.getHoraFin());
    }

    @Test
    void programar_conCirugiaNoSolicitada_deberiaLanzarExcepcion() {
        Cirugia c = new Cirugia();
        c.setIdCirugia(1L);
        c.setEstado(EstadoCirugia.PROGRAMADA);

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(c));

        Exception ex = assertThrows(RuntimeException.class,
                () -> cirugiaService.programar(1L, pabellon, LocalDate.now(), LocalTime.now(), LocalTime.now()));
        assertTrue(ex.getMessage().contains("estado SOLICITADA"));
    }

    @Test
    void programar_conPabellonNoDisponible_deberiaLanzarExcepcion() {
        Cirugia c = new Cirugia();
        c.setIdCirugia(1L);
        c.setEstado(EstadoCirugia.SOLICITADA);

        Pabellon ocupado = new Pabellon();
        ocupado.setIdPabellon(2L);
        ocupado.setEstado(EstadoPabellon.OCUPADO);

        when(cirugiaRepository.findById(1L)).thenReturn(Optional.of(c));
        when(pabellonRepository.findById(2L)).thenReturn(Optional.of(ocupado));

        Exception ex = assertThrows(RuntimeException.class,
                () -> cirugiaService.programar(1L, ocupado, LocalDate.now(), LocalTime.now(), LocalTime.now()));
        assertTrue(ex.getMessage().contains("no está disponible"));
    }
}
