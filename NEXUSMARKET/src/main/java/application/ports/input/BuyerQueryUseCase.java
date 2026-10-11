package application.ports.input;

import application.domain.models.Buyer;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar compradores.
 *
 * Un comprador solamente puede consultar su propia
 * información. Los administradores y supervisores
 * tienen acceso global de lectura.
 */
public interface BuyerQueryUseCase {

    /**
     * Busca un comprador por su identificación.
     *
     * @param requestedBy usuario que realiza la consulta
     * @param identification identificación buscada
     * @return comprador encontrado
     */
    Buyer findByIdentification(
            User requestedBy,
            String identification
    );

    /**
     * Obtiene todos los compradores registrados.
     *
     * @param requestedBy administrador o supervisor
     *                    que realiza la consulta
     * @return lista de compradores
     */
    List<Buyer> findAll(User requestedBy);
}