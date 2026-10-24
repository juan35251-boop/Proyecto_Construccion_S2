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
 * Entidad JPA utilizada para almacenar facturas.
 *
 * Cada factura pertenece a un único pedido y cada pedido
 * solamente puede tener una factura.
 */
@Entity
@Table(
        name = "invoices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_invoices_invoice_id",
                        columnNames = "invoice_id"
                ),
                @UniqueConstraint(
                        name = "uk_invoices_order_id",
                        columnNames = "order_id"
                )
        }
)
public class InvoiceJpaEntity {

    /**
     * Identificador técnico utilizado por JPA.
     */
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    /**
     * Identificador funcional de la factura.
     */
    @Column(
            name = "invoice_id",
            nullable = false,
            unique = true,
            length = 36
    )
    private String invoiceId;

    /**
     * Pedido asociado con la factura.
     *
     * La relación es uno a uno porque cada pedido
     * solamente puede generar una factura.
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
    protected InvoiceJpaEntity() {
    }

    /**
     * Construye una entidad persistente de factura.
     *
     * @param invoiceId identificador funcional
     * @param order pedido persistido
     */
    public InvoiceJpaEntity(
            String invoiceId,
            OrderJpaEntity order
    ) {
        validateInvoiceId(invoiceId);
        validateOrder(order);

        this.invoiceId = invoiceId;
        this.order = order;
    }

    /**
     * Obtiene el identificador técnico.
     *
     * @return identificador técnico
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene el identificador funcional.
     *
     * @return identificador de la factura
     */
    public String getInvoiceId() {
        return invoiceId;
    }

    /**
     * Obtiene el pedido asociado.
     *
     * @return entidad persistente del pedido
     */
    public OrderJpaEntity getOrder() {
        return order;
    }

    /**
     * Valida el identificador funcional.
     *
     * @param invoiceId identificador validado
     */
    private void validateInvoiceId(
            String invoiceId
    ) {
        if (invoiceId == null
                || invoiceId.isBlank()) {
            throw new IllegalArgumentException(
                    "Invoice identifier must not be empty."
            );
        }
    }

    /**
     * Valida el pedido persistido.
     *
     * @param order pedido validado
     */
    private void validateOrder(
            OrderJpaEntity order
    ) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Invoice order JPA entity must not be null."
            );
        }
    }
}