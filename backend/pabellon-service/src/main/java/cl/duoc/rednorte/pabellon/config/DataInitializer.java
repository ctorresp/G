package cl.duoc.rednorte.pabellon.config;

import cl.duoc.rednorte.pabellon.model.Especialidad;
import cl.duoc.rednorte.pabellon.model.EstadoPabellon;
import cl.duoc.rednorte.pabellon.model.Pabellon;
import cl.duoc.rednorte.pabellon.repository.EspecialidadRepository;
import cl.duoc.rednorte.pabellon.repository.PabellonRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private PabellonRepository pabellonRepository;

    @Override
    public void run(String... args) {
        log.info("Iniciando Datos — pabellon-service");

        String[] especialidadesNombres = {
            "Cardiología", "Neurología", "Traumatología",
            "Cirugía General", "Pediatría", "Ginecología"
        };
        for (String nombre : especialidadesNombres) {
            if (especialidadRepository.findByNombre(nombre).isEmpty()) {
                Especialidad e = new Especialidad();
                e.setNombre(nombre);
                e.setDescripcion(nombre + " — especialidad médica");
                especialidadRepository.save(e);
                log.info("  [ESPECIALIDAD CREADA]  {}", nombre);
            }
        }

        for (int i = 1; i <= 5; i++) {
            String numero = "PAB-0" + i;
            if (pabellonRepository.findByNumero(numero).isEmpty()) {
                Pabellon p = new Pabellon();
                p.setNumero(numero);
                p.setEstado(EstadoPabellon.DISPONIBLE);
                pabellonRepository.save(p);
                log.info("  [PABELLON CREADO]  {}", numero);
            }
        }

        log.info("Datos Iniciados exitosamente — pabellon-service");
    }
}
