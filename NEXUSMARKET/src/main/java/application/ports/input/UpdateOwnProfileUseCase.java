package application.ports.input;

import application.domain.models.User;

/**
 * Puerto de entrada que define el caso de uso
 * para actualizar el perfil propio de un usuario.
 *
 * Todos los roles pueden modificar su nombre y correo
 * electrónico mientras su cuenta esté activa.
 */
public interface UpdateOwnProfileUseCase {

    /**
     * Actualiza el nombre y el correo del usuario.
     *
     * El nuevo correo debe continuar siendo único
     * dentro de NexusMarket.
     *
     * @param user usuario que actualiza su perfil
     * @param newFullName nuevo nombre completo
     * @param newEmail nuevo correo electrónico
     * @return usuario actualizado
     */
    User update(
            User user,
            String newFullName,
            String newEmail
    );
}