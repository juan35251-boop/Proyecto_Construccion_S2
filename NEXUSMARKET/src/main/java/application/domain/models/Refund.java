package application.domain.models;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa un reembolso procesado a partir
 * de una devolución.
 *
 * Todo reembolso debe estar asociado con una devolución
 * válida y debe ser procesado por un administrador activo.
 */
public class Refund {

    /**
     * Identificador permanente del reembolso.
     */
    private final String refundId;

    /**
     * Devolución que origina el reembolso.
     */
    private final Return returnProcess;

    /**
     * Administrador responsable de procesar
     * el reembolso.
     */
    private final Administrator processedBy;

    /**
     * Crea un reembolso nuevo y genera
     * automáticamente su identificador.
     *
     * @param returnProcess devolución relacionada
     * @param processedBy administrador responsable
     */
    public Refund(
            Return returnProcess,
            Administrator processedBy
    ) {
        this(
                UUID.randomUUID().toString(),
                returnProcess,
                processedBy
        );
    }

    /**
     * Reconstruye un reembolso almacenado previamente.
     *
     * Este constructor conserva el identificador
     * recuperado desde la base de datos.
     *
     * @param refundId identificador permanente
     * @param returnProcess devolución relacionada
     * @param processedBy administrador responsable
     */
    public Refund(
            String refundId,
            Return returnProcess,
            Administrator processedBy
    ) {
        validateRefundId(refundId);
        validateReturn(returnProcess);
        validateAdministrator(processedBy);

        this.refundId = refundId.trim();
        this.returnProcess = returnProcess;
        this.processedBy = processedBy;
    }

    /**
     * Obtiene el identificador del reembolso.
     *
     * @return identificador permanente
     */
    public String getRefundId() {
        return refundId;
    }

    /**
     * Obtiene la devolución asociada.
     *
     * @return proceso de devolución
     */
    public Return getReturnProcess() {
        return returnProcess;
    }

    /**
     * Obtiene el administrador que procesó
     * el reembolso.
     *
     * @return administrador responsable
     */
    public Administrator getProcessedBy() {
        return processedBy;
    }

    /**
     * Obtiene el comprador relacionado.
     *
     * @return comprador que solicitó la devolución
     */
    public Buyer getBuyer() {
        return returnProcess.getBuyer();
    }

    /**
     * Obtiene la orden que originó
     * la devolución.
     *
     * @return orden relacionada
     */
    public Order getOrder() {
        return returnProcess.getOrder();
    }

    /**
     * Determina si el reembolso corresponde
     * a una devolución.
     *
     * La comparación utiliza el identificador permanente
     * para funcionar después de reconstruir los objetos
     * desde MySQL.
     *
     * @param comparedReturn devolución comparada
     * @return true si corresponde a la misma devolución
     */
    public boolean belongsTo(
            Return comparedReturn
    ) {
        return comparedReturn != null
                && returnProcess
                        .getReturnId()
                        .equals(
                                comparedReturn
                                        .getReturnId()
                        );
    }

    /**
     * Valida el identificador del reembolso.
     */
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

    /**
     * Valida que exista una devolución.
     */
    private void validateReturn(
            Return returnProcess
    ) {
        if (returnProcess == null) {
            throw new IllegalArgumentException(
                    "Refund must be associated with a return."
            );
        }
    }

    /**
     * Valida que el administrador exista
     * y se encuentre activo.
     */
    private void validateAdministrator(
            Administrator administrator
    ) {
        if (administrator == null) {
            throw new IllegalArgumentException(
                    "Refund must be processed by an administrator."
            );
        }

        if (!administrator.isActive()) {
            throw new IllegalStateException(
                    "Only an active administrator can process refunds."
            );
        }
    }

    /**
     * Compara reembolsos mediante su identificador.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Refund refund)) {
            return false;
        }

        return refundId.equals(
                refund.refundId
        );
    }

    /**
     * Genera el código hash mediante
     * el identificador permanente.
     */
    @Override
    public int hashCode() {
        return Objects.hash(refundId);
    }
}