package application.services.support;

import application.domain.models.InventoryMovement;
import application.ports.output.InventoryMovementRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria utilizado para comprobar
 * el guardado de movimientos de inventario.
 */
public class InMemoryInventoryMovementRepository
        implements InventoryMovementRepository {

    private final List<InventoryMovement> movements =
            new ArrayList<>();

    /**
     * Guarda un movimiento en memoria.
     */
    @Override
    public void save(InventoryMovement movement) {
        movements.add(movement);
    }

    /**
     * Devuelve una copia de los movimientos guardados.
     */
    public List<InventoryMovement> findAll() {
        return List.copyOf(movements);
    }

    /**
     * Indica cuántos movimientos fueron guardados.
     */
    public int getSaveCount() {
        return movements.size();
    }
}