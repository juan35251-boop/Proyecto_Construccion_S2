package application.domain.models;

/**
 * Representa un elemento individual dentro de un carrito de compras.
 *
 * Un elemento del carrito relaciona un {@link Product} con la cantidad
 * que el comprador desea adquirir. El producto no puede reemplazarse
 * después de crear el elemento, pero su cantidad sí puede modificarse.
 */
public class CartItem {

    /**
     * Producto asociado al elemento del carrito.
     *
     * Su referencia es inmutable después de crear el elemento.
     */
    private final Product product;

    /**
     * Cantidad solicitada del producto.
     *
     * Siempre debe ser mayor que cero.
     */
    private int quantity;

    /**
     * Crea un elemento del carrito con un producto y una cantidad.
     *
     * El producto y la cantidad se validan antes de almacenarse.
     *
     * @param product producto asociado al elemento
     * @param quantity cantidad inicial solicitada
     *
     * @throws IllegalArgumentException si el producto es nulo
     * @throws IllegalArgumentException si la cantidad es menor
     *                                  o igual a cero
     */
    public CartItem(Product product, int quantity) {
        validateProduct(product);
        validateQuantity(quantity);

        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Obtiene el producto asociado al elemento del carrito.
     *
     * @return el producto registrado
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Obtiene la cantidad solicitada del producto.
     *
     * @return la cantidad actual
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Reemplaza la cantidad actual por una nueva cantidad.
     *
     * @param newQuantity nueva cantidad solicitada
     *
     * @throws IllegalArgumentException si la nueva cantidad es menor
     *                                  o igual a cero
     */
    public void changeQuantity(int newQuantity) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
    }

    /**
     * Aumenta la cantidad actual del producto.
     *
     * La cantidad recibida se suma a la cantidad que ya existe.
     *
     * @param quantityToAdd cantidad que se desea agregar
     *
     * @throws IllegalArgumentException si la cantidad que se desea agregar
     *                                  es menor o igual a cero
     */
    public void increaseQuantity(int quantityToAdd) {
        validateQuantity(quantityToAdd);
        this.quantity += quantityToAdd;
    }

    /**
     * Determina si este elemento pertenece al producto recibido.
     *
     * La comparación utiliza la identidad de los objetos, por lo que
     * comprueba que ambas referencias correspondan a la misma instancia
     * de {@link Product}.
     *
     * @param product producto que se desea comparar
     * @return {@code true} si corresponde a la misma instancia de producto;
     *         de lo contrario, {@code false}
     */
    public boolean belongsTo(Product product) {
        return this.product == product;
    }

    /**
     * Valida que el elemento tenga un producto asociado.
     *
     * @param product producto que se desea validar
     *
     * @throws IllegalArgumentException si el producto es nulo
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Cart item must contain a product."
            );
        }
    }

    /**
     * Valida que una cantidad sea mayor que cero.
     *
     * @param quantity cantidad que se desea validar
     *
     * @throws IllegalArgumentException si la cantidad es menor
     *                                  o igual a cero
     */
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Cart item quantity must be greater than zero."
            );
        }
    }
}