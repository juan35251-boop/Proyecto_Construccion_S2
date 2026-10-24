package application.adapters.input.rest.dto;

import application.domain.models.Refund;

/**
 * Información expuesta por la API
 * para un reembolso.
 *
 * @param refundId identificador del reembolso
 * @param returnId identificador de la devolución
 * @param orderId identificador de la orden
 * @param buyerIdentification identificación del comprador
 * @param processedByIdentification identificación del administrador
 */
public record RefundResponse(
        String refundId,
        String returnId,
        String orderId,
        String buyerIdentification,
        String processedByIdentification
) {

    /**
     * Convierte un reembolso del dominio
     * en una respuesta REST.
     *
     * @param refund reembolso convertido
     * @return respuesta de la API
     */
    public static RefundResponse from(
            Refund refund
    ) {
        validateRefund(refund);

        return new RefundResponse(
                refund.getRefundId(),
                refund
                        .getReturnProcess()
                        .getReturnId(),
                refund
                        .getOrder()
                        .getOrderId(),
                refund
                        .getBuyer()
                        .getIdentification(),
                refund
                        .getProcessedBy()
                        .getIdentification()
        );
    }

    /**
     * Valida el reembolso y sus relaciones
     * obligatorias.
     */
    private static void validateRefund(
            Refund refund
    ) {
        if (refund == null) {
            throw new IllegalArgumentException(
                    "Refund must not be null."
            );
        }

        if (refund.getReturnProcess() == null) {
            throw new IllegalStateException(
                    "Refund return must not be null."
            );
        }

        if (refund.getOrder() == null) {
            throw new IllegalStateException(
                    "Refund order must not be null."
            );
        }

        if (refund.getBuyer() == null) {
            throw new IllegalStateException(
                    "Refund buyer must not be null."
            );
        }

        if (refund.getProcessedBy() == null) {
            throw new IllegalStateException(
                    "Refund administrator must not be null."
            );
        }
    }
}