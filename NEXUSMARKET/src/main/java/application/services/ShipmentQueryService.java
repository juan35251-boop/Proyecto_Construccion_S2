package application.services;

import application.domain.models.Buyer;
import application.domain.models.OrderItem;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.ShipmentQueryUseCase;
import application.ports.output.ShipmentQueryRepository;

import java.util.List;

/**
 * Servicio encargado de consultar envíos.
 *
 * Implementa el puerto de entrada ShipmentQueryUseCase,
 * permitiendo consultar envíos sin depender directamente
 * de esta implementación.
 *
 * Permisos:
 *
 * - El comprador consulta los envíos de sus pedidos.
 * - El vendedor consulta envíos con sus productos.
 * - El operador logístico consulta todos los envíos.
 * - El administrador consulta todos los envíos.
 * - El supervisor consulta todos los envíos.
 */
public class ShipmentQueryService
        implements ShipmentQueryUseCase {

    private final ShipmentQueryRepository
            shipmentQueryRepository;

    /**
     * Construye el servicio con el repositorio requerido.
     *
     * @param shipmentQueryRepository repositorio
     *                                de consulta de envíos
     */
    public ShipmentQueryService(
            ShipmentQueryRepository shipmentQueryRepository
    ) {
        if (shipmentQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Shipment query repository must not be null."
            );
        }

        this.shipmentQueryRepository =
                shipmentQueryRepository;
    }

    /**
     * Obtiene los envíos visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
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
         * El comprador solamente puede consultar
         * los envíos de sus pedidos.
         */
        if (requestedBy instanceof Buyer buyer) {
            return shipments.stream()
                    .filter(
                            shipment ->
                                    shipment.getOrder()
                                            .getBuyer() == buyer
                    )
                    .toList();
        }

        /*
         * El vendedor consulta envíos que contengan
         * al menos uno de sus productos.
         */
        if (requestedBy instanceof Seller seller) {
            return shipments.stream()
                    .filter(
                            shipment ->
                                    containsSellerProduct(
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
     * Comprueba si el envío contiene productos
     * pertenecientes al vendedor.
     *
     * @param shipment envío consultado
     * @param seller vendedor que realiza la consulta
     * @return true si contiene productos del vendedor
     */
    private boolean containsSellerProduct(
            Shipment shipment,
            Seller seller
    ) {
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
     * Valida que quien consulta sea un usuario activo.
     *
     * @param requestedBy usuario solicitante
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
     * Indica si el usuario tiene acceso global
     * de lectura sobre los envíos.
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