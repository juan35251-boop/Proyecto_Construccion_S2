package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.InventoryJpaEntity;
import application.adapters.output.persistence.jpa.repositories.SpringDataInventoryRepository;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.Warehouse;
import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.output.ProductQueryRepository;
import application.ports.output.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del adaptador JPA de inventarios.
 *
 * MySQL no es necesario porque los repositorios técnicos
 * son reemplazados por simulaciones.
 */
class JpaInventoryRepositoryAdapterTest {

    private SpringDataInventoryRepository
            springDataRepository;

    private ProductQueryRepository
            productQueryRepository;

    private WarehouseRepository
            warehouseRepository;

    private JpaInventoryRepositoryAdapter adapter;

    private Product product;
    private Warehouse warehouse;

    /**
     * Prepara las dependencias antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        springDataRepository =
                mock(SpringDataInventoryRepository.class);

        productQueryRepository =
                mock(ProductQueryRepository.class);

        warehouseRepository =
                mock(WarehouseRepository.class);

        adapter =
                new JpaInventoryRepositoryAdapter(
                        springDataRepository,
                        productQueryRepository,
                        warehouseRepository
                );

        product =
                new Product(
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                );

        warehouse =
                new Warehouse(
                        "warehouse-001",
                        WarehouseOwnerType.MARKETPLACE
                );
    }

    /**
     * Comprueba el almacenamiento de
     * un inventario nuevo.
     */
    @Test
    void shouldSaveNewInventory() {
        Inventory inventory =
                new Inventory(
                        "inventory-001",
                        product,
                        warehouse,
                        30,
                        InventoryCondition.AVAILABLE
                );

        when(
                springDataRepository.findByInventoryId(
                        "inventory-001"
                )
        ).thenReturn(Optional.empty());

        adapter.save(inventory);

        verify(springDataRepository)
                .save(
                        argThat(entity ->
                                entity != null
                                        && entity.getId() == null
                                        && "inventory-001".equals(
                                                entity.getInventoryId()
                                        )
                                        && product.getProductId().equals(
                                                entity.getProductId()
                                        )
                                        && "warehouse-001".equals(
                                                entity.getWarehouseId()
                                        )
                                        && entity.getAvailableQuantity()
                                        == 30
                        )
                );
    }

    /**
     * Comprueba que al actualizar un inventario
     * se conserve la clave técnica de MySQL.
     */
    @Test
    void shouldUpdateExistingInventory() {
        Inventory inventory =
                new Inventory(
                        "inventory-002",
                        product,
                        warehouse,
                        45,
                        InventoryCondition.AVAILABLE
                );

        InventoryJpaEntity existingEntity =
                new InventoryJpaEntity(
                        12L,
                        "inventory-002",
                        product.getProductId(),
                        warehouse.getWarehouseId(),
                        20,
                        InventoryCondition.AVAILABLE
                );

        when(
                springDataRepository.findByInventoryId(
                        "inventory-002"
                )
        ).thenReturn(
                Optional.of(existingEntity)
        );

        adapter.save(inventory);

        verify(springDataRepository)
                .save(
                        argThat(entity ->
                                Long.valueOf(12L)
                                        .equals(entity.getId())
                                        && entity.getAvailableQuantity()
                                        == 45
                        )
                );
    }

