package application.adapters.output.persistence.mongodb.adapters;

import application.adapters.output.persistence.mongodb.documents.InventoryMovementMongoDocument;
import application.adapters.output.persistence.mongodb.repositories.SpringDataInventoryMovementMongoRepository;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;
import application.ports.output.InventoryQueryRepository;
import application.ports.output.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas del adaptador MongoDB encargado
 * de almacenar movimientos de inventario.
 *
 * Las pruebas utilizan simulaciones y no necesitan
 * una instancia real de MongoDB.
 */
class MongoInventoryMovementRepositoryAdapterTest {

    private SpringDataInventoryMovementMongoRepository
            mongoRepository;

    private InventoryQueryRepository
            inventoryQueryRepository;

    private UserRepository userRepository;

    private MongoInventoryMovementRepositoryAdapter adapter;

    private Inventory inventory;
    private User performedBy;

    /**
     * Prepara las dependencias antes
     * de cada prueba.
     */
    @BeforeEach
    void setUp() {
        mongoRepository =
                mock(
                        SpringDataInventoryMovementMongoRepository.class
                );

        inventoryQueryRepository =
                mock(InventoryQueryRepository.class);

        userRepository =
                mock(UserRepository.class);

        adapter =
                new MongoInventoryMovementRepositoryAdapter(
                        mongoRepository,
                        inventoryQueryRepository,
                        userRepository
                );

        inventory = mock(Inventory.class);
        performedBy = mock(User.class);
    }

    /**
     * Comprueba que un movimiento sea convertido
     * correctamente en un documento MongoDB.
     */
    @Test
    void shouldSaveInventoryMovement() {
        Product product = mock(Product.class);
        Warehouse warehouse = mock(Warehouse.class);
        InventoryMovement movement =
                mock(InventoryMovement.class);

        Instant occurredAt =
                Instant.parse(
                        "2026-10-20T20:00:00Z"
                );

        when(
                inventory.getInventoryId()
        ).thenReturn("inventory-001");

        when(
                inventory.getProduct()
        ).thenReturn(product);

        when(
                inventory.getWarehouse()
        ).thenReturn(warehouse);

        when(
                product.getProductId()
        ).thenReturn("product-001");

        when(
                warehouse.getWarehouseId()
        ).thenReturn("warehouse-001");

        when(
                performedBy.getIdentification()
        ).thenReturn("seller-001");

        when(
                performedBy.getRole()
        ).thenReturn(SystemRole.SELLER);

        when(
                movement.getMovementId()
        ).thenReturn("movement-001");

        when(
                movement.getInventory()
        ).thenReturn(inventory);

        when(
                movement.getMovementType()
        ).thenReturn(
                InventoryMovementType.ENTRY
        );

        when(
                movement.getQuantity()
        ).thenReturn(15);

        when(
                movement.getPerformedBy()
        ).thenReturn(performedBy);

        when(
                movement.getOccurredAt()
        ).thenReturn(occurredAt);

        adapter.save(movement);

        verify(mongoRepository)
                .save(
                        argThat(document ->
                                document != null
                                        && "movement-001".equals(
                                                document.getMovementId()
                                        )
                                        && "inventory-001".equals(
                                                document.getInventoryId()
                                        )
                                        && "product-001".equals(
                                                document.getProductId()
                                        )
                                        && "warehouse-001".equals(
                                                document.getWarehouseId()
                                        )
                                        && document.getMovementType()
                                        == InventoryMovementType.ENTRY
                                        && document.getQuantity() == 15
                                        && "seller-001".equals(
                                                document
                                                        .getPerformedByIdentification()
                                        )
                                        && document.getPerformedByRole()
                                        == SystemRole.SELLER
                                        && occurredAt.equals(
                                                document.getOccurredAt()
                                        )
                        )
                );
    }

