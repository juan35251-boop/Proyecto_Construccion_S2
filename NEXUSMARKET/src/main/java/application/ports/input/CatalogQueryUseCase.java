package application.ports.input;

import application.domain.models.Product;

import java.util.List;

/**
 * Puerto de entrada utilizado para consultar
 * el catálogo de NexusMarket.
 */
public interface CatalogQueryUseCase {

    /**
     * Obtiene los productos visibles para el usuario.
     *
     * @param requesterIdentification identificación
     *        del usuario solicitante
     * @return productos que puede consultar
     */
    List<Product> findAccessibleProducts(
            String requesterIdentification
    );
}