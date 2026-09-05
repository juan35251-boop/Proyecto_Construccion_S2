package application.services;

import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio encargado de consultar usuarios.
 *
 * Cada participante puede consultar su propio perfil.
 * Administradores y supervisores pueden consultar
 * cualquier usuario y listar todos los registrados.
 */
public class UserQueryService {

    private final UserRepository userRepository;

    public UserQueryService(
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
     * Busca un usuario mediante su identificación.
     *
     * @param requester usuario que realiza la consulta
     * @param identification identificación buscada
     * @return usuario encontrado
     */
    public User findByIdentification(
            User requester,
            String identification
    ) {
        validateActiveRequester(requester);
        validateIdentification(identification);
        validateQueryPermission(
                requester,
                identification
        );

        return userRepository
                .findByIdentification(identification)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "User was not found."
                        )
                );
    }

    /**
     * Lista todos los usuarios.
     *
     * Solamente administradores y supervisores
     * tienen acceso a esta consulta global.
     */
    public List<User> findAll(User requester) {
        validateActiveRequester(requester);
        validateGlobalQueryPermission(requester);

        return List.copyOf(userRepository.findAll());
    }

    private void validateActiveRequester(User requester) {
        if (requester == null) {
            throw new IllegalArgumentException(
                    "User query requires a requester."
            );
        }

        if (!requester.isActive()) {
            throw new IllegalStateException(
                    "Only active users can query user information."
            );
        }
    }

    private void validateIdentification(
            String identification
    ) {
        if (identification == null
                || identification.isBlank()) {
            throw new IllegalArgumentException(
                    "Identification must not be empty."
            );
        }
    }

    private void validateQueryPermission(
            User requester,
            String identification
    ) {
        boolean isOwnProfile =
                requester.getIdentification()
                        .equals(identification);

        if (!isOwnProfile && !hasGlobalAccess(requester)) {
            throw new IllegalStateException(
                    "User is not authorized to query this profile."
            );
        }
    }

    private void validateGlobalQueryPermission(
            User requester
    ) {
        if (!hasGlobalAccess(requester)) {
            throw new IllegalStateException(
                    "User is not authorized to list all users."
            );
        }
    }

    private boolean hasGlobalAccess(User requester) {
        return requester.getRole()
                == SystemRole.ADMINISTRATOR
                || requester.getRole()
                == SystemRole.SUPERVISOR;
    }
}