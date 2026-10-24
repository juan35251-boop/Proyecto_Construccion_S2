package application.adapters.input.rest.dto;

import application.domain.models.Invoice;
import application.domain.valueobjects.OrderStatus;

import java.util.List;

/**
 * Respuesta utilizada para representar una factura.
 *
 * @param invoiceId identificador de la factura
 * @param orderId identificador del pedido
 * @param buyerIdentification identificación del comprador
 * @param orderStatus estado actual del pedido
 * @param items productos facturados
 * @param totalItems cantidad total de unidades
 */
public record InvoiceResponse(
        String invoiceId,
        String orderId,
        String buyerIdentification,
        OrderStatus orderStatus,
        List<InvoiceItemResponse> items,
        int totalItems
) {

    /**
     * Convierte una factura del dominio
     * en una respuesta REST.
     *
     * @param invoice factura convertida
     * @return respuesta construida
     */
    public static InvoiceResponse from(
            Invoice invoice
    ) {
        if (invoice == null) {
            throw new IllegalArgumentException(
                    "Invoice must not be null."
            );
        }

        if (invoice.getOrder() == null) {
            throw new IllegalStateException(
                    "Invoice order must not be null."
            );
        }

        if (invoice.getBuyer() == null) {
            throw new IllegalStateException(
                    "Invoice buyer must not be null."
            );
        }

        List<InvoiceItemResponse> items =
                invoice.getItems()
                        .stream()
                        .map(InvoiceItemResponse::from)
                        .toList();

        int totalItems =
                invoice.getItems()
                        .stream()
                        .mapToInt(item ->
                                item.getQuantity()
                        )
                        .sum();

        return new InvoiceResponse(
                invoice.getInvoiceId(),
                invoice.getOrder().getOrderId(),
                invoice.getBuyer()
                        .getIdentification(),
                invoice.getOrder().getStatus(),
                items,
                totalItems
        );
    }
}