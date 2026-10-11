package application.services;

import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.BuyerQueryUseCase;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio encargado de consultar compradores.
 *
 * Implementa el puerto de entrada BuyerQueryUseCase,
 * permitiendo consultar compradores sin depender
 * directamente de esta implementación.
 *
 * Un comprador únicamente puede consultar su propia
 * información. Los administradores y supervisores
 * tienen acceso global de lectura.
 */
public class BuyerQueryService
        implements BuyerQueryUseCase {

    private final UserRepository userRepository;

    /**
     * Construye el servicio con el repositorio de usuarios.
     *
     * @param userRepository repositorio de usuarios
     */
    public BuyerQueryService(
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
     * Busca un comprador por su identificación.
     *
     * @param requestedBy usuario que realiza la consulta
     * @param identification identificación buscada
     * @return comprador encontrado
     */
    @Override
    public Buyer findByIdentification(
            User requestedBy,
            String identification
    ) {
        validateActiveRequester(requestedBy);
        validateIdentification(identification);

        /*
         * Primero se busca el usuario mediante
         * su identificación.
         */
        User foundUser = userRepository
                .findByIdentification(identification)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Buyer was not found."
                        )
                );

        /*
         * Se comprueba que el usuario encontrado
         * realmente sea un comprador.
         */
        if (!(foundUser instanceof Buyer buyer)) {
            throw new IllegalStateException(
                    "The requested user is not a buyer."
            );
        }

        boolean isSameBuyer =
                requestedBy instanceof Buyer
                        && requestedBy.getIdentification()
                        .equals(buyer.getIdentification());

        /*
         * Un comprador únicamente puede consultar
         * su propio perfil.
         */
        if (!isSameBuyer
                && !hasGlobalAccess(requestedBy)) {
            throw new IllegalStateException(
                    "User is not authorized to query this buyer."
            );
        }

        return buyer;
    }

    /**
     * Obtiene todos los compradores.
     *
     * @param requestedBy administrador o supervisor
     *                    que realiza la consulta
     * @return lista de compradores
     */
    @Override
    public List<Buyer> findAll(User requestedBy) {
        validateActiveRequester(requestedBy);

        if (!hasGlobalAccess(requestedBy)) {
            throw new IllegalStateException(
                    "User is not authorized to query all buyers."
            );
        }

        /*
         * Se consultan todos los usuarios y se conservan
         * solamente los que son compradores.
         */
        return userRepository.findAll()
                .stream()
                .filter(Buyer.class::isInstance)
                .map(Buyer.class::cast)
                .toList();
    }

    /**
     * Valida que el usuario solicitante exista
     * y se encuentre activo.
     *
     * @param requestedBy usuario que realiza la consulta
     */
    private void validateActiveRequester(
            User requestedBy
    ) {
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
     * Indica si el usuario tiene acceso global
     * a la información de compradores.
     *
     * @param user usuario que realiza la consulta
     * @return true si es administrador o supervisor
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }
}