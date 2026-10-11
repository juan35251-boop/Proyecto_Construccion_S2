package application.ports.input;

import application.domain.models.Administrator;
import application.domain.models.User;
import application.domain.valueobjects.UserStatus;

/**
 * Puerto de entrada que define el caso de uso
 * para cambiar el estado operativo de un usuario.
 *
 * Solamente un administrador activo puede activar,
 * inactivar o bloquear cuentas.
 */
public interface ChangeUserStatusUseCase {

    /**
     * Cambia el estado operativo de un usuario.
     *
     * @param administrator administrador responsable
     * @param targetUser usuario que será modificado
     * @param newStatus nuevo estado del usuario
     * @return usuario actualizado
     */
    User changeStatus(
            Administrator administrator,
            User targetUser,
            UserStatus newStatus
    );
}