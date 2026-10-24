package application.ports.output;

import application.domain.models.Product;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida utilizado para consultar productos.
 */
public interface ProductQueryRepository {

    /**
     * Busca un producto mediante su identificador.
     *
     * @param productId identificador del producto
     * @return producto encontrado o un resultado vacío
     */
    Optional<Product> findById(String productId);

    /**
     * Obtiene todos los productos registrados.
     *
     * @return lista de productos
     */
    List<Product> findAll();
}