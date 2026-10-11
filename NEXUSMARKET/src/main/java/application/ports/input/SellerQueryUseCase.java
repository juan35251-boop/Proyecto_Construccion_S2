package application.ports.input;

import application.domain.models.Seller;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar vendedores.
 *
 * Un vendedor solamente puede consultar su propia
 * información. Los administradores y supervisores
 * tienen acceso global de lectura.
 */
public interface SellerQueryUseCase {

    /**
     * Busca un vendedor mediante su identificación.
     *
     * @param requestedBy usuario que realiza la consulta
     * @param identification identificación del vendedor
     * @return vendedor encontrado
     */
    Seller findByIdentification(
            User requestedBy,
            String identification
    );

    /**
     * Obtiene todos los vendedores registrados.
     *
     * @param requestedBy administrador o supervisor
     *                    que realiza la consulta
     * @return lista de vendedores
     */
    List<Seller> findAll(User requestedBy);
}