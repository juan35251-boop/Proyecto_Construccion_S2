package application.adapters.input.rest.dto;

import application.domain.models.Cart;

import java.util.List;

/**
 * Respuesta utilizada para representar
 * un carrito de compras.
 *
 * @param cartId identificador del carrito
 * @param buyerIdentification identificación del comprador
 * @param items productos agregados
 * @param totalItems cantidad total de unidades
 * @param empty indica si el carrito está vacío
 */
public record CartResponse(
        String cartId,
        String buyerIdentification,
        List<CartItemResponse> items,
        int totalItems,
        boolean empty
) {

    /**
     * Convierte un carrito del dominio
     * en una respuesta para la API.
     *
     * @param cart carrito convertido
     * @return respuesta construida
     */
    public static CartResponse from(
            Cart cart
    ) {
        if (cart == null) {
            throw new IllegalArgumentException(
                    "Cart must not be null."
            );
        }

        if (cart.getBuyer() == null) {
            throw new IllegalStateException(
                    "Cart buyer must not be null."
            );
        }

        List<CartItemResponse> itemResponses =
                cart.getItems()
                        .stream()
                        .map(CartItemResponse::from)
                        .toList();

        int totalItems =
                cart.getItems()
                        .stream()
                        .mapToInt(item ->
                                item.getQuantity()
                        )
                        .sum();

        return new CartResponse(
                cart.getCartId(),
                cart.getBuyer()
                        .getIdentification(),
                itemResponses,
                totalItems,
                cart.isEmpty()
        );
    }
}