package application.adapters.input.rest.controllers;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.valueobjects.OrderStatus;
import application.ports.input.ShipmentManagementUseCase;
import application.ports.input.ShipmentQueryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas del controlador REST de envíos.
 *
 * Comprueba la creación, consulta, despacho
 * y confirmación de entrega de envíos físicos.
 */
@ExtendWith(MockitoExtension.class)
class ShipmentControllerTest {

    private static final String REQUESTER_HEADER =
            "X-Requester-Identification";

    private static final String OPERATOR_IDENTIFICATION =
            "operator-001";

    private static final String BUYER_IDENTIFICATION =
            "buyer-001";

    private static final String ORDER_ID =
            "order-001";

    private static final String SHIPMENT_ID =
            "shipment-001";

    @Mock
    private ShipmentManagementUseCase
            shipmentManagementUseCase;

    @Mock
    private ShipmentQueryUseCase
            shipmentQueryUseCase;

    private MockMvc mockMvc;

    /**
     * Construye el controlador con casos
     * de uso simulados.
     */
    @BeforeEach
    void setUp() {
        ShipmentController controller =
                new ShipmentController(
                        shipmentManagementUseCase,
                        shipmentQueryUseCase
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    /**
     * Comprueba la creación de un envío
     * para un pedido físico pagado.
     */
    @Test
    void shouldCreateShipment()
            throws Exception {

        Shipment shipment = createShipmentResponse(
                OrderStatus.PAID,
                false,
                false
        );

        when(
                shipmentManagementUseCase
                        .createShipmentByIdentifiers(
                                OPERATOR_IDENTIFICATION,
                                ORDER_ID
                        )
        ).thenReturn(shipment);

        mockMvc.perform(
                        post("/api/v1/shipments")
                                .header(
                                        REQUESTER_HEADER,
                                        OPERATOR_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "orderId": "order-001"
                                        }
                                        """
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.shipmentId")
                                .value(SHIPMENT_ID)
                )
                .andExpect(
                        jsonPath("$.orderId")
                                .value(ORDER_ID)
                )
                .andExpect(
                        jsonPath("$.buyerIdentification")
                                .value(BUYER_IDENTIFICATION)
                )
                .andExpect(
                        jsonPath("$.orderStatus")
                                .value("PAID")
                )
                .andExpect(
                        jsonPath("$.dispatched")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.delivered")
                                .value(false)
                );

        verify(shipmentManagementUseCase)
                .createShipmentByIdentifiers(
                        OPERATOR_IDENTIFICATION,
                        ORDER_ID
                );
    }

    /**
     * Comprueba la consulta de los envíos
     * visibles para el solicitante.
     */
    @Test
    void shouldReturnAccessibleShipments()
            throws Exception {

        Shipment shipment = createShipmentResponse(
                OrderStatus.DISPATCHED,
                true,
                false
        );

        when(
                shipmentQueryUseCase
                        .findAccessibleShipmentsByRequesterIdentification(
                                OPERATOR_IDENTIFICATION
                        )
        ).thenReturn(
                List.of(shipment)
        );

        mockMvc.perform(
                        get("/api/v1/shipments")
                                .header(
                                        REQUESTER_HEADER,
                                        OPERATOR_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].shipmentId")
                                .value(SHIPMENT_ID)
                )
                .andExpect(
                        jsonPath("$[0].orderId")
                                .value(ORDER_ID)
                )
                .andExpect(
                        jsonPath("$[0].orderStatus")
                                .value("DISPATCHED")
                )
                .andExpect(
                        jsonPath("$[0].dispatched")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$[0].delivered")
                                .value(false)
                );

        verify(shipmentQueryUseCase)
                .findAccessibleShipmentsByRequesterIdentification(
                        OPERATOR_IDENTIFICATION
                );
    }

    /**
     * Comprueba la consulta individual de un envío.
     */
    @Test
    void shouldReturnShipmentById()
            throws Exception {

        Shipment shipment = createShipmentResponse(
                OrderStatus.PAID,
                false,
                false
        );

        when(
                shipmentQueryUseCase
                        .findAccessibleShipmentByIdentifiers(
                                BUYER_IDENTIFICATION,
                                SHIPMENT_ID
                        )
        ).thenReturn(shipment);

        mockMvc.perform(
                        get(
                                "/api/v1/shipments/{shipmentId}",
                                SHIPMENT_ID
                        )
                                .header(
                                        REQUESTER_HEADER,
                                        BUYER_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.shipmentId")
                                .value(SHIPMENT_ID)
                )
                .andExpect(
                        jsonPath("$.buyerIdentification")
                                .value(BUYER_IDENTIFICATION)
                )
                .andExpect(
                        jsonPath("$.orderStatus")
                                .value("PAID")
                );

        verify(shipmentQueryUseCase)
                .findAccessibleShipmentByIdentifiers(
                        BUYER_IDENTIFICATION,
                        SHIPMENT_ID
                );
    }

    /**
     * Comprueba el despacho de un envío.
     */
    @Test
    void shouldDispatchShipment()
            throws Exception {

        Shipment shipment = createShipmentResponse(
                OrderStatus.DISPATCHED,
                true,
                false
        );

        when(
                shipmentManagementUseCase
                        .dispatchShipmentByIdentifiers(
                                OPERATOR_IDENTIFICATION,
                                SHIPMENT_ID
                        )
        ).thenReturn(shipment);

        mockMvc.perform(
                        patch(
                                "/api/v1/shipments/{shipmentId}/dispatch",
                                SHIPMENT_ID
                        )
                                .header(
                                        REQUESTER_HEADER,
                                        OPERATOR_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.shipmentId")
                                .value(SHIPMENT_ID)
                )
                .andExpect(
                        jsonPath("$.orderStatus")
                                .value("DISPATCHED")
                )
                .andExpect(
                        jsonPath("$.dispatched")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.delivered")
                                .value(false)
                );

        verify(shipmentManagementUseCase)
                .dispatchShipmentByIdentifiers(
                        OPERATOR_IDENTIFICATION,
                        SHIPMENT_ID
                );
    }

    /**
     * Comprueba la confirmación de entrega.
     */
    @Test
    void shouldConfirmDelivery()
            throws Exception {

        Shipment shipment = createShipmentResponse(
                OrderStatus.DELIVERED,
                true,
                true
        );

        when(
                shipmentManagementUseCase
                        .confirmDeliveryByIdentifiers(
                                OPERATOR_IDENTIFICATION,
                                SHIPMENT_ID
                        )
        ).thenReturn(shipment);

        mockMvc.perform(
                        patch(
                                "/api/v1/shipments/{shipmentId}/delivery",
                                SHIPMENT_ID
                        )
                                .header(
                                        REQUESTER_HEADER,
                                        OPERATOR_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.shipmentId")
                                .value(SHIPMENT_ID)
                )
                .andExpect(
                        jsonPath("$.orderStatus")
                                .value("DELIVERED")
                )
                .andExpect(
                        jsonPath("$.dispatched")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.delivered")
                                .value(true)
                );

        verify(shipmentManagementUseCase)
                .confirmDeliveryByIdentifiers(
                        OPERATOR_IDENTIFICATION,
                        SHIPMENT_ID
                );
    }

    /**
     * Comprueba que no sea posible crear un envío
     * sin indicar el pedido.
     */
    @Test
    void shouldRejectEmptyOrderIdentifier()
            throws Exception {

        mockMvc.perform(
                        post("/api/v1/shipments")
                                .header(
                                        REQUESTER_HEADER,
                                        OPERATOR_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "orderId": ""
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    /**
     * Comprueba que el encabezado del solicitante
     * sea obligatorio.
     */
    @Test
    void shouldRejectRequestWithoutRequesterHeader()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/shipments")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    /**
     * Crea un envío simulado con todos los datos
     * requeridos por ShipmentResponse.
     */
    private Shipment createShipmentResponse(
            OrderStatus status,
            boolean dispatched,
            boolean delivered
    ) {
        Buyer buyer = mock(Buyer.class);
        Order order = mock(Order.class);
        Shipment shipment = mock(Shipment.class);

        when(buyer.getIdentification())
                .thenReturn(BUYER_IDENTIFICATION);

        when(order.getOrderId())
                .thenReturn(ORDER_ID);

        when(order.getBuyer())
                .thenReturn(buyer);

        when(order.getStatus())
                .thenReturn(status);

        when(shipment.getShipmentId())
                .thenReturn(SHIPMENT_ID);

        when(shipment.getOrder())
                .thenReturn(order);

        when(shipment.isDispatched())
                .thenReturn(dispatched);

        when(shipment.isDelivered())
                .thenReturn(delivered);

        return shipment;
    }
}