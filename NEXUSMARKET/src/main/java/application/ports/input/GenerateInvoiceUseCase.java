package application.ports.input;

import application.domain.models.Invoice;
import application.domain.models.Order;

/**
 * Puerto de entrada que define el caso de uso
 * para generar facturas en NexusMarket.
 *
 * La factura se genera como consecuencia de la
 * confirmación del pago y no representa otro cobro.
 */
public interface GenerateInvoiceUseCase {

    /**
     * Genera la factura correspondiente a un pedido.
     *
     * @param order pedido que será facturado
     * @return factura generada
     */
    Invoice generate(Order order);

    /**
     * Genera una factura utilizando el identificador
     * de un pedido persistido.
     *
     * @param orderId identificador del pedido
     * @return factura generada
     */
    Invoice generateByOrderId(String orderId);
}