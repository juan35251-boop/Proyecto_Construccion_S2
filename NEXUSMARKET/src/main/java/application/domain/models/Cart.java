package application.domain.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el carrito de compras de un comprador en NexusMarket.
 *
 * Un carrito pertenece obligatoriamente a un {@link Buyer} y contiene
 * una colección de elementos de tipo {@link CartItem}. Permite agregar
 * productos publicados, modificar sus cantidades y eliminarlos.
 *
 * La clase controla sus productos internamente para evitar modificaciones
 * directas desde otras partes del sistema.
 */
public class Cart {

    /**
     * Comprador propietario del carrito.
     *
     * No puede ser reemplazado después de crear el carrito.
     */
    private final Buyer buyer;

    /**
     * Elementos agregados al carrito.
     *
     * Cada elemento relaciona un producto con la cantidad solicitada.
     */
    private final List<CartItem> items;

    /**
     * Crea un carrito vacío perteneciente a un comprador.
     *
     * @param buyer comprador propietario del carrito
     *
     * @throws IllegalArgumentException si el comprador es nulo
     */
    public Cart(Buyer buyer) {
        validateBuyer(buyer);

        this.buyer = buyer;
        this.items = new ArrayList<>();
    }

    /**
     * Obtiene el comprador propietario del carrito.
     *
     * @return el comprador asociado
     */
    public Buyer getBuyer() {
        return buyer;
    }

    /**
     * Obtiene una copia no modificable de los elementos del carrito.
     *
     * La copia impide agregar o eliminar elementos directamente desde
     * fuera de esta clase. Las modificaciones deben realizarse mediante
     * los métodos definidos por el carrito.
     *
     * @return una copia de los elementos del carrito
     */
    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    /**
     * Indica si el carrito no contiene productos.
     *
     * @return {@code true} si el carrito está vacío;
     *         {@code false} si contiene al menos un elemento
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Determina si un producto se encuentra en el carrito.
     *
     * @param product producto que se desea buscar
     * @return {@code true} si el producto está en el carrito;
     *         de lo contrario, {@code false}
     */
    public boolean containsProduct(Product product) {
        return findItem(product) != null;
    }

    /**
     * Agrega un producto publicado al carrito.
     *
     * Si el producto ya está agregado, aumenta la cantidad del elemento
     * existente. Si todavía no existe, crea un nuevo {@link CartItem}.
     *
     * La validación de la cantidad se delega a la clase {@link CartItem}.
     *
     * @param product producto que se desea agregar
     * @param quantity cantidad que se desea agregar
     *
     * @throws IllegalArgumentException si el producto es nulo
     * @throws IllegalStateException si el producto no está publicado
     */
    public void addProduct(Product product, int quantity) {
        validatePublishedProduct(product);

        CartItem existingItem = findItem(product);

        if (existingItem != null) {
            existingItem.increaseQuantity(quantity);
            return;
        }

        items.add(new CartItem(product, quantity));
    }

    /**
     * Cambia la cantidad solicitada de un producto existente.
     *
     * La validación de la nueva cantidad se delega a
     * {@link CartItem#changeQuantity(int)}.
     *
     * @param product producto cuya cantidad se desea cambiar
     * @param newQuantity nueva cantidad solicitada
     *
     * @throws IllegalStateException si el producto no está en el carrito
     */
    public void changeProductQuantity(
            Product product,
            int newQuantity
    ) {
        CartItem item = findRequiredItem(product);
        item.changeQuantity(newQuantity);
    }

    /**
     * Elimina un producto del carrito.
     *
     * @param product producto que se desea eliminar
     *
     * @throws IllegalStateException si el producto no está en el carrito
     */
    public void removeProduct(Product product) {
        CartItem item = findRequiredItem(product);
        items.remove(item);
    }

    /**
     * Busca el elemento asociado a un producto.
     *
     * @param product producto que se desea buscar
     * @return el elemento encontrado o {@code null} si el producto
     *         no se encuentra en el carrito
     */
    private CartItem findItem(Product product) {
        for (CartItem item : items) {
            if (item.belongsTo(product)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Busca obligatoriamente el elemento asociado a un producto.
     *
     * A diferencia de {@link #findItem(Product)}, este método lanza una
     * excepción cuando el producto no está registrado.
     *
     * @param product producto que se desea buscar
     * @return el elemento asociado al producto
     *
     * @throws IllegalStateException si el producto no está en el carrito
     */
    private CartItem findRequiredItem(Product product) {
        CartItem item = findItem(product);

        if (item == null) {
            throw new IllegalStateException(
                    "Product does not exist in the cart."
            );
        }

        return item;
    }

    /**
     * Valida que el carrito tenga un comprador asociado.
     *
     * @param buyer comprador que se desea validar
     *
     * @throws IllegalArgumentException si el comprador es nulo
     */
    private void validateBuyer(Buyer buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Cart must belong to a buyer."
            );
        }
    }

    /**
     * Valida que el producto exista y esté publicado.
     *
     * Solamente los productos publicados pueden agregarse al carrito.
     *
     * @param product producto que se desea validar
     *
     * @throws IllegalArgumentException si el producto es nulo
     * @throws IllegalStateException si el producto no está publicado
     */
    private void validatePublishedProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Cart product must not be null."
            );
        }

        if (!product.isPublished()) {
            throw new IllegalStateException(
                    "Only published products can be added to the cart."
            );
        }
    }
}