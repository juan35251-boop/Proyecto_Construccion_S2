package application.services;

import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.SellerQueryUseCase;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio de aplicación encargado de consultar vendedores.
 *
 * Implementa el puerto de entrada SellerQueryUseCase,
 * permitiendo consultar vendedores sin depender
 * directamente de esta implementación.
 *
 * Aplica las restricciones de acceso según el rol:
 *
 * - Un vendedor solamente puede consultar su información.
 * - Un administrador puede consultar cualquier vendedor.
 * - Un supervisor puede consultar vendedores para monitoreo.
 * - Los demás roles no pueden consultar esta información.
 */
public class SellerQueryService
        implements SellerQueryUseCase {

    private final UserRepository userRepository;

    /**
     * Construye el servicio con el repositorio de usuarios.
     *
     * @param userRepository repositorio utilizado
     *                       para consultar usuarios
     */
    public SellerQueryService(
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
     * Busca un vendedor mediante su identificación.
     *
     * Esta operación recibe directamente al usuario
     * que realiza la consulta.
     *
     * @param requestedBy usuario que realiza la consulta
     * @param identification identificación del vendedor
     * @return vendedor encontrado
     */
    @Override
    public Seller findByIdentification(
            User requestedBy,
            String identification
    ) {
        validateActiveRequester(requestedBy);
        validateIdentification(identification);

        User foundUser = userRepository
                .findByIdentification(identification)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Seller was not found."
                        )
                );

        if (!(foundUser instanceof Seller seller)) {
            throw new IllegalStateException(
                    "The requested user is not a seller."
            );
        }

        validateSellerAccess(
                requestedBy,
                seller
        );

        return seller;
    }

    /**
     * Obtiene todos los vendedores registrados.
     *
     * Esta consulta solamente puede ser realizada por
     * un administrador o supervisor activo.
     *
     * @param requestedBy usuario que solicita la información
     * @return lista de vendedores
     */
    @Override
    public List<Seller> findAll(User requestedBy) {
        validateActiveRequester(requestedBy);
        validateGlobalAccess(requestedBy);

        return userRepository.findAll()
                .stream()
                .filter(Seller.class::isInstance)
                .map(Seller.class::cast)
                .toList();
    }

    /**
     * Busca un vendedor utilizando la identificación
     * del usuario que realiza la consulta.
     *
     * @param requesterIdentification identificación del solicitante
     * @param sellerIdentification identificación del vendedor consultado
     * @return vendedor encontrado
     */
    @Override
    public Seller findByIdentification(
            String requesterIdentification,
            String sellerIdentification
    ) {
        User requestedBy = findRequester(
                requesterIdentification
        );

        return findByIdentification(
                requestedBy,
                sellerIdentification
        );
    }

    /**
     * Obtiene todos los vendedores utilizando
     * la identificación del solicitante.
     *
     * @param requesterIdentification identificación del solicitante
     * @return lista de vendedores
     */
    @Override
    public List<Seller> findAll(
            String requesterIdentification
    ) {
        User requestedBy = findRequester(
                requesterIdentification
        );

        return findAll(requestedBy);
    }

    /**
     * Busca al usuario que realiza la consulta.
     *
     * @param requesterIdentification identificación del solicitante
     * @return usuario solicitante
     */
    private User findRequester(
            String requesterIdentification
    ) {
        if (requesterIdentification == null
                || requesterIdentification.isBlank()) {
            throw new IllegalArgumentException(
                    "Requester identification must not be empty."
            );
        }

        return userRepository
                .findByIdentification(
                        requesterIdentification
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Requesting user was not found."
                        )
                );
    }

    /**
     * Valida que el usuario que consulta exista
     * y esté activo.
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
                    "Only active users can query sellers."
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
     * Comprueba el acceso a un vendedor específico.
     *
     * @param requestedBy usuario que realiza la consulta
     * @param seller vendedor consultado
     */
    private void validateSellerAccess(
            User requestedBy,
            Seller seller
    ) {
        boolean isSameSeller =
                requestedBy instanceof Seller
                        && requestedBy
                        .getIdentification()
                        .equals(
                                seller.getIdentification()
                        );

        if (!isSameSeller
                && !hasGlobalAccess(requestedBy)) {
            throw new IllegalStateException(
                    "User is not authorized to query this seller."
            );
        }
    }

    /**
     * Comprueba que el usuario tenga acceso global.
     *
     * @param requestedBy usuario que realiza la consulta
     */
    private void validateGlobalAccess(
            User requestedBy
    ) {
        if (!hasGlobalAccess(requestedBy)) {
            throw new IllegalStateException(
                    "User is not authorized to query all sellers."
            );
        }
    }

    /**
     * Indica si el usuario posee acceso global
     * de consulta.
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