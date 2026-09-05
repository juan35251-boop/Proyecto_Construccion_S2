package application.services;

import application.domain.models.Buyer;
import application.domain.models.OrderItem;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.output.ShipmentQueryRepository;

import java.util.List;

/**
 * Servicio encargado de consultar envíos.
 *
 * Permisos:
 *
 * - El comprador consulta los envíos de sus pedidos.
 * - El vendedor consulta envíos que contienen sus productos.
 * - El operador logístico consulta todos los envíos.
 * - El administrador y el supervisor tienen acceso global de lectura.
 */
public class ShipmentQueryService {

    private final ShipmentQueryRepository shipmentQueryRepository;

    /**
     * Construye el servicio con el repositorio requerido.
     *
     * @param shipmentQueryRepository repositorio de consulta de envíos
     */
    public ShipmentQueryService(
            ShipmentQueryRepository shipmentQueryRepository
    ) {
        if (shipmentQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Shipment query repository must not be null."
            );
        }

        this.shipmentQueryRepository = shipmentQueryRepository;
    }

    /**
     * Obtiene los envíos visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return envíos autorizados
     */
    public List<Shipment> findAccessibleShipments(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Shipment> shipments =
                shipmentQueryRepository.findAll();

        if (hasGlobalAccess(requestedBy)
                || requestedBy.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return List.copyOf(shipments);
        }

        if (requestedBy instanceof Buyer buyer) {
            return shipments.stream()
                    .filter(
                            shipment ->
                                    shipment.getOrder()
                                            .getBuyer() == buyer
                    )
                    .toList();
        }

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
     * Comprueba si el envío contiene productos del vendedor.
     */
    private boolean containsSellerProduct(
            Shipment shipment,
            Seller seller
    ) {
        for (OrderItem item
                : shipment.getOrder().getItems()) {
            if (seller.managesProduct(item.getProduct())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Valida que quien consulta sea un usuario activo.
     */
    private void validateActiveRequester(User requestedBy) {
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
     * Administradores y supervisores tienen acceso global de lectura.
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole() == SystemRole.ADMINISTRATOR
                || user.getRole() == SystemRole.SUPERVISOR;
    }
}