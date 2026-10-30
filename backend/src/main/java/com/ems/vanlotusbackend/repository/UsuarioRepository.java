package com.ems.vanlotusbackend.repository;

import com.ems.vanlotusbackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de la entidad Usuario.
 * Extiende JpaRepository para obtener todos los métodos CRUD (save, findById,
 * delete, etc.)
 */
@Repository // Indica a Spring que esta interfaz es un componente de Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario por su nombre de usuario (username).
     * Devuelve un Optional para el manejo seguro de nulos.
     * Spring Data JPA infiere automáticamente la consulta SQL:
     * SELECT * FROM usuarios WHERE username = ?
     *
     * @param username Nombre del usuario.
     * @return Usuario correspondiente o Optional.empty()
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Verifica si ya existe un usuario con un determinado username.
     * Útil al registrar un nuevo usuario para validar si el nombre ya está tomado.
     *
     * @param username Nombre de usuario a comprobar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByUsername(String username);
}
