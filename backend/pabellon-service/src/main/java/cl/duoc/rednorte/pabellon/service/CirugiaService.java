package cl.duoc.rednorte.pabellon.service;

import cl.duoc.rednorte.pabellon.model.Cirugia;
import cl.duoc.rednorte.pabellon.model.Especialidad;
import cl.duoc.rednorte.pabellon.model.EstadoCirugia;
import cl.duoc.rednorte.pabellon.model.EstadoPabellon;
import cl.duoc.rednorte.pabellon.model.Pabellon;
import cl.duoc.rednorte.pabellon.repository.CirugiaRepository;
import cl.duoc.rednorte.pabellon.repository.EspecialidadRepository;
import cl.duoc.rednorte.pabellon.repository.PabellonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class CirugiaService {

    private final CirugiaRepository cirugiaRepository;
    private final PabellonRepository pabellonRepository;
    private final EspecialidadRepository especialidadRepository;

    public CirugiaService(CirugiaRepository cirugiaRepository,
                          PabellonRepository pabellonRepository,
                          EspecialidadRepository especialidadRepository) {
        this.cirugiaRepository = cirugiaRepository;
        this.pabellonRepository = pabellonRepository;
        this.especialidadRepository = especialidadRepository;
    }

    @Transactional(readOnly = true)
    public List<Cirugia> listarTodas() {
        return cirugiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cirugia obtenerPorId(Long id) {
        return cirugiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cirugía no encontrada con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Cirugia> listarPorMedico(String medicoRut) {
        return cirugiaRepository.findByMedicoRutOrderByFechaProgramadaAsc(medicoRut);
    }

    @Transactional(readOnly = true)
    public List<Cirugia> listarPorPaciente(String pacienteRut) {
        return cirugiaRepository.findByPacienteRutOrderByFechaProgramadaDesc(pacienteRut);
    }

    public Cirugia crear(Cirugia cirugia) {
        cirugia.setEstado(EstadoCirugia.PROGRAMADA);
        return cirugiaRepository.save(cirugia);
    }

    public Cirugia actualizar(Long id, Cirugia datos) {
        Cirugia c = obtenerPorId(id);
        c.setPacienteRut(datos.getPacienteRut());
        c.setMedicoRut(datos.getMedicoRut());
        c.setEspecialidad(datos.getEspecialidad());
        c.setPabellon(datos.getPabellon());
        c.setFechaProgramada(datos.getFechaProgramada());
        c.setHoraInicio(datos.getHoraInicio());
        c.setHoraFin(datos.getHoraFin());
        return cirugiaRepository.save(c);
    }

    public void cancelar(Long id, String motivo) {
        Cirugia c = obtenerPorId(id);
        if (EstadoCirugia.CANCELADA.equals(c.getEstado())
                || EstadoCirugia.REALIZADA.equals(c.getEstado())) {
            throw new RuntimeException("No se puede cancelar una cirugía en estado " + c.getEstado());
        }
        c.setEstado(EstadoCirugia.CANCELADA);
        c.setMotivoCancelacion(motivo);
        c.setFechaCancelacion(LocalDateTime.now());
        if (c.getPabellon() != null) {
            Pabellon p = pabellonRepository.findById(c.getPabellon().getIdPabellon())
                    .orElseThrow(() -> new RuntimeException("Pabellón no encontrado"));
            p.setEstado(EstadoPabellon.DISPONIBLE);
            pabellonRepository.save(p);
        }
        cirugiaRepository.save(c);
    }

    public void marcarNoShow(Long id) {
        Cirugia c = obtenerPorId(id);
        if (!EstadoCirugia.PROGRAMADA.equals(c.getEstado())) {
            throw new RuntimeException("Solo se puede marcar NO_SHOW en cirugías PROGRAMADAS");
        }
        c.setEstado(EstadoCirugia.CANCELADA);
        c.setMotivoCancelacion("PACIENTE_NO_ASISTIO");
        c.setFechaCancelacion(LocalDateTime.now());
        if (c.getPabellon() != null) {
            Pabellon p = pabellonRepository.findById(c.getPabellon().getIdPabellon())
                    .orElseThrow(() -> new RuntimeException("Pabellón no encontrado"));
            p.setEstado(EstadoPabellon.DISPONIBLE);
            pabellonRepository.save(p);
        }
        cirugiaRepository.save(c);
    }

    public void completar(Long id) {
        Cirugia c = obtenerPorId(id);
        if (!EstadoCirugia.PROGRAMADA.equals(c.getEstado())) {
            throw new RuntimeException("Solo se pueden completar cirugías PROGRAMADAS");
        }
        c.setEstado(EstadoCirugia.REALIZADA);
        if (c.getPabellon() != null) {
            Pabellon p = pabellonRepository.findById(c.getPabellon().getIdPabellon())
                    .orElseThrow(() -> new RuntimeException("Pabellón no encontrado"));
            p.setEstado(EstadoPabellon.DISPONIBLE);
            pabellonRepository.save(p);
        }
        cirugiaRepository.save(c);
    }

    public Cirugia reasignar(Long cirugiaCanceladaId, String nuevoPacienteRut, String adminRut, String motivo) {
        Cirugia original = obtenerPorId(cirugiaCanceladaId);
        if (!EstadoCirugia.CANCELADA.equals(original.getEstado())) {
            throw new RuntimeException("Solo se pueden reasignar cirugías canceladas");
        }

        Cirugia nueva = new Cirugia();
        nueva.setPacienteRut(nuevoPacienteRut);
        nueva.setMedicoRut(original.getMedicoRut());
        nueva.setEspecialidad(original.getEspecialidad());
        nueva.setPabellon(original.getPabellon());
        nueva.setFechaProgramada(original.getFechaProgramada());
        nueva.setHoraInicio(original.getHoraInicio());
        nueva.setHoraFin(original.getHoraFin());
        nueva.setEstado(EstadoCirugia.PROGRAMADA);
        if (original.getPabellon() != null) {
            Pabellon p = pabellonRepository.findById(original.getPabellon().getIdPabellon())
                    .orElseThrow(() -> new RuntimeException("Pabellón no encontrado"));
            p.setEstado(EstadoPabellon.OCUPADO);
            pabellonRepository.save(p);
        }
        original.setEstado(EstadoCirugia.REPROGRAMADA);
        cirugiaRepository.save(original);

        return cirugiaRepository.save(nueva);
    }

    public Cirugia solicitar(Cirugia cirugia) {
        if (cirugia.getEspecialidad() == null) {
            throw new RuntimeException("La especialidad es obligatoria para solicitar una cirugía");
        }
        Especialidad esp = null;
        if (cirugia.getEspecialidad().getIdEspecialidad() != null) {
            esp = especialidadRepository.findById(cirugia.getEspecialidad().getIdEspecialidad()).orElse(null);
        }
        if (esp == null && cirugia.getEspecialidad().getNombre() != null) {
            esp = especialidadRepository.findByNombre(cirugia.getEspecialidad().getNombre())
                .orElseThrow(() -> new RuntimeException("Especialidad '" + cirugia.getEspecialidad().getNombre() + "' no encontrada en pabellon-service"));
        }
        if (esp == null) {
            throw new RuntimeException("La especialidad es obligatoria para solicitar una cirugía");
        }
        cirugia.setEspecialidad(esp);
        cirugia.setEstado(EstadoCirugia.SOLICITADA);
        cirugia.setTriajeCompletado(false);
        cirugia.setPabellon(null);
        cirugia.setFechaSolicitud(LocalDateTime.now());
        return cirugiaRepository.save(cirugia);
    }

    @Transactional(readOnly = true)
    public List<Cirugia> listarSolicitadas() {
        return cirugiaRepository.findByEstadoAndTriajeCompletado(EstadoCirugia.SOLICITADA, true);
    }

    @Transactional(readOnly = true)
    public List<String> listarPacientesConCirugiaActiva() {
        return cirugiaRepository.findPacientesConCirugiaActiva();
    }

    public List<Cirugia> listarPendientesTriaje() {
        return cirugiaRepository.findByEstadoAndTriajeCompletado(EstadoCirugia.SOLICITADA, false);
    }

    public void marcarTriajeCompletado(Long id) {
        Cirugia c = obtenerPorId(id);
        c.setTriajeCompletado(true);
        cirugiaRepository.save(c);
    }

    public Cirugia programar(Long idCirugia, Pabellon pabellon,
                              LocalDate fechaProgramada, LocalTime horaInicio, LocalTime horaFin) {
        Cirugia c = obtenerPorId(idCirugia);
        if (!EstadoCirugia.SOLICITADA.equals(c.getEstado())) {
            throw new RuntimeException("Solo se pueden programar cirugías en estado SOLICITADA");
        }
        Pabellon p = pabellonRepository.findById(pabellon.getIdPabellon())
                .orElseThrow(() -> new RuntimeException("Pabellón no encontrado"));
        if (!EstadoPabellon.DISPONIBLE.equals(p.getEstado())) {
            throw new RuntimeException("El pabellón seleccionado no está disponible");
        }
        p.setEstado(EstadoPabellon.OCUPADO);
        pabellonRepository.save(p);
        c.setPabellon(p);
        c.setFechaProgramada(fechaProgramada);
        c.setHoraInicio(horaInicio);
        c.setHoraFin(horaFin);
        c.setEstado(EstadoCirugia.PROGRAMADA);
        return cirugiaRepository.save(c);
    }

}
