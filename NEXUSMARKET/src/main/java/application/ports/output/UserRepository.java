package application.ports.output;

import application.domain.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida que define las operaciones necesarias
 * para conservar y consultar usuarios.
 *
 * Esta interfaz no indica si la información se almacena
 * en memoria, archivos o una base de datos.
 */
public interface UserRepository {

    /**
     * Comprueba si una identificación ya está registrada.
     *
     * @param identification identificación consultada
     * @return true si la identificación ya existe
     */
    boolean existsByIdentification(String identification);

    /**
     * Comprueba si un correo electrónico ya está registrado.
     *
     * @param email correo electrónico consultado
     * @return true si el correo ya existe
     */
    boolean existsByEmail(String email);

    /**
     * Busca un usuario mediante su identificación.
     *
     * Optional representa la posibilidad de que el usuario
     * no exista, evitando devolver null.
     *
     * @param identification identificación del usuario
     * @return usuario encontrado o un Optional vacío
     */
    Optional<User> findByIdentification(
            String identification
    );

    /**
     * Devuelve todos los usuarios registrados.
     *
     * @return lista de usuarios
     */
    List<User> findAll();

    /**
     * Guarda o actualiza un usuario.
     *
     * @param user usuario que se desea conservar
     */
    void save(User user);
}