    /**
     * Comprueba que el adaptador rechace
     * movimientos nulos.
     */
    @Test
    void shouldRejectNullMovement() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> adapter.save(null)
                );

        assertEquals(
                "Inventory movement must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba la reconstrucción de todos
     * los movimientos almacenados.
     */
    @Test
    void shouldFindAllMovements() {
        InventoryMovementMongoDocument older =
                createDocument(
                        "movement-older",
                        Instant.parse(
                                "2026-10-20T18:00:00Z"
                        )
                );

        InventoryMovementMongoDocument newer =
                createDocument(
                        "movement-newer",
                        Instant.parse(
                                "2026-10-20T20:00:00Z"
                        )
                );

        prepareRelatedObjects();

        when(
                mongoRepository.findAll()
        ).thenReturn(
                List.of(older, newer)
        );

        List<InventoryMovement> result =
                adapter.findAll();

        assertEquals(2, result.size());

        assertEquals(
                "movement-newer",
                result.get(0).getMovementId()
        );

        assertEquals(
                "movement-older",
                result.get(1).getMovementId()
        );
    }

    /**
     * Comprueba la consulta por inventario.
     */
    @Test
    void shouldFindMovementsByInventoryId() {
        InventoryMovementMongoDocument document =
                createDocument(
                        "movement-002",
                        Instant.parse(
                                "2026-10-20T21:00:00Z"
                        )
                );

        prepareRelatedObjects();

        when(
                mongoRepository
                        .findAllByInventoryIdOrderByOccurredAtDesc(
                                "inventory-001"
                        )
        ).thenReturn(List.of(document));

        List<InventoryMovement> result =
                adapter.findByInventoryId(
                        "inventory-001"
                );

        assertEquals(1, result.size());

        assertEquals(
                "movement-002",
                result.get(0).getMovementId()
        );

        assertSame(
                inventory,
                result.get(0).getInventory()
        );
    }

    /**
     * Comprueba la consulta por usuario responsable.
     */
    @Test
    void shouldFindMovementsByUserIdentification() {
        InventoryMovementMongoDocument document =
                createDocument(
                        "movement-003",
                        Instant.parse(
                                "2026-10-20T22:00:00Z"
                        )
                );

        prepareRelatedObjects();

        when(
                mongoRepository
                        .findAllByPerformedByIdentificationOrderByOccurredAtDesc(
                                "seller-001"
                        )
        ).thenReturn(List.of(document));

        List<InventoryMovement> result =
                adapter.findByPerformedByIdentification(
                        "seller-001"
                );

        assertEquals(1, result.size());

        assertSame(
                performedBy,
                result.get(0).getPerformedBy()
        );
    }

    /**
     * Comprueba que se detecte un movimiento
     * relacionado con un inventario inexistente.
     */
    @Test
    void shouldRejectMovementWithMissingInventory() {
        InventoryMovementMongoDocument document =
                createDocument(
                        "movement-004",
                        Instant.now()
                );

        when(
                mongoRepository.findAll()
        ).thenReturn(List.of(document));

        when(
                inventoryQueryRepository.findById(
                        "inventory-001"
                )
        ).thenReturn(Optional.empty());

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> adapter.findAll()
                );

        assertEquals(
                "Inventory movement references an inventory that does not exist.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba que se detecte un movimiento
     * relacionado con un usuario inexistente.
     */
    @Test
    void shouldRejectMovementWithMissingUser() {
        InventoryMovementMongoDocument document =
                createDocument(
                        "movement-005",
                        Instant.now()
                );

        when(
                mongoRepository.findAll()
        ).thenReturn(List.of(document));

        when(
                inventoryQueryRepository.findById(
                        "inventory-001"
                )
        ).thenReturn(Optional.of(inventory));

        when(
                userRepository.findByIdentification(
                        "seller-001"
                )
        ).thenReturn(Optional.empty());

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> adapter.findAll()
                );

        assertEquals(
                "Inventory movement references a user that does not exist.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba la validación del repositorio MongoDB.
     */
    @Test
    void shouldRejectNullMongoRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new MongoInventoryMovementRepositoryAdapter(
                                        null,
                                        inventoryQueryRepository,
                                        userRepository
                                )
                );

        assertEquals(
                "Inventory movement MongoDB repository must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba la validación del repositorio
     * de inventarios.
     */
    @Test
    void shouldRejectNullInventoryQueryRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new MongoInventoryMovementRepositoryAdapter(
                                        mongoRepository,
                                        null,
                                        userRepository
                                )
                );

        assertEquals(
                "Inventory query repository must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba la validación del repositorio
     * de usuarios.
     */
    @Test
    void shouldRejectNullUserRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new MongoInventoryMovementRepositoryAdapter(
                                        mongoRepository,
                                        inventoryQueryRepository,
                                        null
                                )
                );

        assertEquals(
                "User repository must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba que se rechace un identificador
     * de inventario vacío.
     */
    @Test
    void shouldRejectBlankInventoryId() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                adapter.findByInventoryId(" ")
                );

        assertEquals(
                "Inventory identifier must not be empty.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba que se rechace una identificación
     * de usuario vacía.
     */
    @Test
    void shouldRejectBlankUserIdentification() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                adapter
                                        .findByPerformedByIdentification(
                                                " "
                                        )
                );

        assertEquals(
                "User identification must not be empty.",
                exception.getMessage()
        );
    }

    /**
     * Crea un documento reutilizable.
     *
     * @param movementId identificador del movimiento
     * @param occurredAt momento del movimiento
     * @return documento de prueba
     */
    private InventoryMovementMongoDocument createDocument(
            String movementId,
            Instant occurredAt
    ) {
        return new InventoryMovementMongoDocument(
                movementId,
                "inventory-001",
                "product-001",
                "warehouse-001",
                InventoryMovementType.ENTRY,
                10,
                "seller-001",
                SystemRole.SELLER,
                occurredAt
        );
    }

    /**
     * Prepara el inventario y el usuario requeridos
     * para reconstruir un movimiento.
     */
    private void prepareRelatedObjects() {
        when(
                inventoryQueryRepository.findById(
                        "inventory-001"
                )
        ).thenReturn(Optional.of(inventory));

        when(
                userRepository.findByIdentification(
                        "seller-001"
                )
        ).thenReturn(Optional.of(performedBy));
    }
}