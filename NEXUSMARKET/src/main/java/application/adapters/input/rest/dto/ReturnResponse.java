package application.adapters.input.rest.dto;

import application.domain.models.Return;
import application.domain.valueobjects.OrderStatus;

/**
 * Información expuesta por la API
 * para una solicitud de devolución.
 *
 * @param returnId identificador de la devolución
 * @param orderId identificador de la orden
 * @param buyerIdentification identificación del comprador
 * @param orderStatus estado de la orden
 */
public record ReturnResponse(
        String returnId,
        String orderId,
        String buyerIdentification,
        OrderStatus orderStatus
) {

    /**
     * Convierte una devolución del dominio
     * en una respuesta REST.
     *
     * @param returnProcess devolución convertida
     * @return respuesta de la API
     */
    public static ReturnResponse from(
            Return returnProcess
    ) {
        validateReturn(returnProcess);

        return new ReturnResponse(
                returnProcess.getReturnId(),
                returnProcess
                        .getOrder()
                        .getOrderId(),
                returnProcess
                        .getBuyer()
                        .getIdentification(),
                returnProcess
                        .getOrder()
                        .getStatus()
        );
    }

    /**
     * Valida que la devolución y sus relaciones
     * obligatorias estén disponibles.
     */
    private static void validateReturn(
            Return returnProcess
    ) {
        if (returnProcess == null) {
            throw new IllegalArgumentException(
                    "Return must not be null."
            );
        }

        if (returnProcess.getOrder() == null) {
            throw new IllegalStateException(
                    "Return order must not be null."
            );
        }

        if (returnProcess.getBuyer() == null) {
            throw new IllegalStateException(
                    "Return buyer must not be null."
            );
        }
    }
}