package application.ports.output;

import application.domain.models.Product;

import java.util.List;

/**
 * Puerto de salida utilizado para consultar productos.
 *
 * Se mantiene separado del repositorio encargado de guardar
 * productos para distinguir las operaciones de lectura de
 * las operaciones de escritura.
 */
public interface ProductQueryRepository {

    /**
     * Obtiene todos los productos registrados.
     *
     * El servicio de aplicación será responsable de filtrar
     * los productos según los permisos del usuario.
     *
     * @return lista de productos registrados
     */
    List<Product> findAll();
}