package application.adapters.output.persistence.jpa.repositories;

import application.adapters.output.persistence.jpa.entities.InventoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio técnico de Spring Data utilizado
 * para acceder a los inventarios almacenados en MySQL.
 */
public interface SpringDataInventoryRepository
        extends JpaRepository<InventoryJpaEntity, Long> {

    /**
     * Busca un inventario mediante su identificador permanente.
     *
     * @param inventoryId identificador del inventario
     * @return entidad encontrada, si existe
     */
    Optional<InventoryJpaEntity> findByInventoryId(
            String inventoryId
    );

    /**
     * Comprueba si existe un inventario.
     *
     * @param inventoryId identificador del inventario
     * @return true si existe
     */
    boolean existsByInventoryId(
            String inventoryId
    );

    /**
     * Comprueba si un producto ya tiene inventario
     * en una bodega determinada.
     *
     * @param productId identificador del producto
     * @param warehouseId identificador de la bodega
     * @return true si la relación ya existe
     */
    boolean existsByProductIdAndWarehouseId(
            String productId,
            String warehouseId
    );

    /**
     * Obtiene los inventarios relacionados con un producto.
     *
     * @param productId identificador del producto
     * @return entidades encontradas
     */
    List<InventoryJpaEntity> findAllByProductId(
            String productId
    );

    /**
     * Obtiene los inventarios almacenados en una bodega.
     *
     * @param warehouseId identificador de la bodega
     * @return entidades encontradas
     */
    List<InventoryJpaEntity> findAllByWarehouseId(
            String warehouseId
    );
}