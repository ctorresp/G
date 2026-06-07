package cl.duoc.rednorte.usuarios.repository;

import cl.duoc.rednorte.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByRut(String rut);

    boolean existsByEmail(String email);

    boolean existsByRut(String rut);

    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.estado = true")
    Optional<Usuario> findByEmailAndEstadoTrue(@Param("email") String email);

    List<Usuario> findByRol_NombreRol(String nombreRol);
}
