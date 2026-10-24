package application.adapters.input.rest.dto;

import application.domain.models.CartItem;
import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;

/**
 * Respuesta que representa un producto
 * agregado al carrito.
 *
 * @param productId identificador del producto
 * @param productType tipo del producto
 * @param productStatus estado del producto
 * @param quantity cantidad solicitada
 */
public record CartItemResponse(
        String productId,
        ProductType productType,
        ProductStatus productStatus,
        int quantity
) {

    /**
     * Convierte un elemento del dominio
     * en una respuesta para la API.
     *
     * @param item elemento convertido
     * @return respuesta construida
     */
    public static CartItemResponse from(
            CartItem item
    ) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "Cart item must not be null."
            );
        }

        Product product =
                item.getProduct();

        if (product == null) {
            throw new IllegalStateException(
                    "Cart item product must not be null."
            );
        }

        return new CartItemResponse(
                product.getProductId(),
                product.getProductType(),
                product.getStatus(),
                item.getQuantity()
        );
    }
}