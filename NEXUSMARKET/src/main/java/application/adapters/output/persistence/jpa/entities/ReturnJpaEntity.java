package application.adapters.output.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entidad JPA utilizada para almacenar
 * solicitudes de devolución.
 *
 * Cada devolución pertenece a una sola orden
 * y cada orden solamente puede tener una devolución.
 */
@Entity
@Table(
        name = "return_requests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_return_requests_return_id",
                        columnNames = "return_id"
                ),
                @UniqueConstraint(
                        name = "uk_return_requests_order_id",
                        columnNames = "order_id"
                )
        }
)
public class ReturnJpaEntity {

    /**
     * Identificador técnico utilizado por JPA.
     */
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    /**
     * Identificador permanente de la devolución.
     */
    @Column(
            name = "return_id",
            nullable = false,
            unique = true,
            length = 36
    )
    private String returnId;

    /**
     * Orden relacionada con la devolución.
     */
    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "order_id",
            nullable = false,
            unique = true
    )
    private OrderJpaEntity order;

    /**
     * Constructor requerido por JPA.
     */
    protected ReturnJpaEntity() {
    }

    /**
     * Construye una entidad persistente de devolución.
     *
     * @param returnId identificador de la devolución
     * @param order orden persistida
     */
    public ReturnJpaEntity(
            String returnId,
            OrderJpaEntity order
    ) {
        validateReturnId(returnId);
        validateOrder(order);

        this.returnId = returnId;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public String getReturnId() {
        return returnId;
    }

    public OrderJpaEntity getOrder() {
        return order;
    }

    /**
     * Actualiza el identificador funcional.
     */
    public void changeReturnId(
            String returnId
    ) {
        validateReturnId(returnId);
        this.returnId = returnId;
    }

    /**
     * Actualiza la orden relacionada.
     */
    public void changeOrder(
            OrderJpaEntity order
    ) {
        validateOrder(order);
        this.order = order;
    }

    private void validateReturnId(
            String returnId
    ) {
        if (returnId == null
                || returnId.isBlank()) {
            throw new IllegalArgumentException(
                    "Return identifier must not be empty."
            );
        }
    }

    private void validateOrder(
            OrderJpaEntity order
    ) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Return order JPA entity must not be null."
            );
        }
    }
}