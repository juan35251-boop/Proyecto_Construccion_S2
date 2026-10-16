package application.adapters.input.rest.controllers;

import application.adapters.input.rest.exception.GlobalExceptionHandler;
import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.ports.input.BuyerAddressUseCase;
import application.ports.input.BuyerQueryUseCase;
import application.ports.input.RegisterBuyerUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class BuyerControllerTest {

    private RegisterBuyerUseCase registerBuyerUseCase;
    private BuyerQueryUseCase buyerQueryUseCase;
    private BuyerAddressUseCase buyerAddressUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        registerBuyerUseCase =
                mock(RegisterBuyerUseCase.class);

        buyerQueryUseCase =
                mock(BuyerQueryUseCase.class);

        buyerAddressUseCase =
                mock(BuyerAddressUseCase.class);

        BuyerController buyerController =
                new BuyerController(
                        registerBuyerUseCase,
                        buyerQueryUseCase,
                        buyerAddressUseCase
                );

        mockMvc = standaloneSetup(buyerController)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }

    @Test
    void shouldRegisterBuyer() throws Exception {
        Buyer buyer = createBuyer();

        when(registerBuyerUseCase.register(
                "1001",
                "Juan Perez",
                "juan@email.com",
                "Calle 10 #20-30"
        )).thenReturn(buyer);

        String requestBody = """
                {
                    "identification": "1001",
                    "fullName": "Juan Perez",
                    "email": "juan@email.com",
                    "primaryAddress": "Calle 10 #20-30"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/buyers")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identification")
                        .value("1001"))
                .andExpect(jsonPath("$.fullName")
                        .value("Juan Perez"))
                .andExpect(jsonPath("$.email")
                        .value("juan@email.com"))
                .andExpect(jsonPath("$.role")
                        .value("BUYER"))
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"))
                .andExpect(jsonPath("$.primaryAddress")
                        .value("Calle 10 #20-30"))
                .andExpect(jsonPath("$.commercialStatus")
                        .value("ACTIVE"))
                .andExpect(jsonPath("$.additionalAddresses")
                        .isEmpty());

        verify(registerBuyerUseCase).register(
                "1001",
                "Juan Perez",
                "juan@email.com",
                "Calle 10 #20-30"
        );
    }

    @Test
    void shouldFindBuyerByIdentification()
            throws Exception {

        Buyer buyer = createBuyer();

        when(buyerQueryUseCase.findByIdentification(
                "1001",
                "1001"
        )).thenReturn(buyer);

        mockMvc.perform(
                        get("/api/v1/buyers/1001")
                                .header(
                                        "X-Requester-Identification",
                                        "1001"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identification")
                        .value("1001"))
                .andExpect(jsonPath("$.fullName")
                        .value("Juan Perez"))
                .andExpect(jsonPath("$.email")
                        .value("juan@email.com"))
                .andExpect(jsonPath("$.role")
                        .value("BUYER"))
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"))
                .andExpect(jsonPath("$.primaryAddress")
                        .value("Calle 10 #20-30"))
                .andExpect(jsonPath("$.commercialStatus")
                        .value("ACTIVE"));

        verify(buyerQueryUseCase)
                .findByIdentification(
                        "1001",
                        "1001"
                );
    }

    @Test
    void shouldRejectInvalidBuyerRequest()
            throws Exception {

        String requestBody = """
                {
                    "identification": "",
                    "fullName": "",
                    "email": "invalid-email",
                    "primaryAddress": ""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/buyers")
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
                        .value("/api/v1/buyers"))
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
                        "$.validationErrors.primaryAddress"
                ).exists());
    }

    @Test
    void shouldReturnConflictWhenBuyerAlreadyExists()
            throws Exception {

        when(registerBuyerUseCase.register(
                "1001",
                "Juan Perez",
                "juan@email.com",
                "Calle 10 #20-30"
        )).thenThrow(
                new IllegalStateException(
                        "Email is already registered."
                )
        );

        String requestBody = """
                {
                    "identification": "1001",
                    "fullName": "Juan Perez",
                    "email": "juan@email.com",
                    "primaryAddress": "Calle 10 #20-30"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/buyers")
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
                        .value("/api/v1/buyers"));
    }

    @Test
    void shouldChangePrimaryAddress()
            throws Exception {

        Buyer buyer = createBuyer();

        buyer.changePrimaryAddress(
                "Carrera 50 #40-20"
        );

        when(buyerAddressUseCase.changePrimaryAddress(
                "1001",
                "Carrera 50 #40-20"
        )).thenReturn(buyer);

        String requestBody = """
                {
                    "address": "Carrera 50 #40-20"
                }
                """;

        mockMvc.perform(
                        patch(
                                "/api/v1/buyers/me/primary-address"
                        )
                                .header(
                                        "X-Requester-Identification",
                                        "1001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identification")
                        .value("1001"))
                .andExpect(jsonPath("$.primaryAddress")
                        .value("Carrera 50 #40-20"));

        verify(buyerAddressUseCase)
                .changePrimaryAddress(
                        "1001",
                        "Carrera 50 #40-20"
                );
    }

    @Test
    void shouldAddAdditionalAddress()
            throws Exception {

        Buyer buyer = createBuyer();

        buyer.addAdditionalAddress(
                "Carrera 40 #50-60"
        );

        when(buyerAddressUseCase.addAdditionalAddress(
                "1001",
                "Carrera 40 #50-60"
        )).thenReturn(buyer);

        String requestBody = """
                {
                    "address": "Carrera 40 #50-60"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/buyers/me/addresses")
                                .header(
                                        "X-Requester-Identification",
                                        "1001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identification")
                        .value("1001"))
                .andExpect(jsonPath(
                        "$.additionalAddresses[0]"
                ).value("Carrera 40 #50-60"));

        verify(buyerAddressUseCase)
                .addAdditionalAddress(
                        "1001",
                        "Carrera 40 #50-60"
                );
    }

    @Test
    void shouldRemoveAdditionalAddress()
            throws Exception {

        when(buyerAddressUseCase.removeAdditionalAddress(
                "1001",
                "Carrera 40 #50-60"
        )).thenReturn(true);

        String requestBody = """
                {
                    "address": "Carrera 40 #50-60"
                }
                """;

        mockMvc.perform(
                        delete("/api/v1/buyers/me/addresses")
                                .header(
                                        "X-Requester-Identification",
                                        "1001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isNoContent());

        verify(buyerAddressUseCase)
                .removeAdditionalAddress(
                        "1001",
                        "Carrera 40 #50-60"
                );
    }

    private Buyer createBuyer() {
        return new Buyer(
                "1001",
                "Juan Perez",
                "juan@email.com",
                UserStatus.ACTIVE,
                "Calle 10 #20-30",
                BuyerStatus.ACTIVE
        );
    }
}