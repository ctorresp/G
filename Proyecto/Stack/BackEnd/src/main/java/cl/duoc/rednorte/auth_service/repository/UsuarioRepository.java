package cl.duoc.rednorte.auth_service.repository;

import cl.duoc.rednorte.auth_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repositorio JPA para la entidad Usuario
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByRut(String rut);

    boolean existsByEmail(String email);

    boolean existsByRut(String rut);

    // Carga el usuario activo y sus roles en una sola consulta (evita N+1)
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.email = :email AND u.estado = true")
    Optional<Usuario> findByEmailAndEstadoTrue(@Param("email") String email);

    // Obtiene directamente los nombres de los roles usando JPQL
    @Query("SELECT r.nombreRol FROM Usuario u JOIN u.roles r WHERE u.email = :email AND u.estado = true")
    List<String> findRoleNamesByEmail(@Param("email") String email);
}
