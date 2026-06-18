package cl.duoc.rednorte.auth_service.repository;

import cl.duoc.rednorte.auth_service.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositorio JPA para la entidad Rol
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombreRol(String nombreRol);

    boolean existsByNombreRol(String nombreRol);
}
