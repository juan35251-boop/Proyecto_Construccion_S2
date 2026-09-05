package application.services;

import application.domain.models.User;
import application.ports.output.UserRepository;

/**
 * Servicio de aplicación encargado de actualizar
 * el perfil propio de un usuario.
 *
 * Todos los roles pueden cambiar su nombre y correo mientras
 * tengan un estado activo. La identificación, el rol y el estado
 * operativo no se modifican mediante este servicio.
 */
public class UpdateOwnProfileService {

    private final UserRepository userRepository;

    /**
     * Construye el servicio con el repositorio de usuarios.
     *
     * @param userRepository repositorio utilizado para validar
     *                       y guardar usuarios
     */
    public UpdateOwnProfileService(
            UserRepository userRepository
    ) {
        validateRepository(userRepository);
        this.userRepository = userRepository;
    }

    /**
     * Actualiza el nombre y el correo electrónico del usuario.
     *
     * El correo nuevo debe continuar siendo único dentro
     * de toda la plataforma.
     *
     * @param user usuario que actualiza su propio perfil
     * @param newFullName nuevo nombre completo
     * @param newEmail nuevo correo electrónico
     * @return usuario actualizado
     */
    public User update(
            User user,
            String newFullName,
            String newEmail
    ) {
        validateUser(user);
        validateActiveUser(user);

        /*
         * Se validan ambos datos antes de modificar el objeto.
         * Esto evita cambiar el nombre si después descubrimos
         * que el correo no es válido.
         */
        validateText(newFullName, "Full name");
        validateText(newEmail, "Email");

        validateUniqueEmail(user, newEmail);

        user.changeFullName(newFullName);
        user.changeEmail(newEmail);

        userRepository.save(user);

        return user;
    }

    /**
     * Valida que el repositorio exista.
     */
    private void validateRepository(
            UserRepository userRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }
    }

    /**
     * Valida que exista un usuario.
     */
    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "Profile update requires a user."
            );
        }
    }

    /**
     * Comprueba que el usuario tenga un estado activo.
     */
    private void validateActiveUser(User user) {
        if (!user.isActive()) {
            throw new IllegalStateException(
                    "Only an active user can update their profile."
            );
        }
    }

    /**
     * Valida que un texto obligatorio tenga contenido.
     */
    private void validateText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }

    /**
     * Comprueba que el nuevo correo no pertenezca
     * a otro usuario.
     *
     * Si el correo no cambió, no se considera duplicado
     * porque continúa perteneciendo al mismo usuario.
     */
    private void validateUniqueEmail(
            User user,
            String newEmail
    ) {
        boolean emailChanged =
                !user.getEmail().equals(newEmail);

        if (emailChanged
                && userRepository.existsByEmail(newEmail)) {
            throw new IllegalStateException(
                    "Email is already registered."
            );
        }
    }
}