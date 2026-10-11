package application.services;

import application.domain.models.Administrator;
import application.domain.models.User;
import application.domain.valueobjects.UserStatus;
import application.ports.input.ChangeUserStatusUseCase;
import application.ports.output.UserRepository;

/**
 * Servicio encargado de cambiar el estado operativo
 * de un usuario.
 *
 * Implementa el puerto de entrada ChangeUserStatusUseCase,
 * permitiendo cambiar el estado de una cuenta sin depender
 * directamente de esta implementación.
 *
 * Únicamente un administrador activo puede activar,
 * inactivar o bloquear usuarios.
 */
public class ChangeUserStatusService
        implements ChangeUserStatusUseCase {

    private final UserRepository userRepository;

    /**
     * Construye el servicio con el repositorio de usuarios.
     *
     * @param userRepository repositorio utilizado
     *                       para guardar usuarios
     */
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
    @Override
    public User changeStatus(
            Administrator administrator,
            User targetUser,
            UserStatus newStatus
    ) {
        validateAdministrator(administrator);
        validateTargetUser(targetUser);
        validateStatus(newStatus);

        validateDifferentUsers(
                administrator,
                targetUser
        );

        /*
         * User realiza el cambio del estado operativo.
         */
        targetUser.changeStatus(newStatus);

        /*
         * Se almacena el usuario actualizado mediante
         * el puerto de salida.
         */
        userRepository.save(targetUser);

        return targetUser;
    }

    /**
     * Valida que la operación sea realizada por
     * un administrador activo.
     *
     * @param administrator administrador responsable
     */
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

    /**
     * Valida que exista el usuario que será modificado.
     *
     * @param targetUser usuario objetivo
     */
    private void validateTargetUser(User targetUser) {
        if (targetUser == null) {
            throw new IllegalArgumentException(
                    "Target user must not be null."
            );
        }
    }

    /**
     * Valida que se haya indicado un nuevo estado.
     *
     * @param status nuevo estado
     */
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
     *
     * @param administrator administrador responsable
     * @param targetUser usuario que será modificado
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