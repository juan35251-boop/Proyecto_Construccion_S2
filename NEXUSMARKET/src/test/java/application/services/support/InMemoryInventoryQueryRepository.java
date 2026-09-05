package application.services.support;

import application.domain.models.Inventory;
import application.ports.output.InventoryQueryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria utilizado para probar
 * las consultas de inventario sin una base de datos.
 */
public class InMemoryInventoryQueryRepository
        implements InventoryQueryRepository {

    private final List<Inventory> inventories =
            new ArrayList<>();

    /**
     * Agrega un inventario al repositorio de prueba.
     *
     * @param inventory inventario que se desea almacenar
     */
    public void add(Inventory inventory) {
        inventories.add(inventory);
    }

    /**
     * Obtiene una copia de todos los inventarios almacenados.
     */
    @Override
    public List<Inventory> findAll() {
        return List.copyOf(inventories);
    }
}