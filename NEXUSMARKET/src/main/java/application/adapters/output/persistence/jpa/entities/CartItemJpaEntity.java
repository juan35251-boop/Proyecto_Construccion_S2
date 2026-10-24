package application.adapters.output.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entidad JPA que representa un producto
 * almacenado dentro de un carrito.
 */
@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_item_product",
                        columnNames = {
                                "cart_database_id",
                                "product_id"
                        }
                )
        }
)
public class CartItemJpaEntity {

    /**
     * Clave primaria técnica.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Carrito al cual pertenece el elemento.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "cart_database_id",
            nullable = false
    )
    private CartJpaEntity cart;

    /**
     * Identificador del producto.
     */
    @Column(
            name = "product_id",
            nullable = false,
            length = 36
    )
    private String productId;

    /**
     * Cantidad solicitada.
     */
    @Column(
            name = "quantity",
            nullable = false
    )
    private int quantity;

    /**
     * Constructor requerido por JPA.
     */
    protected CartItemJpaEntity() {
    }

    /**
     * Crea un elemento persistente.
     *
     * @param productId identificador del producto
     * @param quantity cantidad solicitada
     */
    public CartItemJpaEntity(
            String productId,
            int quantity
    ) {
        validateProductId(productId);
        validateQuantity(quantity);

        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Asigna el carrito propietario.
     *
     * @param cart carrito persistente
     */
    public void assignCart(
            CartJpaEntity cart
    ) {
        if (cart == null) {
            throw new IllegalArgumentException(
                    "Cart JPA entity must not be null."
            );
        }

        this.cart = cart;
    }

    /**
     * Valida el identificador del producto.
     */
    private void validateProductId(
            String productId
    ) {
        if (productId == null
                || productId.isBlank()) {
            throw new IllegalArgumentException(
                    "Product identifier must not be empty."
            );
        }
    }

    /**
     * Valida la cantidad almacenada.
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
}