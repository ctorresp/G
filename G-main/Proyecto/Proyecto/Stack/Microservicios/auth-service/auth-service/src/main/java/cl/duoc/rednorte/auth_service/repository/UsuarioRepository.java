package cl.duoc.rednorte.auth_service.repository;

import cl.duoc.rednorte.auth_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Usuario.
 * Tabla: usuarios — DB_Pacientes
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su email (usado para login JWT).
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca un usuario por su RUT.
     */
    Optional<Usuario> findByRut(String rut);

    /**
     * Verifica si ya existe un usuario con ese email.
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si ya existe un usuario con ese RUT.
     */
    boolean existsByRut(String rut);

    /**
     * Busca un usuario activo por email, cargando sus roles con JOIN FETCH.
     * DISTINCT evita duplicados cuando un usuario tiene múltiples roles.
     *
     * @param email correo electrónico
     * @return Optional con el Usuario activo y sus roles cargados
     */
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.email = :email AND u.estado = true")
    Optional<Usuario> findByEmailAndEstadoTrue(@Param("email") String email);

    /**
     * Obtiene los nombres de roles de un usuario por email usando SQL nativo.
     * Bypasa cualquier issue de sesión Hibernate con @ManyToMany.
     *
     * @param email correo electrónico del usuario
     * @return lista de nombres de roles (ej. "ROLE_ADMIN")
     */
    @Query(value = """
            SELECT r.nombre_rol
            FROM roles r
            INNER JOIN usuario_roles ur ON r.id_rol = ur.id_rol
            INNER JOIN usuarios u ON u.id_usuario = ur.id_usuario
            WHERE u.email = :email AND u.estado = 1
            """, nativeQuery = true)
    List<String> findRoleNamesByEmail(@Param("email") String email);
}
