package application.services.support;

import application.domain.models.Product;
import application.ports.output.ProductQueryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria para probar consultas de productos
 * sin utilizar una base de datos.
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
        products.add(product);
    }

    /**
     * Devuelve una copia de todos los productos almacenados.
     */
    @Override
    public List<Product> findAll() {
        return List.copyOf(products);
    }
}