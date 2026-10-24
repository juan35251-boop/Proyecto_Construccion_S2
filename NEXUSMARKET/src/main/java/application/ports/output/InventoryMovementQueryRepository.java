package application.ports.output;

import application.domain.models.InventoryMovement;

import java.util.Comparator;
import java.util.List;

/**
 * Puerto de salida utilizado para consultar
 * movimientos históricos de inventario.
 *
 * Su implementación puede utilizar MongoDB sin que
 * los servicios conozcan esa tecnología.
 */
public interface InventoryMovementQueryRepository {

    /**
     * Obtiene todos los movimientos registrados.
     *
     * @return movimientos de inventario
     */
    List<InventoryMovement> findAll();

    /**
     * Obtiene los movimientos asociados
     * con un inventario.
     *
     * La implementación predeterminada mantiene
     * compatibles los repositorios en memoria existentes.
     *
     * @param inventoryId identificador del inventario
     * @return movimientos ordenados desde el más reciente
     */
    default List<InventoryMovement> findByInventoryId(
            String inventoryId
    ) {
        if (inventoryId == null
                || inventoryId.isBlank()) {
            return List.of();
        }

        return findAll()
                .stream()
                .filter(movement ->
                        inventoryId.equals(
                                movement
                                        .getInventory()
                                        .getInventoryId()
                        )
                )
                .sorted(
                        Comparator.comparing(
                                InventoryMovement::getOccurredAt
                        ).reversed()
                )
                .toList();
    }

    /**
     * Obtiene los movimientos realizados
     * por un usuario.
     *
     * @param identification identificación del usuario
     * @return movimientos ordenados desde el más reciente
     */
    default List<InventoryMovement>
    findByPerformedByIdentification(
            String identification
    ) {
        if (identification == null
                || identification.isBlank()) {
            return List.of();
        }

        return findAll()
                .stream()
                .filter(movement ->
                        identification.equals(
                                movement
                                        .getPerformedBy()
                                        .getIdentification()
                        )
                )
                .sorted(
                        Comparator.comparing(
                                InventoryMovement::getOccurredAt
                        ).reversed()
                )
                .toList();
    }
}