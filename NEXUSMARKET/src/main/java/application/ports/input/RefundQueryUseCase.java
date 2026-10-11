package application.ports.input;

import application.domain.models.Refund;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar reembolsos.
 *
 * El comprador solamente puede consultar sus
 * propios reembolsos. Los administradores y
 * supervisores tienen acceso global.
 */
public interface RefundQueryUseCase {

    /**
     * Obtiene los reembolsos visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return lista de reembolsos autorizados
     */
    List<Refund> findAccessibleRefunds(
            User requestedBy
    );
}