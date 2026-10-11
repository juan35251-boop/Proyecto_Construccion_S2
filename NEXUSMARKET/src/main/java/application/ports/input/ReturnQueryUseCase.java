package application.ports.input;

import application.domain.models.Return;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar devoluciones.
 *
 * El comprador solamente puede consultar sus
 * propias devoluciones. Los administradores y
 * supervisores tienen acceso global.
 */
public interface ReturnQueryUseCase {

    /**
     * Obtiene las devoluciones visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return lista de devoluciones autorizadas
     */
    List<Return> findAccessibleReturns(
            User requestedBy
    );
}