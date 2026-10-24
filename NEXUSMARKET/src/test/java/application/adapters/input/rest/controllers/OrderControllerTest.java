package application.adapters.input.rest.controllers;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.valueobjects.OrderStatus;
import application.ports.input.CompleteOrderUseCase;
import application.ports.input.ConfirmOrderPaymentUseCase;
import application.ports.input.CreateOrderUseCase;
import application.ports.input.DeliverDigitalOrderUseCase;
import application.ports.input.OrderQueryUseCase;
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
 * Pruebas del controlador REST de pedidos.
 *
 * Comprueba la creación, consulta, pago,
 * entrega digital y finalización de pedidos.
 */
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private static final String REQUESTER_HEADER =
            "X-Requester-Identification";

    private static final String BUYER_IDENTIFICATION =
            "1001";

    private static final String ORDER_ID =
            "order-001";

    private static final String CART_ID =
            "cart-001";

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private ConfirmOrderPaymentUseCase
            confirmOrderPaymentUseCase;

    @Mock
    private DeliverDigitalOrderUseCase
            deliverDigitalOrderUseCase;

    @Mock
    private CompleteOrderUseCase completeOrderUseCase;

    @Mock
    private OrderQueryUseCase orderQueryUseCase;

    private MockMvc mockMvc;

    /**
     * Construye el controlador con casos
     * de uso simulados.
     */
    @BeforeEach
    void setUp() {
        OrderController controller =
                new OrderController(
                        createOrderUseCase,
                        confirmOrderPaymentUseCase,
                        deliverDigitalOrderUseCase,
                        completeOrderUseCase,
                        orderQueryUseCase
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    /**
     * Comprueba la creación de un pedido
     * a partir de un carrito.
     */
    @Test
    void shouldCreateOrderFromCart()
            throws Exception {

        Order order = createOrderResponse(
                OrderStatus.PENDING_PAYMENT
        );

        when(
                createOrderUseCase.createByCartId(
                        BUYER_IDENTIFICATION,
                        CART_ID
                )
        ).thenReturn(order);

        mockMvc.perform(
                        post("/api/v1/orders")
                                .header(
                                        REQUESTER_HEADER,
                                        BUYER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "cartId": "cart-001"
                                        }
                                        """
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.orderId")
                                .value(ORDER_ID)
                )
                .andExpect(
                        jsonPath("$.buyerIdentification")
                                .value(BUYER_IDENTIFICATION)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("PENDING_PAYMENT")
                )
                .andExpect(
                        jsonPath("$.containsPhysicalProducts")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.items")
                                .isArray()
                );

        verify(createOrderUseCase)
                .createByCartId(
                        BUYER_IDENTIFICATION,
                        CART_ID
                );
    }

    /**
     * Comprueba la consulta de todos los pedidos
     * visibles para el solicitante.
     */
    @Test
    void shouldReturnAccessibleOrders()
            throws Exception {

        Order order = createOrderResponse(
                OrderStatus.PENDING_PAYMENT
        );

        when(
                orderQueryUseCase
                        .findAccessibleOrdersByRequesterIdentification(
                                BUYER_IDENTIFICATION
                        )
        ).thenReturn(
                List.of(order)
        );

        mockMvc.perform(
                        get("/api/v1/orders")
                                .header(
                                        REQUESTER_HEADER,
                                        BUYER_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].orderId")
                                .value(ORDER_ID)
                )
                .andExpect(
                        jsonPath("$[0].buyerIdentification")
                                .value(BUYER_IDENTIFICATION)
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("PENDING_PAYMENT")
                );

        verify(orderQueryUseCase)
                .findAccessibleOrdersByRequesterIdentification(
                        BUYER_IDENTIFICATION
                );
    }

    /**
     * Comprueba la consulta de un pedido
     * mediante su identificador.
     */
    @Test
    void shouldReturnAccessibleOrderById()
            throws Exception {

        Order order = createOrderResponse(
                OrderStatus.PAID
        );

        when(
                orderQueryUseCase
                        .findAccessibleOrderByIdentifiers(
                                BUYER_IDENTIFICATION,
                                ORDER_ID
                        )
        ).thenReturn(order);

        mockMvc.perform(
                        get(
                                "/api/v1/orders/{orderId}",
                                ORDER_ID
                        )
                                .header(
                                        REQUESTER_HEADER,
                                        BUYER_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.orderId")
                                .value(ORDER_ID)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("PAID")
                );

        verify(orderQueryUseCase)
                .findAccessibleOrderByIdentifiers(
                        BUYER_IDENTIFICATION,
                        ORDER_ID
                );
    }

    /**
     * Comprueba la confirmación del pago.
     */
    @Test
    void shouldConfirmOrderPayment()
            throws Exception {

        Order order = createOrderResponse(
                OrderStatus.PAID
        );

        when(
                confirmOrderPaymentUseCase
                        .confirmByIdentifiers(
                                BUYER_IDENTIFICATION,
                                ORDER_ID
                        )
        ).thenReturn(order);

        mockMvc.perform(
                        patch(
                                "/api/v1/orders/{orderId}/payment",
                                ORDER_ID
                        )
                                .header(
                                        REQUESTER_HEADER,
                                        BUYER_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.orderId")
                                .value(ORDER_ID)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("PAID")
                );

        verify(confirmOrderPaymentUseCase)
                .confirmByIdentifiers(
                        BUYER_IDENTIFICATION,
                        ORDER_ID
                );
    }

    /**
     * Comprueba la entrega de un pedido digital.
     */
    @Test
    void shouldDeliverDigitalOrder()
            throws Exception {

        /*
         * Este pedido solamente demuestra que la consulta
         * de autorización fue realizada. No necesita getters
         * simulados porque no se convierte en una respuesta.
         */
        Order accessibleOrder = mock(Order.class);

        Order deliveredOrder = createOrderResponse(
                OrderStatus.DELIVERED
        );

        when(
                orderQueryUseCase
                        .findAccessibleOrderByIdentifiers(
                                BUYER_IDENTIFICATION,
                                ORDER_ID
                        )
        ).thenReturn(accessibleOrder);

        when(
                deliverDigitalOrderUseCase
                        .deliverByOrderId(ORDER_ID)
        ).thenReturn(deliveredOrder);

        mockMvc.perform(
                        patch(
                                "/api/v1/orders/{orderId}/digital-delivery",
                                ORDER_ID
                        )
                                .header(
                                        REQUESTER_HEADER,
                                        BUYER_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.orderId")
                                .value(ORDER_ID)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("DELIVERED")
                );

        verify(orderQueryUseCase)
                .findAccessibleOrderByIdentifiers(
                        BUYER_IDENTIFICATION,
                        ORDER_ID
                );

        verify(deliverDigitalOrderUseCase)
                .deliverByOrderId(ORDER_ID);
    }

    /**
     * Comprueba la finalización de un pedido entregado.
     */
    @Test
    void shouldCompleteDeliveredOrder()
            throws Exception {

        /*
         * Este pedido se utiliza únicamente como resultado
         * de la validación de acceso.
         */
        Order accessibleOrder = mock(Order.class);

        Order finalizedOrder = createOrderResponse(
                OrderStatus.FINALIZED
        );

        when(
                orderQueryUseCase
                        .findAccessibleOrderByIdentifiers(
                                BUYER_IDENTIFICATION,
                                ORDER_ID
                        )
        ).thenReturn(accessibleOrder);

        when(
                completeOrderUseCase
                        .completeByOrderId(ORDER_ID)
        ).thenReturn(finalizedOrder);

        mockMvc.perform(
                        patch(
                                "/api/v1/orders/{orderId}/completion",
                                ORDER_ID
                        )
                                .header(
                                        REQUESTER_HEADER,
                                        BUYER_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.orderId")
                                .value(ORDER_ID)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("FINALIZED")
                );

        verify(orderQueryUseCase)
                .findAccessibleOrderByIdentifiers(
                        BUYER_IDENTIFICATION,
                        ORDER_ID
                );

        verify(completeOrderUseCase)
                .completeByOrderId(ORDER_ID);
    }

    /**
     * Comprueba que no sea posible crear un pedido
     * sin indicar el carrito.
     */
    @Test
    void shouldRejectEmptyCartIdentifier()
            throws Exception {

        mockMvc.perform(
                        post("/api/v1/orders")
                                .header(
                                        REQUESTER_HEADER,
                                        BUYER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "cartId": ""
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
                        get("/api/v1/orders")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    /**
     * Crea un pedido simulado con todos los datos
     * requeridos por OrderResponse.
     *
     * Este método solamente debe utilizarse cuando el pedido
     * será transformado en una respuesta JSON.
     */
    private Order createOrderResponse(
            OrderStatus status
    ) {
        Buyer buyer = mock(Buyer.class);
        Order order = mock(Order.class);

        when(buyer.getIdentification())
                .thenReturn(BUYER_IDENTIFICATION);

        when(order.getOrderId())
                .thenReturn(ORDER_ID);

        when(order.getBuyer())
                .thenReturn(buyer);

        when(order.getStatus())
                .thenReturn(status);

        when(order.containsPhysicalProducts())
                .thenReturn(false);

        when(order.getItems())
                .thenReturn(List.of());

        return order;
    }
}