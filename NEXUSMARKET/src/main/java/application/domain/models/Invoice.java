package application.domain.models;

import application.domain.valueobjects.OrderStatus;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa la factura asociada a un pedido de NexusMarket.
 *
 * Cada factura posee un identificador permanente y pertenece
 * a un único pedido.
 *
 * Una factura solamente puede generarse cuando el pedido ya
 * superó el estado PENDING_PAYMENT.
 */
public class Invoice {

    /**
     * Identificador permanente de la factura.
     */
    private final String invoiceId;

    /**
     * Pedido al que pertenece la factura.
     *
     * El pedido asociado no puede reemplazarse
     * después de crear la factura.
     */
    private final Order order;

    /**
     * Crea una factura nueva y genera automáticamente
     * su identificador.
     *
     * @param order pedido asociado a la factura
     */
    public Invoice(
            Order order
    ) {
        this(
                UUID.randomUUID().toString(),
                order
        );
    }

    /**
     * Reconstruye una factura utilizando
     * un identificador existente.
     *
     * Este constructor será utilizado por los
     * adaptadores de persistencia.
     *
     * @param invoiceId identificador permanente
     * @param order pedido asociado
     */
    public Invoice(
            String invoiceId,
            Order order
    ) {
        validateInvoiceId(invoiceId);
        validateOrder(order);
        validatePaidOrder(order);

        this.invoiceId = invoiceId;
        this.order = order;
    }

    /**
     * Obtiene el identificador de la factura.
     *
     * @return identificador permanente
     */
    public String getInvoiceId() {
        return invoiceId;
    }

    /**
     * Obtiene el pedido asociado a la factura.
     *
     * @return pedido facturado
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Obtiene el comprador propietario del pedido.
     *
     * @return comprador asociado
     */
    public Buyer getBuyer() {
        return order.getBuyer();
    }

    /**
     * Obtiene los elementos incluidos
     * en el pedido facturado.
     *
     * @return elementos del pedido
     */
    public List<OrderItem> getItems() {
        return order.getItems();
    }

    /**
     * Determina si la factura pertenece al pedido indicado.
     *
     * La comparación se realiza mediante orderId y no mediante
     * referencias de memoria, porque el pedido puede ser
     * reconstruido desde MySQL.
     *
     * @param comparedOrder pedido que se desea comparar
     * @return true si corresponde al mismo pedido
     */
    public boolean belongsTo(
            Order comparedOrder
    ) {
        return comparedOrder != null
                && order.getOrderId().equals(
                        comparedOrder.getOrderId()
                );
    }

    /**
     * Valida el identificador de la factura.
     *
     * @param invoiceId identificador validado
     */
    private void validateInvoiceId(
            String invoiceId
    ) {
        if (invoiceId == null
                || invoiceId.isBlank()) {
            throw new IllegalArgumentException(
                    "Invoice identifier must not be empty."
            );
        }
    }

    /**
     * Valida que la factura esté relacionada
     * con un pedido.
     *
     * @param order pedido validado
     */
    private void validateOrder(
            Order order
    ) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Invoice must be associated with an order."
            );
        }
    }

    /**
     * Valida que el pedido haya superado
     * el estado pendiente de pago.
     *
     * @param order pedido cuyo estado será validado
     */
    private void validatePaidOrder(
            Order order
    ) {
        if (order.getStatus()
                == OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "An invoice cannot be generated before payment."
            );
        }
    }

    /**
     * Compara facturas mediante su identificador permanente.
     *
     * @param object objeto comparado
     * @return true si representa la misma factura
     */
    @Override
    public boolean equals(
            Object object
    ) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Invoice invoice)) {
            return false;
        }

        return invoiceId.equals(
                invoice.invoiceId
        );
    }

    /**
     * Genera el código hash usando invoiceId.
     *
     * @return código hash de la factura
     */
    @Override
    public int hashCode() {
        return Objects.hash(invoiceId);
    }
}