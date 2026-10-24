package application.ports.input;

import application.domain.models.Order;

/**
 * Puerto de entrada para finalizar pedidos
 * cuya entrega ya fue confirmada.
 */
public interface CompleteOrderUseCase {

    /**
     * Finaliza un pedido del dominio.
     *
     * @param order pedido entregado
     * @return pedido finalizado
     */
    Order complete(Order order);

    /**
     * Localiza y finaliza un pedido
     * mediante su identificador.
     *
     * @param orderId identificador del pedido
     * @return pedido finalizado
     */
    Order completeByOrderId(String orderId);
}