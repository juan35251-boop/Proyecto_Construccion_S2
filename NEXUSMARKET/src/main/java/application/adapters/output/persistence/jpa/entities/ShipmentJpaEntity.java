package application.adapters.output.persistence.jpa.entities;

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
 * Entidad JPA utilizada para almacenar envíos.
 *
 * Cada envío pertenece a un único pedido y cada pedido
 * solamente puede tener un envío.
 *
 * El estado del proceso logístico se obtiene directamente
 * desde el estado del pedido relacionado.
 */
@Entity
@Table(
        name = "shipments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_shipments_shipment_id",
                        columnNames = "shipment_id"
                ),
                @UniqueConstraint(
                        name = "uk_shipments_order_id",
                        columnNames = "order_id"
                )
        }
)
public class ShipmentJpaEntity {

    /**
     * Identificador técnico utilizado por JPA.
     */
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    /**
     * Identificador funcional del envío.
     */
    @jakarta.persistence.Column(
            name = "shipment_id",
            nullable = false,
            unique = true,
            length = 36
    )
    private String shipmentId;

    /**
     * Pedido asociado al envío.
     *
     * La relación es uno a uno porque un pedido solamente
     * debe generar un proceso de envío.
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
    protected ShipmentJpaEntity() {
    }

    /**
     * Construye una entidad de envío.
     *
     * @param shipmentId identificador funcional
     * @param order pedido persistido asociado
     */
    public ShipmentJpaEntity(
            String shipmentId,
            OrderJpaEntity order
    ) {
        this.shipmentId = shipmentId;
        this.order = order;
    }

    /**
     * Obtiene el identificador técnico.
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene el identificador funcional.
     */
    public String getShipmentId() {
        return shipmentId;
    }

    /**
     * Obtiene el pedido relacionado.
     */
    public OrderJpaEntity getOrder() {
        return order;
    }

    /**
     * Actualiza el identificador funcional.
     *
     * @param shipmentId identificador del envío
     */
    public void changeShipmentId(
            String shipmentId
    ) {
        this.shipmentId = shipmentId;
    }

    /**
     * Actualiza el pedido relacionado.
     *
     * @param order pedido persistido
     */
    public void changeOrder(
            OrderJpaEntity order
    ) {
        this.order = order;
    }
}