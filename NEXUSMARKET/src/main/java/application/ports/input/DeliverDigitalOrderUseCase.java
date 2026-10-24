package application.ports.input;

import application.domain.models.Order;

/**
 * Puerto de entrada para entregar pedidos
 * compuestos exclusivamente por productos digitales.
 */
public interface DeliverDigitalOrderUseCase {

    /**
     * Entrega un pedido digital del dominio.
     *
     * @param order pedido que será entregado
     * @return pedido actualizado
     */
    Order deliver(Order order);

    /**
     * Localiza y entrega un pedido digital
     * mediante su identificador.
     *
     * @param orderId identificador del pedido
     * @return pedido actualizado
     */
    Order deliverByOrderId(String orderId);
}