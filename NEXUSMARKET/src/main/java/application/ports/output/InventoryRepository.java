package application.ports.output;

import application.domain.models.Inventory;

/**
 * Puerto de salida que define las operaciones necesarias
 * para guardar el estado de un inventario.
 *
 * La implementación concreta se realizará posteriormente
 * en la capa de infraestructura.
 */
public interface InventoryRepository {

    /**
     * Guarda o actualiza un inventario.
     *
     * @param inventory inventario que se desea almacenar
     */
    void save(Inventory inventory);
}