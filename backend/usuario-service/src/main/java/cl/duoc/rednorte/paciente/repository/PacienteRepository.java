package cl.duoc.rednorte.paciente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.rednorte.paciente.model.Paciente;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByRut(String rut);

    List<Paciente> findByMedicoAsignado_IdUsuarioOrderByIdPacienteDesc(Long idMedico);
}
