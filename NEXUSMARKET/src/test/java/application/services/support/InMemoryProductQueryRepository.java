package application.services.support;

import application.domain.models.Product;
import application.ports.output.ProductQueryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio en memoria utilizado para probar
 * las consultas de productos sin depender
 * de una base de datos real.
 */
public class InMemoryProductQueryRepository
        implements ProductQueryRepository {

    private final List<Product> products =
            new ArrayList<>();

    /**
     * Agrega un producto al repositorio de prueba.
     *
     * @param product producto que se desea almacenar
     */
    public void add(Product product) {
        validateProduct(product);
        products.add(product);
    }

    /**
     * Busca un producto mediante su identificador.
     *
     * La comparación se realiza usando productId,
     * de la misma manera que ocurrirá en la base de datos.
     *
     * @param productId identificador del producto
     * @return producto encontrado o un resultado vacío
     */
    @Override
    public Optional<Product> findById(
            String productId
    ) {
        validateProductId(productId);

        return products.stream()
                .filter(
                        product ->
                                product.getProductId()
                                        .equals(productId)
                )
                .findFirst();
    }

    /**
     * Devuelve una copia de todos los productos almacenados.
     *
     * @return lista de productos
     */
    @Override
    public List<Product> findAll() {
        return List.copyOf(products);
    }

    /**
     * Valida que el producto agregado no sea nulo.
     *
     * @param product producto recibido
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product must not be null."
            );
        }
    }

    /**
     * Valida el identificador utilizado en la búsqueda.
     *
     * @param productId identificador recibido
     */
    private void validateProductId(
            String productId
    ) {
        if (productId == null
                || productId.isBlank()) {
            throw new IllegalArgumentException(
                    "Product identification must not be empty."
            );
        }
    }
}