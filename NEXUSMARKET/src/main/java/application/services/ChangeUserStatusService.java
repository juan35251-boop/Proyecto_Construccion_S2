package application.services;

import application.domain.models.Administrator;
import application.domain.models.User;
import application.domain.valueobjects.UserStatus;
import application.ports.output.UserRepository;

/**
 * Servicio encargado de cambiar el estado operativo
 * de un usuario.
 *
 * Únicamente un administrador activo puede activar,
 * inactivar o bloquear usuarios.
 */
public class ChangeUserStatusService {

    private final UserRepository userRepository;

    public ChangeUserStatusService(
            UserRepository userRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }

        this.userRepository = userRepository;
    }

    /**
     * Cambia el estado operativo de un usuario.
     *
     * @param administrator administrador responsable
     * @param targetUser usuario que será modificado
     * @param newStatus nuevo estado
     * @return usuario actualizado
     */
    public User changeStatus(
            Administrator administrator,
            User targetUser,
            UserStatus newStatus
    ) {
        validateAdministrator(administrator);
        validateTargetUser(targetUser);
        validateStatus(newStatus);
        validateDifferentUsers(administrator, targetUser);

        targetUser.changeStatus(newStatus);
        userRepository.save(targetUser);

        return targetUser;
    }

    private void validateAdministrator(
            Administrator administrator
    ) {
        if (administrator == null) {
            throw new IllegalArgumentException(
                    "User status change requires an administrator."
            );
        }

        if (!administrator.isActive()) {
            throw new IllegalStateException(
                    "Only an active administrator can change user status."
            );
        }
    }

    private void validateTargetUser(User targetUser) {
        if (targetUser == null) {
            throw new IllegalArgumentException(
                    "Target user must not be null."
            );
        }
    }

    private void validateStatus(UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "User status must not be null."
            );
        }
    }

    /**
     * Evita que un administrador bloquee o inactive
     * accidentalmente su propia cuenta.
     */
    private void validateDifferentUsers(
            Administrator administrator,
            User targetUser
    ) {
        if (administrator.getIdentification().equals(
                targetUser.getIdentification()
        )) {
            throw new IllegalStateException(
                    "Administrator cannot change their own status."
            );
        }
    }
}