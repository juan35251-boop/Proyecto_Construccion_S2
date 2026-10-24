package application.adapters.output.persistence.jpa.entities;

import application.domain.valueobjects.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA utilizada para almacenar pedidos.
 *
 * marketplace_orders se utiliza como nombre de tabla
 * para evitar conflictos con palabras reservadas de SQL.
 */
@Entity
@Table(name = "marketplace_orders")
public class OrderJpaEntity {

    /**
     * Clave primaria técnica.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador estable del dominio.
     */
    @Column(
            name = "order_id",
            nullable = false,
            unique = true,
            length = 36
    )
    private String orderId;

    /**
     * Identificación del comprador.
     */
    @Column(
            name = "buyer_identification",
            nullable = false,
            length = 100
    )
    private String buyerIdentification;

    /**
     * Estado actual del pedido.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "order_status",
            nullable = false,
            length = 40
    )
    private OrderStatus status;

    /**
     * Productos confirmados en el pedido.
     */
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<OrderItemJpaEntity> items =
            new ArrayList<>();

    /**
     * Constructor requerido por JPA.
     */
    protected OrderJpaEntity() {
    }

    /**
     * Crea una entidad persistente de pedido.
     *
     * @param orderId identificador del pedido
     * @param buyerIdentification identificación del comprador
     * @param status estado del pedido
     */
    public OrderJpaEntity(
            String orderId,
            String buyerIdentification,
            OrderStatus status
    ) {
        validateText(
                orderId,
                "Order identifier"
        );

        validateText(
                buyerIdentification,
                "Buyer identification"
        );

        validateStatus(status);

        this.orderId = orderId;
        this.buyerIdentification =
                buyerIdentification;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getBuyerIdentification() {
        return buyerIdentification;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItemJpaEntity> getItems() {
        return List.copyOf(items);
    }

    /**
     * Actualiza el estado persistente.
     *
     * @param newStatus nuevo estado
     */
    public void changeStatus(
            OrderStatus newStatus
    ) {
        validateStatus(newStatus);
        this.status = newStatus;
    }

    /**
     * Agrega un producto y establece
     * la relación bidireccional.
     *
     * @param item elemento agregado
     */
    public void addItem(
            OrderItemJpaEntity item
    ) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "Order JPA item must not be null."
            );
        }

        item.assignOrder(this);
        items.add(item);
    }

    /**
     * Reemplaza los productos persistentes.
     *
     * @param newItems nuevos elementos
     */
    public void replaceItems(
            List<OrderItemJpaEntity> newItems
    ) {
        if (newItems == null) {
            throw new IllegalArgumentException(
                    "Order JPA items must not be null."
            );
        }

        items.clear();

        for (OrderItemJpaEntity item : newItems) {
            addItem(item);
        }
    }

    /**
     * Valida un texto obligatorio.
     */
    private void validateText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }

    /**
     * Valida el estado del pedido.
     */
    private void validateStatus(
            OrderStatus status
    ) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Order status must not be null."
            );
        }
    }
}