package application.services.support;

import application.domain.models.Warehouse;
import application.ports.output.WarehouseRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de bodegas almacenado en memoria.
 *
 * Se utiliza para probar los servicios sin conectar
 * todavía una base de datos.
 */
public class InMemoryWarehouseRepository
        implements WarehouseRepository {

    private final List<Warehouse> warehouses =
            new ArrayList<>();

    private int saveCount;

    /**
     * Guarda una bodega en memoria.
     */
    @Override
    public void save(Warehouse warehouse) {
        if (!warehouses.contains(warehouse)) {
            warehouses.add(warehouse);
        }

        saveCount++;
    }

    /**
     * Comprueba si la instancia de bodega está registrada.
     */
    @Override
    public boolean exists(Warehouse warehouse) {
        return warehouses.contains(warehouse);
    }

    /**
     * Devuelve una copia de las bodegas almacenadas.
     */
    @Override
    public List<Warehouse> findAll() {
        return List.copyOf(warehouses);
    }

    /**
     * Indica cuántas veces se ejecutó el guardado.
     */
    public int getSaveCount() {
        return saveCount;
    }
}