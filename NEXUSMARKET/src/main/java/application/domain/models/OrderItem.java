package application.domain.models;

import java.util.Objects;

/**
 * Representa un producto confirmado
 * dentro de un pedido.
 *
 * El producto y su cantidad son inmutables
 * después de crear el elemento.
 */
public class OrderItem {

    /**
     * Producto confirmado.
     */
    private final Product product;

    /**
     * Cantidad confirmada.
     */
    private final int quantity;

    /**
     * Crea un elemento del pedido.
     *
     * @param product producto confirmado
     * @param quantity cantidad confirmada
     */
    public OrderItem(
            Product product,
            int quantity
    ) {
        validateProduct(product);
        validateQuantity(quantity);

        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Obtiene el producto.
     *
     * @return producto confirmado
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Obtiene la cantidad.
     *
     * @return cantidad confirmada
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Determina si representa el producto recibido.
     *
     * La comparación utiliza productId para funcionar
     * correctamente con objetos recuperados desde MySQL.
     *
     * @param comparedProduct producto comparado
     * @return true si representa el mismo producto
     */
    public boolean belongsTo(
            Product comparedProduct
    ) {
        if (comparedProduct == null) {
            return false;
        }

        return product.getProductId()
                .equals(
                        comparedProduct.getProductId()
                );
    }

    /**
     * Valida el producto.
     */
    private void validateProduct(
            Product product
    ) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Order item must contain a product."
            );
        }
    }

    /**
     * Valida la cantidad.
     */
    private void validateQuantity(
            int quantity
    ) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Order item quantity must be greater than zero."
            );
        }
    }

    /**
     * Dos elementos son iguales cuando representan
     * el mismo producto.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof OrderItem orderItem)) {
            return false;
        }

        return product.getProductId()
                .equals(
                        orderItem.product.getProductId()
                );
    }

    /**
     * Genera el código hash mediante productId.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                product.getProductId()
        );
    }
}