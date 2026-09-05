package application.services;

import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio de aplicación encargado de consultar vendedores.
 *
 * Aplica las restricciones de acceso según el rol:
 *
 * - Un vendedor solamente puede consultar su propia información.
 * - Un administrador puede consultar cualquier vendedor.
 * - Un supervisor puede consultar vendedores para monitoreo.
 * - Los demás roles no pueden consultar esta información.
 */
public class SellerQueryService {

    private final UserRepository userRepository;

    /**
     * Construye el servicio con el repositorio de usuarios.
     *
     * @param userRepository repositorio utilizado para consultar usuarios
     */
    public SellerQueryService(UserRepository userRepository) {
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
     * @param requestedBy usuario que realiza la consulta
     * @param identification identificación del vendedor
     * @return vendedor encontrado
     */
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

        validateSellerAccess(requestedBy, seller);

        return seller;
    }

    /**
     * Obtiene todos los vendedores registrados.
     *
     * Esta consulta solamente puede ser realizada por un
     * administrador o supervisor activo.
     *
     * @param requestedBy usuario que solicita la información
     * @return lista de vendedores
     */
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
     * Valida que el usuario que consulta exista y esté activo.
     */
    private void validateActiveRequester(User requestedBy) {
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
     * Comprueba el acceso a un vendedor específico.
     */
    private void validateSellerAccess(
            User requestedBy,
            Seller seller
    ) {
        boolean isSameSeller =
                requestedBy instanceof Seller
                        && requestedBy.getIdentification()
                        .equals(seller.getIdentification());

        if (!isSameSeller && !hasGlobalAccess(requestedBy)) {
            throw new IllegalStateException(
                    "User is not authorized to query this seller."
            );
        }
    }

    /**
     * Comprueba que el usuario tenga acceso global.
     */
    private void validateGlobalAccess(User requestedBy) {
        if (!hasGlobalAccess(requestedBy)) {
            throw new IllegalStateException(
                    "User is not authorized to query all sellers."
            );
        }
    }

    /**
     * El administrador y el supervisor tienen acceso global
     * de consulta, pero esta autorización no les permite modificar
     * información fuera de sus responsabilidades.
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole() == SystemRole.ADMINISTRATOR
                || user.getRole() == SystemRole.SUPERVISOR;
    }
}
