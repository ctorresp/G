package cl.duoc.rednorte.auth_service.repository;

import cl.duoc.rednorte.auth_service.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Rol.
 * Tabla: roles — DB_Pacientes
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Busca un rol por su nombre (ej. "ROLE_ADMIN").
     *
     * @param nombreRol nombre exacto del rol
     * @return Optional con el Rol si existe
     */
    Optional<Rol> findByNombreRol(String nombreRol);

    /**
     * Verifica si ya existe un rol con ese nombre.
     *
     * @param nombreRol nombre del rol a verificar
     * @return true si existe, false si no
     */
    boolean existsByNombreRol(String nombreRol);
}
