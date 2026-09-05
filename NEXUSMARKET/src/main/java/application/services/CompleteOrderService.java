package application.services;

import application.domain.models.Order;
import application.ports.output.OrderRepository;

/**
 * Servicio de aplicación encargado de finalizar pedidos.
 *
 * La finalización representa el cierre satisfactorio del pedido
 * después de que su entrega haya sido confirmada.
 */
public class CompleteOrderService {

    private final OrderRepository orderRepository;

    /**
     * Construye el servicio con el repositorio de pedidos.
     *
     * @param orderRepository repositorio utilizado para guardar pedidos
     */
    public CompleteOrderService(
            OrderRepository orderRepository
    ) {
        validateRepository(orderRepository);
        this.orderRepository = orderRepository;
    }

    /**
     * Finaliza un pedido que ya fue entregado.
     *
     * El modelo Order valida que solamente un pedido con estado
     * DELIVERED pueda cambiar al estado FINALIZED.
     *
     * @param order pedido que se desea finalizar
     * @return pedido finalizado
     */
    public Order complete(Order order) {
        validateOrder(order);

        order.finalizeOrder();
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
     * Valida que exista un pedido para finalizar.
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order must not be null."
            );
        }
    }
}