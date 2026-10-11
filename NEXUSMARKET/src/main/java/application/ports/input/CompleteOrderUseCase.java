package application.ports.input;

import application.domain.models.Order;

/**
 * Puerto de entrada que define el caso de uso
 * para finalizar pedidos en NexusMarket.
 *
 * La finalización representa el cierre satisfactorio
 * de un pedido después de confirmar su entrega.
 */
public interface CompleteOrderUseCase {

    /**
     * Finaliza un pedido que ya fue entregado.
     *
     * La operación cambia el estado:
     *
     * DELIVERED -> FINALIZED
     *
     * @param order pedido que se desea finalizar
     * @return pedido finalizado
     *
     * @throws IllegalArgumentException si el pedido es nulo
     * @throws IllegalStateException si el pedido todavía
     *         no fue entregado o ya se encuentra finalizado
     */
    Order complete(Order order);
}