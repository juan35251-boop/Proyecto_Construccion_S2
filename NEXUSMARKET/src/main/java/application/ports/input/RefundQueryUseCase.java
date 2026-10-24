package application.ports.input;

import application.domain.models.Refund;
import application.domain.models.User;

import java.util.List;

/**
 * Caso de uso encargado de consultar reembolsos.
 */
public interface RefundQueryUseCase {

    /**
     * Consulta los reembolsos visibles
     * para un usuario del dominio.
     *
     * @param requestedBy usuario solicitante
     * @return reembolsos autorizados
     */
    List<Refund> findAccessibleRefunds(
            User requestedBy
    );

    /**
     * Consulta reembolsos mediante la identificación
     * del usuario solicitante.
     *
     * @param requesterIdentification identificación del usuario
     * @return reembolsos autorizados
     */
    List<Refund>
    findAccessibleRefundsByRequesterIdentification(
            String requesterIdentification
    );

    /**
     * Consulta un reembolso específico respetando
     * los permisos del solicitante.
     *
     * @param requesterIdentification identificación del usuario
     * @param refundId identificador del reembolso
     * @return reembolso autorizado
     */
    Refund findAccessibleRefundByIdentifiers(
            String requesterIdentification,
            String refundId
    );
}