    /**
     * Comprueba que no se pueda guardar
     * un inventario nulo.
     */
    @Test
    void shouldRejectNullInventoryWhenSaving() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> adapter.save(null)
                );

        assertEquals(
                "Inventory must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba la búsqueda de un inventario
     * mediante inventoryId.
     */
    @Test
    void shouldFindInventoryById() {
        InventoryJpaEntity entity =
                createEntity(
                        "inventory-003"
                );

        prepareRelatedObjects(entity);

        when(
                springDataRepository.findByInventoryId(
                        "inventory-003"
                )
        ).thenReturn(Optional.of(entity));

        Optional<Inventory> result =
                adapter.findById(
                        "inventory-003"
                );

        assertTrue(result.isPresent());

        assertEquals(
                "inventory-003",
                result.get().getInventoryId()
        );

        assertEquals(
                25,
                result.get().getAvailableQuantity()
        );
    }

    /**
     * Comprueba que una búsqueda sin resultados
     * devuelva un Optional vacío.
     */
    @Test
    void shouldReturnEmptyWhenInventoryDoesNotExist() {
        when(
                springDataRepository.findByInventoryId(
                        "inventory-unknown"
                )
        ).thenReturn(Optional.empty());

        Optional<Inventory> result =
                adapter.findById(
                        "inventory-unknown"
                );

        assertTrue(result.isEmpty());
    }

    /**
     * Comprueba la consulta de todos
     * los inventarios.
     */
    @Test
    void shouldFindAllInventories() {
        InventoryJpaEntity firstEntity =
                createEntity(
                        "inventory-004"
                );

        InventoryJpaEntity secondEntity =
                new InventoryJpaEntity(
                        2L,
                        "inventory-005",
                        product.getProductId(),
                        warehouse.getWarehouseId(),
                        10,
                        InventoryCondition.DAMAGED
                );

        prepareRelatedObjects(firstEntity);
        prepareRelatedObjects(secondEntity);

        when(
                springDataRepository.findAll()
        ).thenReturn(
                List.of(
                        firstEntity,
                        secondEntity
                )
        );

        List<Inventory> result =
                adapter.findAll();

        assertEquals(2, result.size());

        assertEquals(
                "inventory-004",
                result.get(0).getInventoryId()
        );

        assertEquals(
                InventoryCondition.DAMAGED,
                result.get(1).getCondition()
        );
    }

    /**
     * Comprueba la consulta por producto.
     */
    @Test
    void shouldFindInventoriesByProductId() {
        InventoryJpaEntity entity =
                createEntity(
                        "inventory-006"
                );

        prepareRelatedObjects(entity);

        when(
                springDataRepository.findAllByProductId(
                        product.getProductId()
                )
        ).thenReturn(List.of(entity));

        List<Inventory> result =
                adapter.findByProductId(
                        product.getProductId()
                );

        assertEquals(1, result.size());

        verify(springDataRepository)
                .findAllByProductId(
                        product.getProductId()
                );
    }

    /**
     * Comprueba la consulta por bodega.
     */
    @Test
    void shouldFindInventoriesByWarehouseId() {
        InventoryJpaEntity entity =
                createEntity(
                        "inventory-007"
                );

        prepareRelatedObjects(entity);

        when(
                springDataRepository.findAllByWarehouseId(
                        warehouse.getWarehouseId()
                )
        ).thenReturn(List.of(entity));

        List<Inventory> result =
                adapter.findByWarehouseId(
                        warehouse.getWarehouseId()
                );

        assertEquals(1, result.size());

        verify(springDataRepository)
                .findAllByWarehouseId(
                        warehouse.getWarehouseId()
                );
    }

    /**
     * Comprueba que se detecten referencias
     * a productos inexistentes.
     */
    @Test
    void shouldRejectInventoryWithMissingProduct() {
        InventoryJpaEntity entity =
                createEntity(
                        "inventory-008"
                );

        when(
                springDataRepository.findByInventoryId(
                        "inventory-008"
                )
        ).thenReturn(Optional.of(entity));

        when(
                productQueryRepository.findById(
                        entity.getProductId()
                )
        ).thenReturn(Optional.empty());

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                adapter.findById(
                                        "inventory-008"
                                )
                );

        assertEquals(
                "Inventory references a product that does not exist.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba que se detecten referencias
     * a bodegas inexistentes.
     */
    @Test
    void shouldRejectInventoryWithMissingWarehouse() {
        InventoryJpaEntity entity =
                createEntity(
                        "inventory-009"
                );

        when(
                springDataRepository.findByInventoryId(
                        "inventory-009"
                )
        ).thenReturn(Optional.of(entity));

        when(
                productQueryRepository.findById(
                        entity.getProductId()
                )
        ).thenReturn(Optional.of(product));

        when(
                warehouseRepository.findById(
                        entity.getWarehouseId()
                )
        ).thenReturn(Optional.empty());

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                adapter.findById(
                                        "inventory-009"
                                )
                );

        assertEquals(
                "Inventory references a warehouse that does not exist.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba la validación del repositorio JPA.
     */
    @Test
    void shouldRejectNullJpaRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new JpaInventoryRepositoryAdapter(
                                        null,
                                        productQueryRepository,
                                        warehouseRepository
                                )
                );

        assertEquals(
                "Inventory JPA repository must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba la validación del repositorio
     * de productos.
     */
    @Test
    void shouldRejectNullProductRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new JpaInventoryRepositoryAdapter(
                                        springDataRepository,
                                        null,
                                        warehouseRepository
                                )
                );

        assertEquals(
                "Product query repository must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba la validación del repositorio
     * de bodegas.
     */
    @Test
    void shouldRejectNullWarehouseRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new JpaInventoryRepositoryAdapter(
                                        springDataRepository,
                                        productQueryRepository,
                                        null
                                )
                );

        assertEquals(
                "Warehouse repository must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Crea una entidad reutilizable para las pruebas.
     *
     * @param inventoryId identificador del inventario
     * @return entidad de prueba
     */
    private InventoryJpaEntity createEntity(
            String inventoryId
    ) {
        return new InventoryJpaEntity(
                1L,
                inventoryId,
                product.getProductId(),
                warehouse.getWarehouseId(),
                25,
                InventoryCondition.AVAILABLE
        );
    }

    /**
     * Prepara las respuestas de los repositorios
     * relacionados con una entidad.
     *
     * @param entity entidad que será reconstruida
     */
    private void prepareRelatedObjects(
            InventoryJpaEntity entity
    ) {
        when(
                productQueryRepository.findById(
                        entity.getProductId()
                )
        ).thenReturn(Optional.of(product));

        when(
                warehouseRepository.findById(
                        entity.getWarehouseId()
                )
        ).thenReturn(Optional.of(warehouse));
    }
}