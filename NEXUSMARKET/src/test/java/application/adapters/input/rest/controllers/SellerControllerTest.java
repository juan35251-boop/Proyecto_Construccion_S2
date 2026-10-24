package application.adapters.input.rest.controllers;

import application.adapters.input.rest.exception.GlobalExceptionHandler;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.input.RegisterSellerUseCase;
import application.ports.input.SellerQueryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Pruebas del controlador REST de vendedores.
 */
class SellerControllerTest {

    private RegisterSellerUseCase
            registerSellerUseCase;

    private SellerQueryUseCase
            sellerQueryUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        registerSellerUseCase =
                mock(RegisterSellerUseCase.class);

        sellerQueryUseCase =
                mock(SellerQueryUseCase.class);

        SellerController sellerController =
                new SellerController(
                        registerSellerUseCase,
                        sellerQueryUseCase
                );

        mockMvc = standaloneSetup(sellerController)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }

    @Test
    void shouldRejectNullRegisterSellerUseCase() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SellerController(
                        null,
                        sellerQueryUseCase
                )
        );
    }

    @Test
    void shouldRejectNullSellerQueryUseCase() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SellerController(
                        registerSellerUseCase,
                        null
                )
        );
    }

    @Test
    void shouldRegisterSeller() throws Exception {
        Seller seller = createSeller(
                "2001",
                "seller@email.com"
        );

        when(registerSellerUseCase.register(
                eq("9001"),
                eq("2001"),
                eq("Test Seller"),
                eq("seller@email.com"),
                eq(UserStatus.ACTIVE),
                any(Warehouse.class)
        )).thenReturn(seller);

        String requestBody = """
                {
                    "identification": "2001",
                    "fullName": "Test Seller",
                    "email": "seller@email.com",
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/sellers")
                                .header(
                                        "X-Requester-Identification",
                                        "9001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identification")
                        .value("2001"))
                .andExpect(jsonPath("$.fullName")
                        .value("Test Seller"))
                .andExpect(jsonPath("$.email")
                        .value("seller@email.com"))
                .andExpect(jsonPath("$.role")
                        .value("SELLER"))
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"))
                .andExpect(jsonPath("$.warehouses.length()")
                        .value(1))
                .andExpect(jsonPath(
                        "$.warehouses[0].ownerType"
                ).value("SELLER"))
                .andExpect(jsonPath("$.productCount")
                        .value(0));

        verify(registerSellerUseCase).register(
                eq("9001"),
                eq("2001"),
                eq("Test Seller"),
                eq("seller@email.com"),
                eq(UserStatus.ACTIVE),
                any(Warehouse.class)
        );
    }

    @Test
    void shouldFindSellerByIdentification()
            throws Exception {

        Seller seller = createSeller(
                "2001",
                "2001@email.com"
        );

        when(sellerQueryUseCase
                .findByIdentification(
                        "9001",
                        "2001"
                ))
                .thenReturn(seller);

        mockMvc.perform(
                        get("/api/v1/sellers/2001")
                                .header(
                                        "X-Requester-Identification",
                                        "9001"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identification")
                        .value("2001"))
                .andExpect(jsonPath("$.email")
                        .value("2001@email.com"))
                .andExpect(jsonPath("$.role")
                        .value("SELLER"))
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"))
                .andExpect(jsonPath(
                        "$.warehouses[0].ownerType"
                ).value("SELLER"));

        verify(sellerQueryUseCase)
                .findByIdentification(
                        "9001",
                        "2001"
                );
    }

    @Test
    void shouldFindAllSellers()
            throws Exception {

        Seller firstSeller = createSeller(
                "2001",
                "2001@email.com"
        );

        Seller secondSeller = createSeller(
                "2002",
                "2002@email.com"
        );

        when(sellerQueryUseCase.findAll("9001"))
                .thenReturn(
                        List.of(
                                firstSeller,
                                secondSeller
                        )
                );

        mockMvc.perform(
                        get("/api/v1/sellers")
                                .header(
                                        "X-Requester-Identification",
                                        "9001"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(2))
                .andExpect(jsonPath(
                        "$[0].identification"
                ).value("2001"))
                .andExpect(jsonPath(
                        "$[0].email"
                ).value("2001@email.com"))
                .andExpect(jsonPath(
                        "$[1].identification"
                ).value("2002"))
                .andExpect(jsonPath(
                        "$[1].email"
                ).value("2002@email.com"));

        verify(sellerQueryUseCase)
                .findAll("9001");
    }

    @Test
    void shouldRejectInvalidSellerRequest()
            throws Exception {

        String requestBody = """
                {
                    "identification": "",
                    "fullName": "",
                    "email": "invalid-email",
                    "status": null
                }
                """;

        mockMvc.perform(
                        post("/api/v1/sellers")
                                .header(
                                        "X-Requester-Identification",
                                        "9001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Request validation failed."
                        ))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/sellers"))
                .andExpect(jsonPath(
                        "$.validationErrors.identification"
                ).exists())
                .andExpect(jsonPath(
                        "$.validationErrors.fullName"
                ).exists())
                .andExpect(jsonPath(
                        "$.validationErrors.email"
                ).exists())
                .andExpect(jsonPath(
                        "$.validationErrors.status"
                ).exists());

        verifyNoInteractions(registerSellerUseCase);
    }

    @Test
    void shouldReturnConflictWhenSellerAlreadyExists()
            throws Exception {

        when(registerSellerUseCase.register(
                eq("9001"),
                eq("2001"),
                eq("Test Seller"),
                eq("seller@email.com"),
                eq(UserStatus.ACTIVE),
                any(Warehouse.class)
        )).thenThrow(
                new IllegalStateException(
                        "Email is already registered."
                )
        );

        String requestBody = """
                {
                    "identification": "2001",
                    "fullName": "Test Seller",
                    "email": "seller@email.com",
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/sellers")
                                .header(
                                        "X-Requester-Identification",
                                        "9001"
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
                .andExpect(jsonPath("$.message")
                        .value(
                                "Email is already registered."
                        ))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/sellers"));
    }

    /**
     * Crea un vendedor para las pruebas.
     *
     * @param identification identificación del vendedor
     * @param email correo electrónico del vendedor
     * @return vendedor de prueba
     */
    private Seller createSeller(
            String identification,
            String email
    ) {
        return new Seller(
                identification,
                "Test Seller",
                email,
                UserStatus.ACTIVE,
                new Warehouse(
                        WarehouseOwnerType.SELLER
                )
        );
    }
}