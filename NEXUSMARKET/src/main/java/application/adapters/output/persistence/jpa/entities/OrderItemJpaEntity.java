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
 * confirmado dentro de un pedido.
 */
@Entity
@Table(
        name = "order_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_order_item_product",
                        columnNames = {
                                "order_database_id",
                                "product_id"
                        }
                )
        }
)
public class OrderItemJpaEntity {

    /**
     * Clave primaria técnica.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Pedido propietario del elemento.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "order_database_id",
            nullable = false
    )
    private OrderJpaEntity order;

    /**
     * Identificador del producto confirmado.
     */
    @Column(
            name = "product_id",
            nullable = false,
            length = 36
    )
    private String productId;

    /**
     * Cantidad confirmada.
     */
    @Column(
            name = "quantity",
            nullable = false
    )
    private int quantity;

    /**
     * Constructor requerido por JPA.
     */
    protected OrderItemJpaEntity() {
    }

    /**
     * Crea un elemento persistente.
     *
     * @param productId identificador del producto
     * @param quantity cantidad confirmada
     */
    public OrderItemJpaEntity(
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
     * Asigna el pedido propietario.
     *
     * @param order pedido persistente
     */
    public void assignOrder(
            OrderJpaEntity order
    ) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order JPA entity must not be null."
            );
        }

        this.order = order;
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
}