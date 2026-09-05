package application.ports.output;

import application.domain.models.Inventory;

import java.util.List;

/**
 * Puerto de salida utilizado para consultar inventarios.
 *
 * Su futura implementación podrá obtener la información
 * desde una base de datos, sin que la capa de aplicación
 * dependa de una tecnología específica.
 */
public interface InventoryQueryRepository {

    /**
     * Obtiene todos los inventarios registrados.
     *
     * @return lista de inventarios
     */
    List<Inventory> findAll();
}