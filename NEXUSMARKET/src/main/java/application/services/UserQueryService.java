package application.services;

import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.UserQueryUseCase;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio encargado de consultar usuarios.
 *
 * Implementa el puerto de entrada UserQueryUseCase,
 * permitiendo consultar usuarios sin depender
 * directamente de esta implementación.
 *
 * Cada participante puede consultar su propio perfil.
 * Administradores y supervisores pueden consultar
 * cualquier usuario y listar todos los registrados.
 */
public class UserQueryService
        implements UserQueryUseCase {

    private final UserRepository userRepository;

    /**
     * Construye el servicio con el repositorio de usuarios.
     *
     * @param userRepository repositorio utilizado
     *                       para consultar usuarios
     */
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
    @Override
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

        /*
         * El repositorio devuelve Optional porque
         * el usuario consultado podría no existir.
         */
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
     *
     * @param requester usuario que realiza la consulta
     * @return lista inmutable de usuarios
     */
    @Override
    public List<User> findAll(User requester) {
        validateActiveRequester(requester);
        validateGlobalQueryPermission(requester);

        /*
         * List.copyOf evita que el consumidor modifique
         * directamente la colección del repositorio.
         */
        return List.copyOf(
                userRepository.findAll()
        );
    }

    /**
     * Valida que la consulta sea realizada
     * por un usuario activo.
     *
     * @param requester usuario que realiza la consulta
     */
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

    /**
     * Valida que la identificación tenga contenido.
     *
     * @param identification identificación consultada
     */
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

    /**
     * Comprueba que el usuario pueda consultar
     * el perfil solicitado.
     *
     * @param requester usuario que realiza la consulta
     * @param identification identificación buscada
     */
    private void validateQueryPermission(
            User requester,
            String identification
    ) {
        boolean isOwnProfile =
                requester.getIdentification()
                        .equals(identification);

        if (!isOwnProfile
                && !hasGlobalAccess(requester)) {
            throw new IllegalStateException(
                    "User is not authorized to query this profile."
            );
        }
    }

    /**
     * Comprueba que el usuario pueda consultar
     * la lista completa.
     *
     * @param requester usuario que realiza la consulta
     */
    private void validateGlobalQueryPermission(
            User requester
    ) {
        if (!hasGlobalAccess(requester)) {
            throw new IllegalStateException(
                    "User is not authorized to list all users."
            );
        }
    }

    /**
     * Indica si el usuario tiene acceso
     * a las consultas globales.
     *
     * @param requester usuario que realiza la consulta
     * @return true si es administrador o supervisor
     */
    private boolean hasGlobalAccess(User requester) {
        return requester.getRole()
                == SystemRole.ADMINISTRATOR
                || requester.getRole()
                == SystemRole.SUPERVISOR;
    }
}