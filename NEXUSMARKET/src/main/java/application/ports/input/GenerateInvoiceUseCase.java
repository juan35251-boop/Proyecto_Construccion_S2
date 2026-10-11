package application.ports.input;

import application.domain.models.Invoice;
import application.domain.models.Order;

/**
 * Puerto de entrada que define el caso de uso
 * para generar facturas en NexusMarket.
 *
 * La factura se genera como consecuencia de la
 * confirmación del pago y no representa un nuevo cobro.
 */
public interface GenerateInvoiceUseCase {

    /**
     * Genera la factura correspondiente a un pedido.
     *
     * El pedido debe existir y no puede continuar
     * en estado PENDING_PAYMENT.
     *
     * @param order pedido para el cual se genera la factura
     * @return factura generada
     *
     * @throws IllegalArgumentException si el pedido es nulo
     * @throws IllegalStateException si el pedido continúa
     *         pendiente de pago
     */
    Invoice generate(Order order);
}