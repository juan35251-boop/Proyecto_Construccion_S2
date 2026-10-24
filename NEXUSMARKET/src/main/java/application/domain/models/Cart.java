package application.domain.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa el carrito de compras de un comprador
 * dentro de NexusMarket.
 *
 * Un carrito pertenece obligatoriamente a un comprador
 * y contiene una colección de elementos de tipo CartItem.
 *
 * Cada carrito posee un identificador estable que permite
 * almacenarlo y recuperarlo desde una base de datos.
 */
public class Cart {

    /**
     * Identificador único del carrito.
     */
    private final String cartId;

    /**
     * Comprador propietario del carrito.
     */
    private final Buyer buyer;

    /**
     * Elementos agregados al carrito.
     */
    private final List<CartItem> items;

    /**
     * Crea un carrito nuevo y vacío.
     *
     * El identificador se genera automáticamente.
     *
     * @param buyer comprador propietario
     */
    public Cart(Buyer buyer) {
        this(
                UUID.randomUUID().toString(),
                buyer,
                List.of()
        );
    }

    /**
     * Reconstruye un carrito previamente almacenado.
     *
     * Este constructor será utilizado por el adaptador JPA
     * para recuperar el carrito y sus productos.
     *
     * @param cartId identificador del carrito
     * @param buyer comprador propietario
     * @param items elementos almacenados
     */
    public Cart(
            String cartId,
            Buyer buyer,
            List<CartItem> items
    ) {
        validateCartId(cartId);
        validateBuyer(buyer);
        validateItems(items);

        this.cartId = cartId;
        this.buyer = buyer;
        this.items = new ArrayList<>(items);
    }

    /**
     * Obtiene el identificador del carrito.
     *
     * @return identificador único
     */
    public String getCartId() {
        return cartId;
    }

    /**
     * Obtiene el comprador propietario.
     *
     * @return comprador asociado
     */
    public Buyer getBuyer() {
        return buyer;
    }

    /**
     * Obtiene una copia de los elementos.
     *
     * @return elementos del carrito
     */
    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    /**
     * Indica si el carrito está vacío.
     *
     * @return true si no contiene productos
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Determina si un producto está en el carrito.
     *
     * La comparación se realiza mediante productId.
     *
     * @param product producto buscado
     * @return true si está agregado
     */
    public boolean containsProduct(Product product) {
        return findItem(product) != null;
    }

    /**
     * Agrega un producto publicado al carrito.
     *
     * Si el producto ya existe, aumenta su cantidad.
     *
     * @param product producto agregado
     * @param quantity cantidad agregada
     */
    public void addProduct(
            Product product,
            int quantity
    ) {
        validatePublishedProduct(product);

        CartItem existingItem =
                findItem(product);

        if (existingItem != null) {
            existingItem.increaseQuantity(quantity);
            return;
        }

        items.add(
                new CartItem(
                        product,
                        quantity
                )
        );
    }

    /**
     * Cambia la cantidad de un producto existente.
     *
     * @param product producto modificado
     * @param newQuantity nueva cantidad
     */
    public void changeProductQuantity(
            Product product,
            int newQuantity
    ) {
        CartItem item =
                findRequiredItem(product);

        item.changeQuantity(newQuantity);
    }

    /**
     * Elimina un producto existente.
     *
     * @param product producto eliminado
     */
    public void removeProduct(
            Product product
    ) {
        CartItem item =
                findRequiredItem(product);

        items.remove(item);
    }

    /**
     * Busca el elemento asociado a un producto.
     *
     * @param product producto buscado
     * @return elemento encontrado o null
     */
    private CartItem findItem(
            Product product
    ) {
        if (product == null) {
            return null;
        }

        for (CartItem item : items) {
            if (item.belongsTo(product)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Busca obligatoriamente un elemento.
     *
     * @param product producto buscado
     * @return elemento encontrado
     */
    private CartItem findRequiredItem(
            Product product
    ) {
        CartItem item =
                findItem(product);

        if (item == null) {
            throw new IllegalStateException(
                    "Product does not exist in the cart."
            );
        }

        return item;
    }

    /**
     * Valida el identificador del carrito.
     */
    private void validateCartId(
            String cartId
    ) {
        if (cartId == null || cartId.isBlank()) {
            throw new IllegalArgumentException(
                    "Cart identifier must not be empty."
            );
        }
    }

    /**
     * Valida el comprador propietario.
     */
    private void validateBuyer(
            Buyer buyer
    ) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Cart must belong to a buyer."
            );
        }
    }

    /**
     * Valida la colección utilizada para reconstruir
     * los elementos del carrito.
     */
    private void validateItems(
            List<CartItem> items
    ) {
        if (items == null) {
            throw new IllegalArgumentException(
                    "Cart items must not be null."
            );
        }

        if (items.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "Cart items must not contain null values."
            );
        }
    }

    /**
     * Valida que el producto exista y esté publicado.
     */
    private void validatePublishedProduct(
            Product product
    ) {
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

    /**
     * Dos carritos son iguales cuando poseen
     * el mismo identificador.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Cart cart)) {
            return false;
        }

        return cartId.equals(cart.cartId);
    }

    /**
     * Genera el código hash utilizando el identificador.
     */
    @Override
    public int hashCode() {
        return Objects.hash(cartId);
    }
}