package application.services;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.ShipmentQueryUseCase;
import application.ports.output.ShipmentQueryRepository;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio encargado de consultar envíos.
 *
 * Permisos aplicados:
 *
 * - Comprador: consulta envíos de sus pedidos.
 * - Vendedor: consulta envíos con sus productos.
 * - Operador logístico: consulta todos los envíos.
 * - Administrador: consulta todos los envíos.
 * - Supervisor: consulta todos los envíos.
 */
public class ShipmentQueryService
        implements ShipmentQueryUseCase {

    private final ShipmentQueryRepository
            shipmentQueryRepository;

    private final UserRepository userRepository;

    /**
     * Construye el servicio para consultas que ya
     * proporcionan el usuario del dominio.
     *
     * Este constructor se conserva para las pruebas
     * unitarias existentes.
     *
     * @param shipmentQueryRepository repositorio de consultas
     */
    public ShipmentQueryService(
            ShipmentQueryRepository shipmentQueryRepository
    ) {
        validateShipmentQueryRepository(
                shipmentQueryRepository
        );

        this.shipmentQueryRepository =
                shipmentQueryRepository;

        this.userRepository = null;
    }

    /**
     * Construye el servicio completo utilizado por la API.
     *
     * @param shipmentQueryRepository repositorio de envíos
     * @param userRepository repositorio de usuarios
     */
    public ShipmentQueryService(
            ShipmentQueryRepository shipmentQueryRepository,
            UserRepository userRepository
    ) {
        validateShipmentQueryRepository(
                shipmentQueryRepository
        );

        validateUserRepository(userRepository);

        this.shipmentQueryRepository =
                shipmentQueryRepository;

        this.userRepository = userRepository;
    }

    /**
     * Obtiene los envíos visibles para el usuario.
     *
     * @param requestedBy usuario solicitante
     * @return envíos autorizados
     */
    @Override
    public List<Shipment> findAccessibleShipments(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Shipment> shipments =
                shipmentQueryRepository.findAll();

        /*
         * Administradores, supervisores y operadores
         * logísticos pueden consultar todos los envíos.
         */
        if (hasGlobalAccess(requestedBy)
                || requestedBy.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return List.copyOf(shipments);
        }

        /*
         * El comprador solamente consulta envíos
         * pertenecientes a sus pedidos.
         */
        if (requestedBy instanceof Buyer buyer) {
            return shipments.stream()
                    .filter(
                            shipment -> belongsToBuyer(
                                    shipment,
                                    buyer
                            )
                    )
                    .toList();
        }

        /*
         * El vendedor consulta envíos que contienen
         * al menos uno de sus productos.
         */
        if (requestedBy instanceof Seller seller) {
            return shipments.stream()
                    .filter(
                            shipment -> containsSellerProduct(
                                    shipment,
                                    seller
                            )
                    )
                    .toList();
        }

        throw new IllegalStateException(
                "User is not authorized to query shipments."
        );
    }

    /**
     * Consulta envíos mediante la identificación
     * del usuario solicitante.
     *
     * @param requesterIdentification identificación del usuario
     * @return envíos autorizados
     */
    @Override
    public List<Shipment>
    findAccessibleShipmentsByRequesterIdentification(
            String requesterIdentification
    ) {
        User requester =
                findRequester(requesterIdentification);

        return findAccessibleShipments(requester);
    }

    /**
     * Consulta un envío específico y comprueba
     * que el solicitante tenga acceso.
     *
     * @param requesterIdentification identificación del solicitante
     * @param shipmentId identificador del envío
     * @return envío autorizado
     */
    @Override
    public Shipment findAccessibleShipmentByIdentifiers(
            String requesterIdentification,
            String shipmentId
    ) {
        User requester =
                findRequester(requesterIdentification);

        validateText(
                shipmentId,
                "Shipment identifier"
        );

        Shipment shipment =
                shipmentQueryRepository
                        .findById(shipmentId)
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "Shipment was not found."
                                )
                        );

        validateShipmentAccess(
                requester,
                shipment
        );

        return shipment;
    }

    /**
     * Localiza al usuario solicitante.
     */
    private User findRequester(
            String requesterIdentification
    ) {
        validateText(
                requesterIdentification,
                "Requester identification"
        );

        validateUserRepositoryAvailable();

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
     * Comprueba si el usuario puede consultar
     * un envío específico.
     */
    private void validateShipmentAccess(
            User requester,
            Shipment shipment
    ) {
        validateActiveRequester(requester);

        if (hasGlobalAccess(requester)
                || requester.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return;
        }

        if (requester instanceof Buyer buyer
                && belongsToBuyer(shipment, buyer)) {
            return;
        }

        if (requester instanceof Seller seller
                && containsSellerProduct(
                        shipment,
                        seller
                )) {
            return;
        }

        throw new IllegalStateException(
                "User is not authorized to query this shipment."
        );
    }

    /**
     * Comprueba si el envío pertenece
     * a un pedido del comprador.
     */
    private boolean belongsToBuyer(
            Shipment shipment,
            Buyer buyer
    ) {
        if (shipment == null
                || shipment.getOrder() == null
                || shipment.getOrder().getBuyer() == null) {
            return false;
        }

        return buyer.getIdentification().equals(
                shipment.getOrder()
                        .getBuyer()
                        .getIdentification()
        );
    }

    /**
     * Comprueba si el envío contiene productos
     * administrados por el vendedor.
     */
    private boolean containsSellerProduct(
            Shipment shipment,
            Seller seller
    ) {
        if (shipment == null
                || shipment.getOrder() == null) {
            return false;
        }

        for (OrderItem item
                : shipment.getOrder().getItems()) {
            if (seller.managesProduct(
                    item.getProduct()
            )) {
                return true;
            }
        }

        return false;
    }

    /**
     * Valida que el usuario exista y esté activo.
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
                    "Only active users can query shipments."
            );
        }
    }

    /**
     * Indica si el usuario tiene acceso global.
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }

    /**
     * Valida un texto obligatorio.
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
     * Valida el repositorio de consultas.
     */
    private void validateShipmentQueryRepository(
            ShipmentQueryRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Shipment query repository must not be null."
            );
        }
    }

    /**
     * Valida el repositorio de usuarios.
     */
    private void validateUserRepository(
            UserRepository userRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }
    }

    /**
     * Comprueba que el repositorio de usuarios
     * esté disponible.
     */
    private void validateUserRepositoryAvailable() {
        if (userRepository == null) {
            throw new IllegalStateException(
                    "User repository is required for identifier-based operations."
            );
        }
    }
}