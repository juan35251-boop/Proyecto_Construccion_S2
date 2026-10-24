package application.adapters.input.rest.dto;

import application.domain.models.OrderItem;
import application.domain.valueobjects.ProductType;

/**
 * Representa un producto incluido en una factura.
 *
 * @param productId identificador del producto
 * @param productType tipo del producto
 * @param quantity cantidad comprada
 */
public record InvoiceItemResponse(
        String productId,
        ProductType productType,
        int quantity
) {

    /**
     * Convierte un elemento del pedido
     * en una respuesta de factura.
     *
     * @param item elemento facturado
     * @return respuesta construida
     */
    public static InvoiceItemResponse from(
            OrderItem item
    ) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "Invoice item must not be null."
            );
        }

        if (item.getProduct() == null) {
            throw new IllegalStateException(
                    "Invoice item product must not be null."
            );
        }

        return new InvoiceItemResponse(
                item.getProduct().getProductId(),
                item.getProduct().getProductType(),
                item.getQuantity()
        );
    }
}