package application.services;

import application.domain.models.Order;
import application.ports.input.CompleteOrderUseCase;
import application.ports.output.OrderRepository;

/**
 * Servicio de aplicación encargado de finalizar pedidos.
 *
 * Implementa el puerto de entrada CompleteOrderUseCase,
 * permitiendo solicitar la finalización de una orden sin
 * depender directamente de esta implementación.
 *
 * La finalización representa el cierre satisfactorio
 * del pedido después de confirmar su entrega.
 */
public class CompleteOrderService
        implements CompleteOrderUseCase {

    private final OrderRepository orderRepository;

    /**
     * Construye el servicio con el repositorio de pedidos.
     *
     * @param orderRepository repositorio utilizado
     *                        para guardar pedidos
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
     * El modelo Order valida que solamente un pedido
     * con estado DELIVERED pueda cambiar al estado FINALIZED.
     *
     * @param order pedido que se desea finalizar
     * @return pedido finalizado
     */
    @Override
    public Order complete(Order order) {
        validateOrder(order);

        /*
         * Order protege su propio ciclo de vida
         * y valida la transición:
         *
         * DELIVERED -> FINALIZED
         */
        order.finalizeOrder();

        /*
         * Se almacena el nuevo estado del pedido
         * mediante el puerto de salida.
         */
        orderRepository.save(order);

        return order;
    }

    /**
     * Valida que el repositorio exista.
     *
     * @param orderRepository repositorio de pedidos
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
     *
     * @param order pedido que será validado
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order must not be null."
            );
        }
    }
}