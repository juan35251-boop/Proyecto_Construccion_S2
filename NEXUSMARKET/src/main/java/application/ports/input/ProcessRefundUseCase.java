package application.ports.input;

import application.domain.models.Administrator;
import application.domain.models.Refund;
import application.domain.models.Return;

/**
 * Caso de uso encargado de procesar reembolsos.
 */
public interface ProcessRefundUseCase {

    /**
     * Procesa un reembolso utilizando
     * objetos del dominio.
     *
     * @param returnProcess devolución relacionada
     * @param administrator administrador responsable
     * @return reembolso procesado
     */
    Refund process(
            Return returnProcess,
            Administrator administrator
    );

    /**
     * Procesa un reembolso utilizando identificadores.
     *
     * Este método está preparado para ser utilizado
     * desde la API REST.
     *
     * @param administratorIdentification administrador solicitante
     * @param returnId identificador de la devolución
     * @return reembolso procesado
     */
    Refund processByIdentifiers(
            String administratorIdentification,
            String returnId
    );
}