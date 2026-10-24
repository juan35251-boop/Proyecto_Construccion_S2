package application.services;

import application.domain.models.Order;
import application.ports.input.CompleteOrderUseCase;
import application.ports.output.OrderRepository;

/**
 * Servicio de aplicación encargado de finalizar pedidos.
 *
 * La finalización representa el cierre satisfactorio
 * del pedido después de confirmar su entrega.
 *
 * Un pedido finalizado no puede volver a modificarse.
 */
public class CompleteOrderService
        implements CompleteOrderUseCase {

    private final OrderRepository orderRepository;

    /**
     * Construye el servicio.
     *
     * @param orderRepository repositorio de pedidos
     */
    public CompleteOrderService(
            OrderRepository orderRepository
    ) {
        validateRepository(orderRepository);
        this.orderRepository = orderRepository;
    }

    /**
     * Finaliza un pedido entregado.
     *
     * Order valida internamente que únicamente un pedido
     * en estado DELIVERED pueda cambiar a FINALIZED.
     *
     * @param order pedido que se desea finalizar
     * @return pedido finalizado
     */
    @Override
    public Order complete(Order order) {
        validateOrder(order);

        order.finalizeOrder();

        orderRepository.save(order);

        return order;
    }

    /**
     * Localiza y finaliza un pedido
     * mediante su identificador.
     *
     * @param orderId identificador del pedido
     * @return pedido finalizado
     */
    @Override
    public Order completeByOrderId(String orderId) {
        validateText(
                orderId,
                "Order identifier"
        );

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Order was not found."
                        )
                );

        return complete(order);
    }

    /**
     * Valida el repositorio.
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
     * Valida que exista el pedido.
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order must not be null."
            );
        }
    }

    /**
     * Valida un identificador obligatorio.
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
}