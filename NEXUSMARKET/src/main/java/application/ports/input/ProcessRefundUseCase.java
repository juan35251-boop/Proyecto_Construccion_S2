package application.ports.input;

import application.domain.models.Administrator;
import application.domain.models.Refund;
import application.domain.models.Return;

/**
 * Puerto de entrada que define el caso de uso
 * para procesar reembolsos en NexusMarket.
 *
 * Todo reembolso debe estar asociado con una
 * devolución y ser procesado por un administrador activo.
 */
public interface ProcessRefundUseCase {

    /**
     * Procesa y registra un reembolso.
     *
     * @param returnProcess devolución que origina el reembolso
     * @param administrator administrador que procesa el reembolso
     * @return reembolso procesado
     *
     * @throws IllegalArgumentException si la devolución
     *         o el administrador son nulos
     * @throws IllegalStateException si el administrador
     *         no se encuentra activo
     */
    Refund process(
            Return returnProcess,
            Administrator administrator
    );
}