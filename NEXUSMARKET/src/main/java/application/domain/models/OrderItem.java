package application.domain.models;

/**
 * Representa un elemento individual dentro de una orden de compra.
 *
 * Cada elemento relaciona un {@link Product} con la cantidad confirmada
 * por el comprador al crear la orden.
 *
 * A diferencia de {@link CartItem}, el producto y la cantidad no pueden
 * modificarse después de crear el elemento.
 */
public class OrderItem {

    /**
     * Producto incluido en la orden.
     *
     * No puede reemplazarse después de crear el elemento.
     */
    private final Product product;

    /**
     * Cantidad confirmada del producto.
     *
     * Debe ser mayor que cero y no puede modificarse después de crear
     * el elemento.
     */
    private final int quantity;

    /**
     * Crea un elemento de orden con un producto y una cantidad.
     *
     * El producto y la cantidad se validan antes de ser almacenados.
     *
     * @param product producto incluido en la orden
     * @param quantity cantidad confirmada del producto
     *
     * @throws IllegalArgumentException si el producto es nulo
     * @throws IllegalArgumentException si la cantidad es menor
     *                                  o igual a cero
     */
    public OrderItem(Product product, int quantity) {
        validateProduct(product);
        validateQuantity(quantity);

        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Obtiene el producto incluido en este elemento.
     *
     * @return el producto asociado
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Obtiene la cantidad confirmada del producto.
     *
     * @return la cantidad incluida en la orden
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Determina si este elemento pertenece al producto recibido.
     *
     * La comparación se realiza por identidad, por lo que ambas referencias
     * deben apuntar a la misma instancia de {@link Product}.
     *
     * @param product producto que se desea comparar
     * @return {@code true} si corresponde a la misma instancia;
     *         de lo contrario, {@code false}
     */
    public boolean belongsTo(Product product) {
        return this.product == product;
    }

    /**
     * Valida que el elemento esté relacionado con un producto.
     *
     * @param product producto que se desea validar
     *
     * @throws IllegalArgumentException si el producto es nulo
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Order item must contain a product."
            );
        }
    }

    /**
     * Valida que la cantidad confirmada sea mayor que cero.
     *
     * @param quantity cantidad que se desea validar
     *
     * @throws IllegalArgumentException si la cantidad es menor
     *                                  o igual a cero
     */
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Order item quantity must be greater than zero."
            );
        }
    }
}