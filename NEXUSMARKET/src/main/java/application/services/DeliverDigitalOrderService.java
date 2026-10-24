package application.services;

import application.domain.models.Order;
import application.ports.input.DeliverDigitalOrderUseCase;
import application.ports.output.OrderRepository;

/**
 * Servicio encargado de confirmar la entrega
 * de pedidos exclusivamente digitales.
 *
 * Un pedido digital no necesita dirección, bodega
 * ni despacho físico. Después de confirmar su pago,
 * puede pasar directamente de PAID a DELIVERED.
 *
 * Este servicio representa la entrega desde el punto
 * de vista del negocio. Todavía no envía archivos,
 * licencias o enlaces por correo electrónico.
 */
public class DeliverDigitalOrderService
        implements DeliverDigitalOrderUseCase {

    private final OrderRepository orderRepository;

    /**
     * Construye el servicio.
     *
     * @param orderRepository repositorio de pedidos
     */
    public DeliverDigitalOrderService(
            OrderRepository orderRepository
    ) {
        validateRepository(orderRepository);
        this.orderRepository = orderRepository;
    }

    /**
     * Confirma la entrega de un pedido digital.
     *
     * El pedido:
     *
     * - Debe existir.
     * - No puede contener productos físicos.
     * - Debe encontrarse pagado.
     *
     * No se valida una dirección de entrega porque
     * los productos digitales no requieren transporte.
     *
     * @param order pedido digital
     * @return pedido actualizado
     */
    @Override
    public Order deliver(Order order) {
        validateOrder(order);
        validateDigitalOnlyOrder(order);

        /*
         * Order comprueba que el estado actual sea PAID
         * antes de cambiarlo a DELIVERED.
         */
        order.markAsDelivered();

        orderRepository.save(order);

        return order;
    }

    /**
     * Localiza y entrega un pedido mediante su identificador.
     *
     * @param orderId identificador del pedido
     * @return pedido actualizado
     */
    @Override
    public Order deliverByOrderId(String orderId) {
        validateText(
                orderId,
                "Order identifier"
        );

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Order was not found."
                        )
                );

        return deliver(order);
    }

    /**
     * Valida que exista el repositorio.
     */
    private void validateRepository(
            OrderRepository orderRepository
    ) {
        if (orderRepository == null) {
            throw new IllegalArgumentException(
                    "Order repository must not be null."
            );
        }
    }

    /**
     * Valida que exista el pedido.
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order must not be null."
            );
        }
    }

    /**
     * Impide usar la entrega digital con pedidos físicos
     * o mixtos.
     */
    private void validateDigitalOnlyOrder(Order order) {
        if (order.containsPhysicalProducts()) {
            throw new IllegalStateException(
                    "Orders with physical products require shipment."
            );
        }
    }

    /**
     * Valida un identificador obligatorio.
     */
    private void validateText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }
}