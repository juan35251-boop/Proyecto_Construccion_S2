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
     * Busca un vendedor utilizando directamente
     * al usuario que realiza la consulta.
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
     * Obtiene todos los vendedores utilizando directamente
     * al usuario que realiza la consulta.
     *
     * @param requestedBy administrador o supervisor
     *                    que realiza la consulta
     * @return lista de vendedores
     */
    List<Seller> findAll(User requestedBy);

    /**
     * Busca un vendedor utilizando la identificación
     * del usuario que realiza la consulta.
     *
     * Esta operación permite que los adaptadores REST
     * trabajen con la identificación recibida mediante
     * una cabecera HTTP.
     *
     * @param requesterIdentification identificación del solicitante
     * @param sellerIdentification identificación del vendedor consultado
     * @return vendedor encontrado
     */
    Seller findByIdentification(
            String requesterIdentification,
            String sellerIdentification
    );

    /**
     * Obtiene todos los vendedores utilizando la identificación
     * del usuario que realiza la consulta.
     *
     * Solamente un administrador o supervisor activo
     * puede realizar esta operación.
     *
     * @param requesterIdentification identificación del solicitante
     * @return lista de vendedores
     */
    List<Seller> findAll(
            String requesterIdentification
    );
}