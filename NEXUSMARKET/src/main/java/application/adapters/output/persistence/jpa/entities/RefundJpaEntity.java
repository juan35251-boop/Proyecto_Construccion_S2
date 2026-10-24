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
 * Entidad JPA utilizada para almacenar reembolsos.
 *
 * Cada reembolso pertenece a una única devolución
 * y cada devolución solamente puede ser reembolsada
 * una vez.
 */
@Entity
@Table(
        name = "refunds",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_refunds_refund_id",
                        columnNames = "refund_id"
                ),
                @UniqueConstraint(
                        name = "uk_refunds_return_id",
                        columnNames = "return_id"
                )
        }
)
public class RefundJpaEntity {

    /**
     * Identificador técnico de JPA.
     */
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    /**
     * Identificador permanente del reembolso.
     */
    @Column(
            name = "refund_id",
            nullable = false,
            unique = true,
            length = 36
    )
    private String refundId;

    /**
     * Devolución que origina el reembolso.
     */
    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "return_id",
            nullable = false,
            unique = true
    )
    private ReturnJpaEntity returnProcess;

    /**
     * Identificación del administrador que procesó
     * el reembolso.
     */
    @Column(
            name = "processed_by_identification",
            nullable = false,
            length = 100
    )
    private String processedByIdentification;

    /**
     * Constructor requerido por JPA.
     */
    protected RefundJpaEntity() {
    }

    /**
     * Construye una entidad persistente de reembolso.
     *
     * @param refundId identificador del reembolso
     * @param returnProcess devolución persistida
     * @param processedByIdentification administrador responsable
     */
    public RefundJpaEntity(
            String refundId,
            ReturnJpaEntity returnProcess,
            String processedByIdentification
    ) {
        validateRefundId(refundId);
        validateReturn(returnProcess);

        validateAdministratorIdentification(
                processedByIdentification
        );

        this.refundId = refundId;
        this.returnProcess = returnProcess;
        this.processedByIdentification =
                processedByIdentification;
    }

    public Long getId() {
        return id;
    }

    public String getRefundId() {
        return refundId;
    }

    public ReturnJpaEntity getReturnProcess() {
        return returnProcess;
    }

    public String getProcessedByIdentification() {
        return processedByIdentification;
    }

    /**
     * Actualiza el identificador funcional.
     */
    public void changeRefundId(
            String refundId
    ) {
        validateRefundId(refundId);
        this.refundId = refundId;
    }

    /**
     * Actualiza la devolución relacionada.
     */
    public void changeReturnProcess(
            ReturnJpaEntity returnProcess
    ) {
        validateReturn(returnProcess);
        this.returnProcess = returnProcess;
    }

    /**
     * Actualiza el administrador responsable.
     */
    public void changeProcessedByIdentification(
            String identification
    ) {
        validateAdministratorIdentification(
                identification
        );

        this.processedByIdentification =
                identification;
    }

    private void validateRefundId(
            String refundId
    ) {
        if (refundId == null
                || refundId.isBlank()) {
            throw new IllegalArgumentException(
                    "Refund identifier must not be empty."
            );
        }
    }

    private void validateReturn(
            ReturnJpaEntity returnProcess
    ) {
        if (returnProcess == null) {
            throw new IllegalArgumentException(
                    "Refund return JPA entity must not be null."
            );
        }
    }

    private void validateAdministratorIdentification(
            String identification
    ) {
        if (identification == null
                || identification.isBlank()) {
            throw new IllegalArgumentException(
                    "Refund administrator identification must not be empty."
            );
        }
    }
}