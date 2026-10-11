package application.ports.input;

import application.domain.models.Order;

/**
 * Puerto de entrada que define el caso de uso
 * para entregar pedidos exclusivamente digitales.
 *
 * Los pedidos digitales no necesitan bodega,
 * dirección física ni proceso de despacho.
 */
public interface DeliverDigitalOrderUseCase {

    /**
     * Confirma la entrega de un pedido
     * exclusivamente digital.
     *
     * El pedido debe estar pagado y no puede contener
     * ningún producto físico.
     *
     * La operación cambia el estado del pedido:
     *
     * PAID -> DELIVERED
     *
     * @param order pedido digital que se desea entregar
     * @return pedido actualizado en estado DELIVERED
     *
     * @throws IllegalArgumentException si el pedido es nulo
     * @throws IllegalStateException si el pedido contiene
     *         productos físicos, todavía no está pagado
     *         o ya fue entregado
     */
    Order deliver(Order order);
}
