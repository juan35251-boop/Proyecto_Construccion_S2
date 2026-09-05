package application.ports.output;

import application.domain.models.Product;

/**
 * Puerto de salida que define las operaciones necesarias
 * para almacenar productos.
 *
 * La interfaz pertenece al núcleo de la aplicación y no conoce
 * ninguna tecnología de base de datos.
 */
public interface ProductRepository {

    /**
     * Guarda un producto en el sistema de persistencia.
     *
     * @param product producto que se desea guardar
     */
    void save(Product product);
}
