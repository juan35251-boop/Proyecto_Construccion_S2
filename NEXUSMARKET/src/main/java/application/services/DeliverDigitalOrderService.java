package application.services;

import application.domain.models.Order;
import application.ports.output.OrderRepository;

/**
 * Servicio de aplicación encargado de confirmar
 * la entrega de pedidos exclusivamente digitales.
 *
 * Los pedidos digitales pasan directamente de pagados
 * a entregados porque no requieren despacho físico.
 */
public class DeliverDigitalOrderService {

    private final OrderRepository orderRepository;

    /**
     * Construye el servicio con el repositorio de pedidos.
     *
     * @param orderRepository repositorio utilizado para actualizar pedidos
     */
    public DeliverDigitalOrderService(
            OrderRepository orderRepository
    ) {
        validateRepository(orderRepository);
        this.orderRepository = orderRepository;
    }

    /**
     * Confirma la entrega automática de un pedido digital.
     *
     * @param order pedido digital que será entregado
     * @return pedido actualizado
     */
    public Order deliver(Order order) {
        validateOrder(order);
        validateDigitalOnlyOrder(order);

        /*
         * Order valida que el pedido digital haya sido pagado
         * antes de cambiarlo al estado DELIVERED.
         */
        order.markAsDelivered();

        orderRepository.save(order);

        return order;
    }

    /**
     * Valida que el repositorio exista.
     */
    private void validateRepository(
            OrderRepository orderRepository
    ) {
        if (orderRepository == null) {
            throw new IllegalArgumentException(
                    "Order repository must not be null."
            );
        }
    }

    /**
     * Valida que exista un pedido.
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order must not be null."
            );
        }
    }

    /**
     * Comprueba que el pedido no contenga productos físicos.
     *
     * Un pedido mixto también requiere despacho porque contiene
     * al menos un producto físico.
     */
    private void validateDigitalOnlyOrder(Order order) {
        if (order.containsPhysicalProducts()) {
            throw new IllegalStateException(
                    "Orders with physical products require shipment."
            );
        }
    }
}