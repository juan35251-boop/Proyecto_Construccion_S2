package application.services;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.output.OrderQueryRepository;

import java.util.List;

/**
 * Servicio de aplicación encargado de consultar pedidos.
 *
 * Permisos:
 *
 * - El comprador consulta únicamente sus pedidos.
 * - El vendedor consulta pedidos que contienen sus productos.
 * - El operador logístico consulta pedidos con productos físicos.
 * - El administrador y el supervisor consultan todos los pedidos.
 */
public class OrderQueryService {

    private final OrderQueryRepository orderQueryRepository;

    /**
     * Construye el servicio con el repositorio de consulta.
     *
     * @param orderQueryRepository repositorio de consulta de pedidos
     */
    public OrderQueryService(
            OrderQueryRepository orderQueryRepository
    ) {
        if (orderQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Order query repository must not be null."
            );
        }

        this.orderQueryRepository = orderQueryRepository;
    }

    /**
     * Obtiene los pedidos visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return pedidos autorizados
     */
    public List<Order> findAccessibleOrders(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Order> orders = orderQueryRepository.findAll();

        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(orders);
        }

        if (requestedBy instanceof Buyer buyer) {
            return orders.stream()
                    .filter(order -> belongsToBuyer(order, buyer))
                    .toList();
        }

        if (requestedBy instanceof Seller seller) {
            return orders.stream()
                    .filter(order -> containsSellerProduct(order, seller))
                    .toList();
        }

        if (requestedBy.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return orders.stream()
                    .filter(Order::containsPhysicalProducts)
                    .toList();
        }

        throw new IllegalStateException(
                "User is not authorized to query orders."
        );
    }

    /**
     * Comprueba si el pedido pertenece al comprador.
     *
     * Se compara la identidad del comprador porque el modelo
     * todavía no implementa igualdad mediante identificación.
     */
    private boolean belongsToBuyer(
            Order order,
            Buyer buyer
    ) {
        return order.getBuyer() == buyer;
    }

    /**
     * Comprueba si el pedido contiene por lo menos un producto
     * administrado por el vendedor.
     */
    private boolean containsSellerProduct(
            Order order,
            Seller seller
    ) {
        for (OrderItem item : order.getItems()) {
            if (seller.managesProduct(item.getProduct())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Valida que la consulta sea realizada por un usuario activo.
     */
    private void validateActiveRequester(User requestedBy) {
        if (requestedBy == null) {
            throw new IllegalArgumentException(
                    "Requesting user must not be null."
            );
        }

        if (!requestedBy.isActive()) {
            throw new IllegalStateException(
                    "Only active users can query orders."
            );
        }
    }

    /**
     * El administrador y el supervisor tienen acceso global
     * de lectura sobre los pedidos.
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole() == SystemRole.ADMINISTRATOR
                || user.getRole() == SystemRole.SUPERVISOR;
    }
}