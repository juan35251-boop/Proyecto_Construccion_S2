package application.adapters.input.rest.controllers;

import application.adapters.input.rest.dto.CreateOrderRequest;
import application.adapters.input.rest.dto.OrderResponse;
import application.domain.models.Order;
import application.ports.input.CompleteOrderUseCase;
import application.ports.input.ConfirmOrderPaymentUseCase;
import application.ports.input.CreateOrderUseCase;
import application.ports.input.DeliverDigitalOrderUseCase;
import application.ports.input.OrderQueryUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST encargado del ciclo principal
 * de los pedidos.
 *
 * Permite:
 *
 * - Crear pedidos desde un carrito.
 * - Consultar pedidos autorizados.
 * - Confirmar el pago.
 * - Entregar pedidos digitales.
 * - Finalizar pedidos entregados.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final ConfirmOrderPaymentUseCase
            confirmOrderPaymentUseCase;
    private final DeliverDigitalOrderUseCase
            deliverDigitalOrderUseCase;
    private final CompleteOrderUseCase completeOrderUseCase;
    private final OrderQueryUseCase orderQueryUseCase;

    /**
     * Construye el controlador con todos los casos
     * de uso requeridos.
     */
    public OrderController(
            CreateOrderUseCase createOrderUseCase,
            ConfirmOrderPaymentUseCase
                    confirmOrderPaymentUseCase,
            DeliverDigitalOrderUseCase
                    deliverDigitalOrderUseCase,
            CompleteOrderUseCase completeOrderUseCase,
            OrderQueryUseCase orderQueryUseCase
    ) {
        validateDependency(
                createOrderUseCase,
                "Create order use case"
        );

        validateDependency(
                confirmOrderPaymentUseCase,
                "Confirm order payment use case"
        );

        validateDependency(
                deliverDigitalOrderUseCase,
                "Deliver digital order use case"
        );

        validateDependency(
                completeOrderUseCase,
                "Complete order use case"
        );

        validateDependency(
                orderQueryUseCase,
                "Order query use case"
        );

        this.createOrderUseCase = createOrderUseCase;
        this.confirmOrderPaymentUseCase =
                confirmOrderPaymentUseCase;
        this.deliverDigitalOrderUseCase =
                deliverDigitalOrderUseCase;
        this.completeOrderUseCase =
                completeOrderUseCase;
        this.orderQueryUseCase = orderQueryUseCase;
    }

    /**
     * Crea un pedido a partir del carrito
     * perteneciente al comprador.
     */
    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String buyerIdentification,

            @Valid
            @RequestBody
            CreateOrderRequest request
    ) {
        Order order =
                createOrderUseCase.createByCartId(
                        buyerIdentification,
                        request.cartId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(OrderResponse.from(order));
    }

    /**
     * Consulta los pedidos visibles para
     * el usuario solicitante.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>>
    findAccessibleOrders(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String requesterIdentification
    ) {
        List<OrderResponse> orders =
                orderQueryUseCase
                        .findAccessibleOrdersByRequesterIdentification(
                                requesterIdentification
                        )
                        .stream()
                        .map(OrderResponse::from)
                        .toList();

        return ResponseEntity.ok(orders);
    }

    /**
     * Consulta un pedido específico respetando
     * los permisos del usuario solicitante.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> findById(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String requesterIdentification,

            @PathVariable
            String orderId
    ) {
        Order order =
                orderQueryUseCase
                        .findAccessibleOrderByIdentifiers(
                                requesterIdentification,
                                orderId
                        );

        return ResponseEntity.ok(
                OrderResponse.from(order)
        );
    }

    /**
     * Confirma el pago de un pedido.
     *
     * El comprador solamente puede confirmar
     * el pago de sus propios pedidos.
     */
    @PatchMapping("/{orderId}/payment")
    public ResponseEntity<OrderResponse> confirmPayment(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String buyerIdentification,

            @PathVariable
            String orderId
    ) {
        Order order =
                confirmOrderPaymentUseCase
                        .confirmByIdentifiers(
                                buyerIdentification,
                                orderId
                        );

        return ResponseEntity.ok(
                OrderResponse.from(order)
        );
    }

    /**
     * Entrega un pedido exclusivamente digital.
     *
     * Antes de ejecutar la entrega se comprueba
     * que el solicitante pueda consultar el pedido.
     */
    @PatchMapping("/{orderId}/digital-delivery")
    public ResponseEntity<OrderResponse>
    deliverDigitalOrder(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String requesterIdentification,

            @PathVariable
            String orderId
    ) {
        /*
         * Esta consulta comprueba que el usuario esté activo
         * y tenga permiso sobre el pedido.
         */
        orderQueryUseCase
                .findAccessibleOrderByIdentifiers(
                        requesterIdentification,
                        orderId
                );

        Order order =
                deliverDigitalOrderUseCase
                        .deliverByOrderId(orderId);

        return ResponseEntity.ok(
                OrderResponse.from(order)
        );
    }

    /**
     * Finaliza un pedido cuya entrega ya
     * fue confirmada.
     *
     * Antes de finalizarlo se comprueba que el usuario
     * tenga acceso al pedido.
     */
    @PatchMapping("/{orderId}/completion")
    public ResponseEntity<OrderResponse> complete(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String requesterIdentification,

            @PathVariable
            String orderId
    ) {
        orderQueryUseCase
                .findAccessibleOrderByIdentifiers(
                        requesterIdentification,
                        orderId
                );

        Order order =
                completeOrderUseCase
                        .completeByOrderId(orderId);

        return ResponseEntity.ok(
                OrderResponse.from(order)
        );
    }

    /**
     * Valida una dependencia requerida
     * por el controlador.
     */
    private void validateDependency(
            Object dependency,
            String dependencyName
    ) {
        if (dependency == null) {
            throw new IllegalArgumentException(
                    dependencyName + " must not be null."
            );
        }
    }
}