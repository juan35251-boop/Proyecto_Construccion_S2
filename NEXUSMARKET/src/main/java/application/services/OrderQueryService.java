package application.services;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.OrderQueryUseCase;
import application.ports.output.OrderQueryRepository;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio de aplicación encargado de consultar pedidos.
 *
 * Permisos aplicados:
 *
 * - Comprador: consulta únicamente sus pedidos.
 * - Vendedor: consulta pedidos que contienen sus productos.
 * - Operador logístico: consulta pedidos con productos físicos.
 * - Administrador: consulta todos los pedidos.
 * - Supervisor: consulta todos los pedidos.
 */
public class OrderQueryService
        implements OrderQueryUseCase {

    private final UserRepository userRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * Construye el servicio para consultas que ya
     * proporcionan el usuario del dominio.
     *
     * Este constructor se conserva para mantener
     * las pruebas unitarias existentes.
     *
     * @param orderQueryRepository repositorio de consultas
     */
    public OrderQueryService(
            OrderQueryRepository orderQueryRepository
    ) {
        validateOrderQueryRepository(
                orderQueryRepository
        );

        this.userRepository = null;
        this.orderQueryRepository =
                orderQueryRepository;
    }

    /**
     * Construye el servicio completo utilizado por la API.
     *
     * @param userRepository repositorio de usuarios
     * @param orderQueryRepository repositorio de pedidos
     */
    public OrderQueryService(
            UserRepository userRepository,
            OrderQueryRepository orderQueryRepository
    ) {
        validateUserRepository(userRepository);

        validateOrderQueryRepository(
                orderQueryRepository
        );

        this.userRepository = userRepository;
        this.orderQueryRepository =
                orderQueryRepository;
    }

    /**
     * Obtiene los pedidos visibles para el usuario.
     *
     * @param requestedBy usuario solicitante
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
                            order -> belongsToBuyer(
                                    order,
                                    buyer
                            )
                    )
                    .toList();
        }

        /*
         * El vendedor consulta pedidos que contienen
         * por lo menos uno de sus productos.
         */
        if (requestedBy instanceof Seller seller) {
            return orders.stream()
                    .filter(
                            order -> containsSellerProduct(
                                    order,
                                    seller
                            )
                    )
                    .toList();
        }

        /*
         * El operador logístico solamente consulta
         * pedidos que necesitan despacho físico.
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
     * Consulta pedidos utilizando la identificación
     * del usuario solicitante.
     *
     * @param requesterIdentification identificación del usuario
     * @return pedidos autorizados
     */
    @Override
    public List<Order>
    findAccessibleOrdersByRequesterIdentification(
            String requesterIdentification
    ) {
        User requester =
                findRequester(requesterIdentification);

        return findAccessibleOrders(requester);
    }

    /**
     * Consulta un pedido específico y comprueba
     * que sea visible para el usuario solicitante.
     *
     * @param requesterIdentification identificación del solicitante
     * @param orderId identificador del pedido
     * @return pedido autorizado
     */
    @Override
    public Order findAccessibleOrderByIdentifiers(
            String requesterIdentification,
            String orderId
    ) {
        User requester =
                findRequester(requesterIdentification);

        validateText(
                orderId,
                "Order identifier"
        );

        Order order = orderQueryRepository
                .findById(orderId)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Order was not found."
                        )
                );

        validateOrderAccess(
                requester,
                order
        );

        return order;
    }

    /**
     * Localiza al usuario que realiza la consulta.
     *
     * @param requesterIdentification identificación del usuario
     * @return usuario encontrado
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
     * un pedido específico.
     *
     * @param requester usuario solicitante
     * @param order pedido consultado
     */
    private void validateOrderAccess(
            User requester,
            Order order
    ) {
        validateActiveRequester(requester);

        if (hasGlobalAccess(requester)) {
            return;
        }

        if (requester instanceof Buyer buyer
                && belongsToBuyer(order, buyer)) {
            return;
        }

        if (requester instanceof Seller seller
                && containsSellerProduct(order, seller)) {
            return;
        }

        if (requester.getRole()
                == SystemRole.LOGISTICS_OPERATOR
                && order.containsPhysicalProducts()) {
            return;
        }

        throw new IllegalStateException(
                "User is not authorized to query this order."
        );
    }

    /**
     * Comprueba si el pedido pertenece al comprador.
     *
     * La comparación se realiza mediante identificación,
     * porque las entidades recuperadas desde persistencia
     * pueden ser instancias diferentes.
     *
     * @param order pedido consultado
     * @param buyer comprador solicitante
     * @return true si el pedido pertenece al comprador
     */
    private boolean belongsToBuyer(
            Order order,
            Buyer buyer
    ) {
        return order.getBuyer() != null
                && buyer.getIdentification().equals(
                        order.getBuyer().getIdentification()
                );
    }

    /**
     * Comprueba si el pedido contiene algún producto
     * administrado por el vendedor.
     *
     * @param order pedido consultado
     * @param seller vendedor solicitante
     * @return true si contiene productos del vendedor
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
     * Valida que el usuario exista y esté activo.
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
     * Indica si el usuario tiene acceso global.
     *
     * @param user usuario solicitante
     * @return true para administrador o supervisor
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
     * esté disponible para consultas por identificación.
     */
    private void validateUserRepositoryAvailable() {
        if (userRepository == null) {
            throw new IllegalStateException(
                    "User repository is required for identifier-based operations."
            );
        }
    }

    /**
     * Valida el repositorio de consultas de pedidos.
     */
    private void validateOrderQueryRepository(
            OrderQueryRepository orderQueryRepository
    ) {
        if (orderQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Order query repository must not be null."
            );
        }
    }
}