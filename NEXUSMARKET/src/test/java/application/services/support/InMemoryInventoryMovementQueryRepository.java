package application.services.support;

import application.domain.models.InventoryMovement;
import application.ports.output.InventoryMovementQueryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria para probar consultas
 * de movimientos de inventario.
 */
public class InMemoryInventoryMovementQueryRepository
        implements InventoryMovementQueryRepository {

    private final List<InventoryMovement> movements =
            new ArrayList<>();

    /**
     * Agrega un movimiento al repositorio de prueba.
     */
    public void add(InventoryMovement movement) {
        movements.add(movement);
    }

    /**
     * Devuelve una copia de los movimientos almacenados.
     */
    @Override
    public List<InventoryMovement> findAll() {
        return List.copyOf(movements);
    }
}