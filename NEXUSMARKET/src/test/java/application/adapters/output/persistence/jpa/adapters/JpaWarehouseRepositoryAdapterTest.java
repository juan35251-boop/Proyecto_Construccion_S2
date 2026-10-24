package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.WarehouseJpaEntity;
import application.adapters.output.persistence.jpa.repositories.SpringDataWarehouseRepository;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseOwnerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas del adaptador JPA encargado
 * de persistir bodegas.
 */
class JpaWarehouseRepositoryAdapterTest {

    private SpringDataWarehouseRepository
            springDataRepository;

    private JpaWarehouseRepositoryAdapter adapter;

    /**
     * Prepara el repositorio simulado y el adaptador
     * antes de ejecutar cada prueba.
     */
    @BeforeEach
    void setUp() {
        springDataRepository =
                mock(SpringDataWarehouseRepository.class);

        adapter =
                new JpaWarehouseRepositoryAdapter(
                        springDataRepository
                );
    }

    /**
     * Comprueba que una bodega nueva sea convertida
     * y enviada al repositorio JPA.
     */
    @Test
    void shouldSaveNewWarehouse() {
        Warehouse warehouse =
                new Warehouse(
                        WarehouseOwnerType.MARKETPLACE
                );

        when(
                springDataRepository.findByWarehouseId(
                        warehouse.getWarehouseId()
                )
        ).thenReturn(Optional.empty());

        adapter.save(warehouse);

        verify(springDataRepository)
                .findByWarehouseId(
                        warehouse.getWarehouseId()
                );

        verify(springDataRepository)
                .save(
                        argThat(entity ->
                                entity != null
                                        && entity.getId() == null
                                        && warehouse
                                        .getWarehouseId()
                                        .equals(
                                                entity.getWarehouseId()
                                        )
                                        && entity.getOwnerType()
                                        == WarehouseOwnerType.MARKETPLACE
                        )
                );
    }

    /**
     * Comprueba que al guardar una bodega existente
     * se conserve su clave técnica de MySQL.
     */
    @Test
    void shouldUpdateExistingWarehouse() {
        Warehouse warehouse =
                new Warehouse(
                        "warehouse-001",
                        WarehouseOwnerType.SELLER
                );

        WarehouseJpaEntity existingEntity =
                new WarehouseJpaEntity(
                        15L,
                        "warehouse-001",
                        WarehouseOwnerType.SELLER
                );

        when(
                springDataRepository.findByWarehouseId(
                        warehouse.getWarehouseId()
                )
        ).thenReturn(
                Optional.of(existingEntity)
        );

        adapter.save(warehouse);

        verify(springDataRepository)
                .save(
                        argThat(entity ->
                                entity != null
                                        && Long.valueOf(15L)
                                        .equals(entity.getId())
                                        && "warehouse-001"
                                        .equals(
                                                entity.getWarehouseId()
                                        )
                        )
                );
    }

