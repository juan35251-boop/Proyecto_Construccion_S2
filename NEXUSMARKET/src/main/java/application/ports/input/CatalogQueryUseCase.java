package application.ports.input;

import application.domain.models.Product;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar el catálogo de NexusMarket.
 *
 * Los productos visibles dependen del rol
 * del usuario que realiza la consulta.
 */
public interface CatalogQueryUseCase {

    /**
     * Obtiene los productos que el usuario
     * tiene autorización para consultar.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return lista de productos accesibles
     */
    List<Product> findAccessibleProducts(
            User requestedBy
    );
}