package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.WarehouseJpaEntity;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseOwnerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas del convertidor entre Warehouse
 * y WarehouseJpaEntity.
 */
class WarehouseJpaMapperTest {

    /**
     * Comprueba la conversión de una bodega del dominio
     * en una entidad JPA.
     */
    @Test
    void shouldConvertDomainWarehouseToEntity() {
        Warehouse warehouse =
                new Warehouse(
                        WarehouseOwnerType.MARKETPLACE
                );

        WarehouseJpaEntity entity =
                WarehouseJpaMapper.toEntity(warehouse);

        assertEquals(
                WarehouseOwnerType.MARKETPLACE,
                entity.getOwnerType()
        );
    }

    /**
     * Comprueba la conversión de una entidad JPA
     * en una bodega del dominio.
     */
    @Test
    void shouldConvertEntityToDomainWarehouse() {
        WarehouseJpaEntity entity =
                new WarehouseJpaEntity(
                        WarehouseOwnerType.SELLER
                );

        Warehouse warehouse =
                WarehouseJpaMapper.toDomain(entity);

        assertEquals(
                WarehouseOwnerType.SELLER,
                warehouse.getOwnerType()
        );
    }

    /**
     * Comprueba que no pueda convertirse
     * una bodega nula en entidad JPA.
     */
    @Test
    void shouldRejectNullDomainWarehouse() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> WarehouseJpaMapper
                                .toEntity(null)
                );

        assertEquals(
                "Warehouse must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba que no pueda convertirse
     * una entidad JPA nula en objeto del dominio.
     */
    @Test
    void shouldRejectNullWarehouseEntity() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> WarehouseJpaMapper
                                .toDomain(null)
                );

        assertEquals(
                "Warehouse entity must not be null.",
                exception.getMessage()
        );
    }
}