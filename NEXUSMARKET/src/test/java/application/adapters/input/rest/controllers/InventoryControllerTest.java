package application.adapters.input.rest.controllers;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;
import application.ports.input.InventoryManagementUseCase;
import application.ports.input.InventoryMovementQueryUseCase;
import application.ports.input.InventoryQueryUseCase;
import application.ports.input.RegisterInventoryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias del controlador REST de inventarios.
 *
 * Los casos de uso son simulados para comprobar solamente
 * el comportamiento HTTP del controlador.
 *
 * Cada prueba configura únicamente los datos que utiliza.
 * Esto evita configuraciones innecesarias detectadas por Mockito.
 */
@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    private static final String REQUESTER_IDENTIFICATION =
            "SELLER-1001";

    private static final String INVENTORY_ID =
            "inventory-001";

    private static final String PRODUCT_ID =
            "product-001";

    private static final String WAREHOUSE_ID =
            "warehouse-001";

    private static final String MOVEMENT_ID =
            "movement-001";

    private static final Instant MOVEMENT_DATE =
            Instant.parse(
                    "2026-10-22T15:00:00Z"
            );

    @Mock
    private RegisterInventoryUseCase
            registerInventoryUseCase;

    @Mock
    private InventoryManagementUseCase
            inventoryManagementUseCase;

    @Mock
    private InventoryQueryUseCase
            inventoryQueryUseCase;

    @Mock
    private InventoryMovementQueryUseCase
            inventoryMovementQueryUseCase;

    @Mock
    private Inventory inventory;

    @Mock
    private InventoryMovement movement;

    @Mock
    private Product product;

    @Mock
    private Warehouse warehouse;

    @Mock
    private User performedBy;

    private MockMvc mockMvc;

    /**
     * Construye el controlador antes de cada prueba.
     *
     * En este método no se configuran respuestas de Mockito,
     * porque no todas las pruebas utilizan los mismos objetos.
     */
    @BeforeEach
    void setUp() {
        InventoryController controller =
                new InventoryController(
                        registerInventoryUseCase,
                        inventoryManagementUseCase,
                        inventoryQueryUseCase,
                        inventoryMovementQueryUseCase
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    /**
     * Comprueba el registro de un inventario.
     */
    @Test
    void shouldRegisterInventory() throws Exception {
        configureInventoryResponse();

        when(registerInventoryUseCase.register(
                REQUESTER_IDENTIFICATION,
                PRODUCT_ID,
                WAREHOUSE_ID,
                20,
                InventoryCondition.AVAILABLE
        )).thenReturn(inventory);

        mockMvc.perform(
                        post("/api/v1/inventories")
                                .header(
                                        "X-Requester-Identification",
                                        REQUESTER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "productId": "product-001",
                                          "warehouseId": "warehouse-001",
                                          "initialQuantity": 20,
                                          "condition": "AVAILABLE"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.inventoryId")
                                .value(INVENTORY_ID)
                )
                .andExpect(
                        jsonPath("$.productId")
                                .value(PRODUCT_ID)
                )
                .andExpect(
                        jsonPath("$.warehouseId")
                                .value(WAREHOUSE_ID)
                )
                .andExpect(
                        jsonPath("$.availableQuantity")
                                .value(20)
                )
                .andExpect(
                        jsonPath("$.condition")
                                .value("AVAILABLE")
                );

        verify(registerInventoryUseCase)
                .register(
                        REQUESTER_IDENTIFICATION,
                        PRODUCT_ID,
                        WAREHOUSE_ID,
                        20,
                        InventoryCondition.AVAILABLE
                );
    }

    /**
     * Comprueba la consulta de inventarios accesibles.
     */
    @Test
    void shouldFindAccessibleInventory()
            throws Exception {
        configureInventoryResponse();

        when(inventoryQueryUseCase
                .findAccessibleInventory(
                        REQUESTER_IDENTIFICATION
                ))
                .thenReturn(
                        List.of(inventory)
                );

        mockMvc.perform(
                        get("/api/v1/inventories")
                                .header(
                                        "X-Requester-Identification",
                                        REQUESTER_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].inventoryId")
                                .value(INVENTORY_ID)
                )
                .andExpect(
                        jsonPath("$[0].productId")
                                .value(PRODUCT_ID)
                )
                .andExpect(
                        jsonPath("$[0].warehouseId")
                                .value(WAREHOUSE_ID)
                )
                .andExpect(
                        jsonPath("$[0].availableQuantity")
                                .value(20)
                )
                .andExpect(
                        jsonPath("$[0].condition")
                                .value("AVAILABLE")
                );

        verify(inventoryQueryUseCase)
                .findAccessibleInventory(
                        REQUESTER_IDENTIFICATION
                );
    }

    /**
     * Comprueba el registro de entrada de unidades.
     */
    @Test
    void shouldRegisterInventoryEntry()
            throws Exception {
        configureMovementResponse(
                InventoryMovementType.ENTRY,
                5
        );

        when(inventoryManagementUseCase.registerEntry(
                REQUESTER_IDENTIFICATION,
                INVENTORY_ID,
                5
        )).thenReturn(movement);

        mockMvc.perform(
                        post(
                                "/api/v1/inventories/{inventoryId}/entries",
                                INVENTORY_ID
                        )
                                .header(
                                        "X-Requester-Identification",
                                        REQUESTER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "quantity": 5
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.movementId")
                                .value(MOVEMENT_ID)
                )
                .andExpect(
                        jsonPath("$.inventoryId")
                                .value(INVENTORY_ID)
                )
                .andExpect(
                        jsonPath("$.movementType")
                                .value("ENTRY")
                )
                .andExpect(
                        jsonPath("$.quantity")
                                .value(5)
                );

        verify(inventoryManagementUseCase)
                .registerEntry(
                        REQUESTER_IDENTIFICATION,
                        INVENTORY_ID,
                        5
                );
    }

    /**
     * Comprueba la reserva de unidades.
     */
    @Test
    void shouldReserveInventoryQuantity()
            throws Exception {
        configureMovementResponse(
                InventoryMovementType.RESERVATION,
                3
        );

        when(inventoryManagementUseCase.reserveQuantity(
                REQUESTER_IDENTIFICATION,
                INVENTORY_ID,
                3
        )).thenReturn(movement);

        mockMvc.perform(
                        post(
                                "/api/v1/inventories/{inventoryId}/reservations",
                                INVENTORY_ID
                        )
                                .header(
                                        "X-Requester-Identification",
                                        REQUESTER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "quantity": 3
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.movementType")
                                .value("RESERVATION")
                )
                .andExpect(
                        jsonPath("$.quantity")
                                .value(3)
                );

        verify(inventoryManagementUseCase)
                .reserveQuantity(
                        REQUESTER_IDENTIFICATION,
                        INVENTORY_ID,
                        3
                );
    }

    /**
     * Comprueba el ajuste de la cantidad disponible.
     */
    @Test
    void shouldAdjustInventoryQuantity()
            throws Exception {
        configureMovementResponse(
                InventoryMovementType.ADJUSTMENT,
                5
        );

        when(inventoryManagementUseCase.adjustQuantity(
                REQUESTER_IDENTIFICATION,
                INVENTORY_ID,
                15
        )).thenReturn(movement);

        mockMvc.perform(
                        patch(
                                "/api/v1/inventories/{inventoryId}/quantity",
                                INVENTORY_ID
                        )
                                .header(
                                        "X-Requester-Identification",
                                        REQUESTER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "newQuantity": 15
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.movementType")
                                .value("ADJUSTMENT")
                )
                .andExpect(
                        jsonPath("$.quantity")
                                .value(5)
                );

        verify(inventoryManagementUseCase)
                .adjustQuantity(
                        REQUESTER_IDENTIFICATION,
                        INVENTORY_ID,
                        15
                );
    }

    /**
     * Comprueba el registro de una devolución.
     */
    @Test
    void shouldRegisterInventoryReturn()
            throws Exception {
        configureMovementResponse(
                InventoryMovementType.RETURN,
                2
        );

        when(inventoryManagementUseCase.registerReturn(
                REQUESTER_IDENTIFICATION,
                INVENTORY_ID,
                2
        )).thenReturn(movement);

        mockMvc.perform(
                        post(
                                "/api/v1/inventories/{inventoryId}/returns",
                                INVENTORY_ID
                        )
                                .header(
                                        "X-Requester-Identification",
                                        REQUESTER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "quantity": 2
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.movementType")
                                .value("RETURN")
                )
                .andExpect(
                        jsonPath("$.quantity")
                                .value(2)
                );

        verify(inventoryManagementUseCase)
                .registerReturn(
                        REQUESTER_IDENTIFICATION,
                        INVENTORY_ID,
                        2
                );
    }

    /**
     * Comprueba el cambio de condición del inventario.
     */
    @Test
    void shouldChangeInventoryCondition()
            throws Exception {
        mockMvc.perform(
                        patch(
                                "/api/v1/inventories/{inventoryId}/condition",
                                INVENTORY_ID
                        )
                                .header(
                                        "X-Requester-Identification",
                                        REQUESTER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "condition": "DAMAGED"
                                        }
                                        """)
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(inventoryManagementUseCase)
                .changeCondition(
                        REQUESTER_IDENTIFICATION,
                        INVENTORY_ID,
                        InventoryCondition.DAMAGED
                );
    }

    /**
     * Comprueba la consulta del historial
     * de movimientos accesibles.
     */
    @Test
    void shouldFindAccessibleMovements()
            throws Exception {
        configureMovementResponse(
                InventoryMovementType.ENTRY,
                5
        );

        when(inventoryMovementQueryUseCase
                .findAccessibleMovements(
                        REQUESTER_IDENTIFICATION
                ))
                .thenReturn(
                        List.of(movement)
                );

        mockMvc.perform(
                        get("/api/v1/inventories/movements")
                                .header(
                                        "X-Requester-Identification",
                                        REQUESTER_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].movementId")
                                .value(MOVEMENT_ID)
                )
                .andExpect(
                        jsonPath("$[0].inventoryId")
                                .value(INVENTORY_ID)
                )
                .andExpect(
                        jsonPath("$[0].productId")
                                .value(PRODUCT_ID)
                )
                .andExpect(
                        jsonPath("$[0].warehouseId")
                                .value(WAREHOUSE_ID)
                )
                .andExpect(
                        jsonPath("$[0].performedByIdentification")
                                .value(REQUESTER_IDENTIFICATION)
                )
                .andExpect(
                        jsonPath("$[0].performedByRole")
                                .value("SELLER")
                );

        verify(inventoryMovementQueryUseCase)
                .findAccessibleMovements(
                        REQUESTER_IDENTIFICATION
                );
    }

    /**
     * Comprueba que una cantidad igual a cero
     * sea rechazada por la validación de la API.
     */
    @Test
    void shouldRejectNonPositiveEntryQuantity()
            throws Exception {
        mockMvc.perform(
                        post(
                                "/api/v1/inventories/{inventoryId}/entries",
                                INVENTORY_ID
                        )
                                .header(
                                        "X-Requester-Identification",
                                        REQUESTER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "quantity": 0
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    /**
     * Comprueba que una cantidad inicial negativa
     * sea rechazada.
     */
    @Test
    void shouldRejectNegativeInitialQuantity()
            throws Exception {
        mockMvc.perform(
                        post("/api/v1/inventories")
                                .header(
                                        "X-Requester-Identification",
                                        REQUESTER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "productId": "product-001",
                                          "warehouseId": "warehouse-001",
                                          "initialQuantity": -1,
                                          "condition": "AVAILABLE"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    /**
     * Comprueba la validación del caso de uso
     * para registrar inventario.
     */
    @Test
    void shouldRejectNullRegisterInventoryUseCase() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryController(
                        null,
                        inventoryManagementUseCase,
                        inventoryQueryUseCase,
                        inventoryMovementQueryUseCase
                )
        );
    }

    /**
     * Comprueba la validación del caso de uso
     * para administrar inventario.
     */
    @Test
    void shouldRejectNullInventoryManagementUseCase() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryController(
                        registerInventoryUseCase,
                        null,
                        inventoryQueryUseCase,
                        inventoryMovementQueryUseCase
                )
        );
    }

    /**
     * Comprueba la validación del caso de uso
     * para consultar inventarios.
     */
    @Test
    void shouldRejectNullInventoryQueryUseCase() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryController(
                        registerInventoryUseCase,
                        inventoryManagementUseCase,
                        null,
                        inventoryMovementQueryUseCase
                )
        );
    }

    /**
     * Comprueba la validación del caso de uso
     * para consultar movimientos.
     */
    @Test
    void shouldRejectNullMovementQueryUseCase() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryController(
                        registerInventoryUseCase,
                        inventoryManagementUseCase,
                        inventoryQueryUseCase,
                        null
                )
        );
    }

    /**
     * Configura los datos necesarios para identificar
     * el inventario, producto y bodega.
     *
     * Esta configuración es utilizada por las respuestas
     * de movimientos.
     */
    private void configureInventoryIdentity() {
        when(inventory.getInventoryId())
                .thenReturn(INVENTORY_ID);

        when(inventory.getProduct())
                .thenReturn(product);

        when(inventory.getWarehouse())
                .thenReturn(warehouse);

        when(product.getProductId())
                .thenReturn(PRODUCT_ID);

        when(warehouse.getWarehouseId())
                .thenReturn(WAREHOUSE_ID);
    }

    /**
     * Configura todos los datos necesarios para generar
     * una respuesta completa de inventario.
     */
    private void configureInventoryResponse() {
        configureInventoryIdentity();

        when(inventory.getAvailableQuantity())
                .thenReturn(20);

        when(inventory.getCondition())
                .thenReturn(
                        InventoryCondition.AVAILABLE
                );
    }

    /**
     * Configura los datos necesarios para generar
     * una respuesta completa de movimiento.
     *
     * @param movementType tipo del movimiento
     * @param quantity cantidad relacionada
     */
    private void configureMovementResponse(
            InventoryMovementType movementType,
            int quantity
    ) {
        configureInventoryIdentity();

        when(movement.getMovementId())
                .thenReturn(MOVEMENT_ID);

        when(movement.getInventory())
                .thenReturn(inventory);

        when(movement.getMovementType())
                .thenReturn(movementType);

        when(movement.getQuantity())
                .thenReturn(quantity);

        when(movement.getPerformedBy())
                .thenReturn(performedBy);

        when(movement.getOccurredAt())
                .thenReturn(MOVEMENT_DATE);

        when(performedBy.getIdentification())
                .thenReturn(
                        REQUESTER_IDENTIFICATION
                );

        when(performedBy.getRole())
                .thenReturn(SystemRole.SELLER);
    }
}