    /**
     * Comprueba que el adaptador rechace
     * una bodega nula al guardar.
     */
    @Test
    void shouldRejectNullWarehouseWhenSaving() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> adapter.save(null)
                );

        assertEquals(
                "Warehouse must not be null.",
                exception.getMessage()
        );

        verify(
                springDataRepository,
                never()
        ).save(
                org.mockito.ArgumentMatchers.any()
        );
    }

    /**
     * Comprueba que exists retorne true cuando
     * warehouseId está almacenado.
     */
    @Test
    void shouldReturnTrueWhenWarehouseExists() {
        Warehouse warehouse =
                new Warehouse(
                        "warehouse-002",
                        WarehouseOwnerType.MARKETPLACE
                );

        when(
                springDataRepository.existsByWarehouseId(
                        warehouse.getWarehouseId()
                )
        ).thenReturn(true);

        boolean result =
                adapter.exists(warehouse);

        assertTrue(result);

        verify(springDataRepository)
                .existsByWarehouseId(
                        "warehouse-002"
                );
    }

    /**
     * Comprueba que exists retorne false cuando
     * warehouseId no está almacenado.
     */
    @Test
    void shouldReturnFalseWhenWarehouseDoesNotExist() {
        Warehouse warehouse =
                new Warehouse(
                        "warehouse-003",
                        WarehouseOwnerType.MARKETPLACE
                );

        when(
                springDataRepository.existsByWarehouseId(
                        warehouse.getWarehouseId()
                )
        ).thenReturn(false);

        boolean result =
                adapter.exists(warehouse);

        assertFalse(result);

        verify(springDataRepository)
                .existsByWarehouseId(
                        "warehouse-003"
                );
    }

    /**
     * Comprueba que no sea posible verificar
     * la existencia de una bodega nula.
     */
    @Test
    void shouldRejectNullWarehouseWhenCheckingExistence() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> adapter.exists(null)
                );

        assertEquals(
                "Warehouse must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba la búsqueda de una bodega
     * mediante warehouseId.
     */
    @Test
    void shouldFindWarehouseById() {
        WarehouseJpaEntity entity =
                new WarehouseJpaEntity(
                        20L,
                        "warehouse-004",
                        WarehouseOwnerType.SELLER
                );

        when(
                springDataRepository.findByWarehouseId(
                        "warehouse-004"
                )
        ).thenReturn(
                Optional.of(entity)
        );

        Optional<Warehouse> result =
                adapter.findById(
                        "warehouse-004"
                );

        assertTrue(result.isPresent());
        assertEquals(
                "warehouse-004",
                result.get().getWarehouseId()
        );
        assertEquals(
                WarehouseOwnerType.SELLER,
                result.get().getOwnerType()
        );
    }

    /**
     * Comprueba que una búsqueda sin resultados
     * retorne un Optional vacío.
     */
    @Test
    void shouldReturnEmptyWhenWarehouseIdDoesNotExist() {
        when(
                springDataRepository.findByWarehouseId(
                        "warehouse-unknown"
                )
        ).thenReturn(Optional.empty());

        Optional<Warehouse> result =
                adapter.findById(
                        "warehouse-unknown"
                );

        assertTrue(result.isEmpty());
    }

    /**
     * Comprueba que se rechace un identificador vacío.
     */
    @Test
    void shouldRejectBlankWarehouseId() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> adapter.findById(" ")
                );

        assertEquals(
                "Warehouse identifier must not be empty.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba que todas las entidades almacenadas
     * sean convertidas a objetos del dominio.
     */
    @Test
    void shouldFindAllWarehouses() {
        WarehouseJpaEntity marketplaceWarehouse =
                new WarehouseJpaEntity(
                        1L,
                        "warehouse-marketplace",
                        WarehouseOwnerType.MARKETPLACE
                );

        WarehouseJpaEntity sellerWarehouse =
                new WarehouseJpaEntity(
                        2L,
                        "warehouse-seller",
                        WarehouseOwnerType.SELLER
                );

        when(
                springDataRepository.findAll()
        ).thenReturn(
                List.of(
                        marketplaceWarehouse,
                        sellerWarehouse
                )
        );

        List<Warehouse> result =
                adapter.findAll();

        assertEquals(2, result.size());

        assertEquals(
                "warehouse-marketplace",
                result.get(0).getWarehouseId()
        );

        assertEquals(
                "warehouse-seller",
                result.get(1).getWarehouseId()
        );
    }

    /**
     * Comprueba que una lista vacía sea manejada
     * correctamente.
     */
    @Test
    void shouldReturnEmptyWarehouseList() {
        when(
                springDataRepository.findAll()
        ).thenReturn(List.of());

        List<Warehouse> result =
                adapter.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Comprueba que el adaptador no pueda construirse
     * sin el repositorio JPA.
     */
    @Test
    void shouldRejectNullSpringDataRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new JpaWarehouseRepositoryAdapter(
                                        null
                                )
                );

        assertEquals(
                "Spring Data warehouse repository must not be null.",
                exception.getMessage()
        );
    }
}