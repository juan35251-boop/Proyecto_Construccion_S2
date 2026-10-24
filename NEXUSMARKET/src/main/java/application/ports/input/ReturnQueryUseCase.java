package application.ports.input;

import application.domain.models.Return;
import application.domain.models.User;

import java.util.List;

/**
 * Caso de uso encargado de consultar devoluciones.
 */
public interface ReturnQueryUseCase {

    /**
     * Consulta las devoluciones visibles
     * para un usuario del dominio.
     *
     * @param requestedBy usuario solicitante
     * @return devoluciones autorizadas
     */
    List<Return> findAccessibleReturns(
            User requestedBy
    );

    /**
     * Consulta devoluciones mediante la identificación
     * del usuario solicitante.
     *
     * @param requesterIdentification identificación del usuario
     * @return devoluciones autorizadas
     */
    List<Return> findAccessibleReturnsByRequesterIdentification(
            String requesterIdentification
    );

    /**
     * Consulta una devolución específica respetando
     * los permisos del solicitante.
     *
     * @param requesterIdentification identificación del usuario
     * @param returnId identificador de la devolución
     * @return devolución autorizada
     */
    Return findAccessibleReturnByIdentifiers(
            String requesterIdentification,
            String returnId
    );
}