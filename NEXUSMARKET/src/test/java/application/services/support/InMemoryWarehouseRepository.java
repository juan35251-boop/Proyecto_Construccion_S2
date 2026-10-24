package application.services.support;

import application.domain.models.Warehouse;
import application.ports.output.WarehouseRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación en memoria del repositorio de bodegas.
 *
 * Se utiliza exclusivamente durante las pruebas para evitar
 * depender de una base de datos real.
 */
public class InMemoryWarehouseRepository
        implements WarehouseRepository {

    private final List<Warehouse> warehouses;
    private int saveCount;

    /**
     * Construye un repositorio vacío.
     */
    public InMemoryWarehouseRepository() {
        this.warehouses = new ArrayList<>();
        this.saveCount = 0;
    }

    /**
     * Guarda una bodega en memoria y registra
     * cuántas veces fue ejecutada esta operación.
     *
     * @param warehouse bodega que será almacenada
     */
    @Override
    public void save(Warehouse warehouse) {
        validateWarehouse(warehouse);

        warehouses.add(warehouse);
        saveCount++;
    }

    /**
     * Comprueba si una bodega se encuentra
     * almacenada en memoria.
     *
     * @param warehouse bodega que se desea comprobar
     * @return true si la bodega está almacenada
     */
    @Override
    public boolean exists(Warehouse warehouse) {
        validateWarehouse(warehouse);
        return warehouses.contains(warehouse);
    }

    /**
     * Obtiene una copia inmutable de todas
     * las bodegas almacenadas.
     *
     * @return lista de bodegas
     */
    @Override
    public List<Warehouse> findAll() {
        return List.copyOf(warehouses);
    }

    /**
     * Obtiene el número de veces que se ejecutó
     * la operación de guardado.
     *
     * Este método permite comprobar en las pruebas
     * que el servicio solicitó guardar la bodega.
     *
     * @return cantidad de operaciones de guardado
     */
    public int getSaveCount() {
        return saveCount;
    }

    /**
     * Obtiene la cantidad total de bodegas
     * actualmente almacenadas.
     *
     * @return cantidad de bodegas almacenadas
     */
    public int count() {
        return warehouses.size();
    }

    /**
     * Valida que la bodega recibida no sea nula.
     *
     * @param warehouse bodega recibida
     */
    private void validateWarehouse(
            Warehouse warehouse
    ) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Warehouse must not be null."
            );
        }
    }
}