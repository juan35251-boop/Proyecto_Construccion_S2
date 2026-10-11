package application.ports.input;

import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar usuarios.
 *
 * Cada usuario activo puede consultar su propio perfil.
 * Los administradores y supervisores pueden consultar
 * cualquier usuario y listar todos los registrados.
 */
public interface UserQueryUseCase {

    /**
     * Busca un usuario mediante su identificación.
     *
     * @param requester usuario que realiza la consulta
     * @param identification identificación buscada
     * @return usuario encontrado
     */
    User findByIdentification(
            User requester,
            String identification
    );

    /**
     * Lista todos los usuarios registrados.
     *
     * @param requester usuario que realiza la consulta
     * @return lista de usuarios
     */
    List<User> findAll(User requester);
}