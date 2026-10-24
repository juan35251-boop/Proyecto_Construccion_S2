package application.domain.models;

import java.util.Objects;

/**
 * Representa un elemento individual dentro
 * de un carrito de compras.
 *
 * Un elemento relaciona un producto con la cantidad
 * que el comprador desea adquirir.
 */
public class CartItem {

    /**
     * Producto asociado al elemento.
     */
    private final Product product;

    /**
     * Cantidad solicitada.
     *
     * Siempre debe ser mayor que cero.
     */
    private int quantity;

    /**
     * Crea un elemento del carrito.
     *
     * @param product producto asociado
     * @param quantity cantidad solicitada
     */
    public CartItem(
            Product product,
            int quantity
    ) {
        validateProduct(product);
        validateQuantity(quantity);

        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Obtiene el producto asociado.
     *
     * @return producto almacenado
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Obtiene la cantidad solicitada.
     *
     * @return cantidad actual
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Reemplaza la cantidad actual.
     *
     * @param newQuantity nueva cantidad
     */
    public void changeQuantity(
            int newQuantity
    ) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
    }

    /**
     * Aumenta la cantidad existente.
     *
     * @param quantityToAdd cantidad agregada
     */
    public void increaseQuantity(
            int quantityToAdd
    ) {
        validateQuantity(quantityToAdd);
        this.quantity += quantityToAdd;
    }

    /**
     * Determina si el elemento pertenece al producto recibido.
     *
     * La comparación se realiza mediante productId y no mediante
     * la referencia del objeto en memoria. De esta manera funciona
     * correctamente después de recuperar los datos desde MySQL.
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
     * Valida el producto asociado.
     */
    private void validateProduct(
            Product product
    ) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Cart item must contain a product."
            );
        }
    }

    /**
     * Valida que una cantidad sea mayor que cero.
     */
    private void validateQuantity(
            int quantity
    ) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Cart item quantity must be greater than zero."
            );
        }
    }

    /**
     * Dos elementos se consideran iguales cuando
     * corresponden al mismo producto.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof CartItem cartItem)) {
            return false;
        }

        return product.getProductId()
                .equals(
                        cartItem.product.getProductId()
                );
    }

    /**
     * Genera el código hash con el identificador del producto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                product.getProductId()
        );
    }
}