package application.adapters.input.rest.dto;

import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.valueobjects.ProductType;

/**
 * Representación de una línea perteneciente
 * a un pedido.
 *
 * @param productId identificador del producto
 * @param productType tipo del producto
 * @param quantity cantidad solicitada
 */
public record OrderItemResponse(
        String productId,
        ProductType productType,
        int quantity
) {

    /**
     * Convierte una línea del dominio
     * en una respuesta de la API.
     *
     * @param item línea del pedido
     * @return representación de la línea
     */
    public static OrderItemResponse from(
            OrderItem item
    ) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "Order item must not be null."
            );
        }

        Product product = item.getProduct();

        if (product == null) {
            throw new IllegalStateException(
                    "Order item must contain a product."
            );
        }

        return new OrderItemResponse(
                product.getProductId(),
                product.getProductType(),
                item.getQuantity()
        );
    }
}