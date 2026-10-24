package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.WarehouseJpaEntity;
import application.domain.models.Warehouse;

/**
 * Convierte bodegas entre el modelo de dominio
 * y la representación utilizada por JPA.
 *
 * Este mapeador evita que el dominio dependa
 * de anotaciones o clases de persistencia.
 */
public final class WarehouseJpaMapper {

    /**
     * Evita la creación de instancias del mapeador.
     */
    private WarehouseJpaMapper() {
    }

    /**
     * Convierte una bodega del dominio
     * en una entidad JPA.
     *
     * @param warehouse bodega del dominio
     * @return entidad preparada para MySQL
     */
    public static WarehouseJpaEntity toEntity(
            Warehouse warehouse
    ) {
        validateWarehouse(warehouse);

        return new WarehouseJpaEntity(
                warehouse.getWarehouseId(),
                warehouse.getOwnerType()
        );
    }

    /**
     * Convierte una entidad JPA en una bodega
     * perteneciente al dominio.
     *
     * Se conserva warehouseId para mantener
     * la misma identidad después de consultar MySQL.
     *
     * @param entity entidad almacenada
     * @return bodega del dominio
     */
    public static Warehouse toDomain(
            WarehouseJpaEntity entity
    ) {
        validateEntity(entity);

        return new Warehouse(
                entity.getWarehouseId(),
                entity.getOwnerType()
        );
    }

    /**
     * Valida que exista la bodega del dominio.
     *
     * @param warehouse bodega que será validada
     */
    private static void validateWarehouse(
            Warehouse warehouse
    ) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Warehouse must not be null."
            );
        }
    }

    /**
     * Valida que exista la entidad JPA.
     *
     * @param entity entidad que será validada
     */
    private static void validateEntity(
            WarehouseJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Warehouse entity must not be null."
            );
        }
    }
}