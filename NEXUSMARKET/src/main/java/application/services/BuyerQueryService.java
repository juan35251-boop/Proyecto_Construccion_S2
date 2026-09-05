package application.services;

import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio encargado de consultar compradores.
 *
 * Un comprador únicamente puede consultar su propia información.
 * Los administradores y supervisores tienen acceso global de lectura.
 */
public class BuyerQueryService {

    private final UserRepository userRepository;

    /**
     * Construye el servicio con el repositorio de usuarios.
     *
     * @param userRepository repositorio de usuarios
     */
    public BuyerQueryService(UserRepository userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }

        this.userRepository = userRepository;
    }

    /**
     * Busca un comprador por su identificación.
     *
     * @param requestedBy usuario que realiza la consulta
     * @param identification identificación buscada
     * @return comprador encontrado
     */
    public Buyer findByIdentification(
            User requestedBy,
            String identification
    ) {
        validateActiveRequester(requestedBy);
        validateIdentification(identification);

        User foundUser = userRepository
                .findByIdentification(identification)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Buyer was not found."
                        )
                );

        if (!(foundUser instanceof Buyer buyer)) {
            throw new IllegalStateException(
                    "The requested user is not a buyer."
            );
        }

        boolean isSameBuyer =
                requestedBy instanceof Buyer
                        && requestedBy.getIdentification()
                        .equals(buyer.getIdentification());

        if (!isSameBuyer && !hasGlobalAccess(requestedBy)) {
            throw new IllegalStateException(
                    "User is not authorized to query this buyer."
            );
        }

        return buyer;
    }

    /**
     * Obtiene todos los compradores.
     *
     * @param requestedBy administrador o supervisor que consulta
     * @return lista de compradores
     */
    public List<Buyer> findAll(User requestedBy) {
        validateActiveRequester(requestedBy);

        if (!hasGlobalAccess(requestedBy)) {
            throw new IllegalStateException(
                    "User is not authorized to query all buyers."
            );
        }

        return userRepository.findAll()
                .stream()
                .filter(Buyer.class::isInstance)
                .map(Buyer.class::cast)
                .toList();
    }

    /**
     * Valida que el usuario solicitante exista y esté activo.
     */
    private void validateActiveRequester(User requestedBy) {
        if (requestedBy == null) {
            throw new IllegalArgumentException(
                    "Requesting user must not be null."
            );
        }

        if (!requestedBy.isActive()) {
            throw new IllegalStateException(
                    "Only active users can query buyers."
            );
        }
    }

    /**
     * Valida que la identificación no esté vacía.
     */
    private void validateIdentification(String identification) {
        if (identification == null
                || identification.isBlank()) {
            throw new IllegalArgumentException(
                    "Identification must not be empty."
            );
        }
    }

    /**
     * Administradores y supervisores tienen acceso global de lectura.
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole() == SystemRole.ADMINISTRATOR
                || user.getRole() == SystemRole.SUPERVISOR;
    }
}