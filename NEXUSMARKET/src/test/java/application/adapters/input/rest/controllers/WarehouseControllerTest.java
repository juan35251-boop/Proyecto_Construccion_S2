package application.adapters.input.rest.controllers;

import application.adapters.input.rest.exception.GlobalExceptionHandler;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.input.RegisterWarehouseUseCase;
import application.ports.input.WarehouseQueryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Pruebas del controlador REST de bodegas.
 */
class WarehouseControllerTest {

    private RegisterWarehouseUseCase
            registerWarehouseUseCase;

    private WarehouseQueryUseCase
            warehouseQueryUseCase;

    private MockMvc mockMvc;

    /**
     * Prepara los casos de uso simulados y el controlador
     * antes de ejecutar cada prueba.
     */
    @BeforeEach
    void setUp() {
        registerWarehouseUseCase =
                mock(RegisterWarehouseUseCase.class);

        warehouseQueryUseCase =
                mock(WarehouseQueryUseCase.class);

        WarehouseController warehouseController =
                new WarehouseController(
                        registerWarehouseUseCase,
                        warehouseQueryUseCase
                );

        mockMvc = standaloneSetup(warehouseController)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }

    /**
     * Comprueba que un administrador pueda registrar
     * una bodega del Marketplace.
     */
    @Test
    void shouldRegisterMarketplaceWarehouse()
            throws Exception {

        Warehouse warehouse =
                new Warehouse(
                        WarehouseOwnerType.MARKETPLACE
                );

        when(registerWarehouseUseCase.register(
                "ADMIN-001",
                WarehouseOwnerType.MARKETPLACE
        )).thenReturn(warehouse);

        String requestBody = """
                {
                    "ownerType": "MARKETPLACE"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/warehouses")
                                .header(
                                        "X-Requester-Identification",
                                        "ADMIN-001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.ownerType")
                                .value("MARKETPLACE")
                );

        verify(registerWarehouseUseCase).register(
                "ADMIN-001",
                WarehouseOwnerType.MARKETPLACE
        );
    }

    /**
     * Comprueba que también pueda registrarse
     * una bodega perteneciente a vendedores.
     */
    @Test
    void shouldRegisterSellerWarehouse()
            throws Exception {

        Warehouse warehouse =
                new Warehouse(
                        WarehouseOwnerType.SELLER
                );

        when(registerWarehouseUseCase.register(
                "ADMIN-001",
                WarehouseOwnerType.SELLER
        )).thenReturn(warehouse);

        String requestBody = """
                {
                    "ownerType": "SELLER"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/warehouses")
                                .header(
                                        "X-Requester-Identification",
                                        "ADMIN-001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.ownerType")
                                .value("SELLER")
                );

        verify(registerWarehouseUseCase).register(
                "ADMIN-001",
                WarehouseOwnerType.SELLER
        );
    }

    /**
     * Comprueba que la API rechace una solicitud
     * sin el tipo de propietario obligatorio.
     */
    @Test
    void shouldRejectRequestWithoutOwnerType()
            throws Exception {

        String requestBody = """
                {
                    "ownerType": null
                }
                """;

        mockMvc.perform(
                        post("/api/v1/warehouses")
                                .header(
                                        "X-Requester-Identification",
                                        "ADMIN-001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Bad Request")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Request validation failed."
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/warehouses"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.ownerType"
                        ).exists()
                );
    }

    /**
     * Comprueba que se devuelvan las bodegas visibles
     * para el usuario solicitante.
     */
    @Test
    void shouldFindAccessibleWarehouses()
            throws Exception {

        Warehouse marketplaceWarehouse =
                new Warehouse(
                        WarehouseOwnerType.MARKETPLACE
                );

        Warehouse sellerWarehouse =
                new Warehouse(
                        WarehouseOwnerType.SELLER
                );

        when(
                warehouseQueryUseCase
                        .findAccessibleWarehouses(
                                "ADMIN-001"
                        )
        ).thenReturn(
                List.of(
                        marketplaceWarehouse,
                        sellerWarehouse
                )
        );

        mockMvc.perform(
                        get("/api/v1/warehouses")
                                .header(
                                        "X-Requester-Identification",
                                        "ADMIN-001"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()")
                        .value(2))
                .andExpect(jsonPath("$[0].ownerType")
                        .value("MARKETPLACE"))
                .andExpect(jsonPath("$[1].ownerType")
                        .value("SELLER"));

        verify(warehouseQueryUseCase)
                .findAccessibleWarehouses(
                        "ADMIN-001"
                );
    }

    /**
     * Comprueba que la API pueda devolver
     * una lista vacía de bodegas.
     */
    @Test
    void shouldReturnEmptyWarehouseList()
            throws Exception {

        when(
                warehouseQueryUseCase
                        .findAccessibleWarehouses(
                                "SUPERVISOR-001"
                        )
        ).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/v1/warehouses")
                                .header(
                                        "X-Requester-Identification",
                                        "SUPERVISOR-001"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(warehouseQueryUseCase)
                .findAccessibleWarehouses(
                        "SUPERVISOR-001"
                );
    }

    /**
     * Comprueba que una operación no autorizada
     * produzca una respuesta HTTP 409.
     */
    @Test
    void shouldReturnConflictWhenRequesterIsNotAuthorized()
            throws Exception {

        when(
                warehouseQueryUseCase
                        .findAccessibleWarehouses(
                                "BUYER-001"
                        )
        ).thenThrow(
                new IllegalStateException(
                        "User is not authorized to query warehouses."
                )
        );

        mockMvc.perform(
                        get("/api/v1/warehouses")
                                .header(
                                        "X-Requester-Identification",
                                        "BUYER-001"
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.error")
                        .value("Conflict"))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "User is not authorized to query warehouses."
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/warehouses"
                                )
                );
    }

    /**
     * Comprueba que la API rechace el registro cuando
     * el solicitante no es un administrador autorizado.
     */
    @Test
    void shouldReturnConflictWhenRequesterCannotRegister()
            throws Exception {

        when(registerWarehouseUseCase.register(
                "SELLER-001",
                WarehouseOwnerType.MARKETPLACE
        )).thenThrow(
                new IllegalStateException(
                        "Only an active administrator can register warehouses."
                )
        );

        String requestBody = """
                {
                    "ownerType": "MARKETPLACE"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/warehouses")
                                .header(
                                        "X-Requester-Identification",
                                        "SELLER-001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.error")
                        .value("Conflict"))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Only an active administrator can register warehouses."
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/warehouses"
                                )
                );
    }
}