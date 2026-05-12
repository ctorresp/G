package cl.duoc.rednorte.paciente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.rednorte.paciente.model.Paciente;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Paciente.
 * Tabla: pacientes — DB_Pacientes
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /**
     * Busca el paciente asociado a un usuario por ID de usuario.
     *
     * @param idUsuario ID del usuario
     * @return Optional con el Paciente si existe
     */
    Optional<Paciente> findByUsuario_IdUsuario(Long idUsuario);
}
