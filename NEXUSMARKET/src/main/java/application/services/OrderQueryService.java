package application.services;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.OrderQueryUseCase;
import application.ports.output.OrderQueryRepository;
import application.ports.input.OrderQueryUseCase;
import java.util.List;

/**
 * Servicio de aplicación encargado de consultar pedidos.
 *
 * Implementa el puerto de entrada OrderQueryUseCase,
 * permitiendo consultar pedidos sin depender directamente
 * de esta implementación.
 *
 * Permisos:
 *
 * - El comprador consulta únicamente sus pedidos.
 * - El vendedor consulta pedidos con sus productos.
 * - El operador logístico consulta pedidos físicos.
 * - El administrador consulta todos los pedidos.
 * - El supervisor consulta todos los pedidos.
 */
public class OrderQueryService
        implements OrderQueryUseCase {

    private final OrderQueryRepository
            orderQueryRepository;

    /**
     * Construye el servicio con el repositorio
     * de consulta.
     *
     * @param orderQueryRepository repositorio
     *                             de consulta de pedidos
     */
    public OrderQueryService(
            OrderQueryRepository orderQueryRepository
    ) {
        if (orderQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Order query repository must not be null."
            );
        }

        this.orderQueryRepository =
                orderQueryRepository;
    }

    /**
     * Obtiene los pedidos visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return pedidos autorizados
     */
    @Override
    public List<Order> findAccessibleOrders(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Order> orders =
                orderQueryRepository.findAll();

        /*
         * Administradores y supervisores tienen
         * acceso global de lectura.
         */
        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(orders);
        }

        /*
         * El comprador solamente puede consultar
         * los pedidos que le pertenecen.
         */
        if (requestedBy instanceof Buyer buyer) {
            return orders.stream()
                    .filter(
                            order ->
                                    belongsToBuyer(
                                            order,
                                            buyer
                                    )
                    )
                    .toList();
        }

        /*
         * El vendedor consulta pedidos que contengan
         * por lo menos uno de sus productos.
         */
        if (requestedBy instanceof Seller seller) {
            return orders.stream()
                    .filter(
                            order ->
                                    containsSellerProduct(
                                            order,
                                            seller
                                    )
                    )
                    .toList();
        }

        /*
         * El operador logístico solamente consulta
         * pedidos que contengan productos físicos.
         */
        if (requestedBy.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return orders.stream()
                    .filter(
                            Order::containsPhysicalProducts
                    )
                    .toList();
        }

        throw new IllegalStateException(
                "User is not authorized to query orders."
        );
    }

    /**
     * Comprueba si el pedido pertenece al comprador.
     *
     * Se compara la identidad del comprador porque
     * el modelo todavía no implementa igualdad
     * mediante identificación.
     *
     * @param order pedido consultado
     * @param buyer comprador solicitante
     * @return true si el pedido pertenece al comprador
     */
    private boolean belongsToBuyer(
            Order order,
            Buyer buyer
    ) {
        return order.getBuyer() == buyer;
    }

    /**
     * Comprueba si el pedido contiene al menos
     * un producto administrado por el vendedor.
     *
     * @param order pedido consultado
     * @param seller vendedor solicitante
     * @return true si contiene un producto del vendedor
     */
    private boolean containsSellerProduct(
            Order order,
            Seller seller
    ) {
        for (OrderItem item : order.getItems()) {
            if (seller.managesProduct(
                    item.getProduct()
            )) {
                return true;
            }
        }

        return false;
    }

    /**
     * Valida que la consulta sea realizada
     * por un usuario activo.
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
                    "Only active users can query orders."
            );
        }
    }

    /**
     * Indica si el usuario tiene acceso global
     * de lectura sobre los pedidos.
     *
     * @param user usuario solicitante
     * @return true si es administrador o supervisor
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }
}