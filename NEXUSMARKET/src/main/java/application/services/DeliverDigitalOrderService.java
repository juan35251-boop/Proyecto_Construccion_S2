package application.services;

import application.domain.models.Order;
import application.ports.input.DeliverDigitalOrderUseCase;
import application.ports.output.OrderRepository;

/**
 * Servicio de aplicación encargado de confirmar
 * la entrega de pedidos exclusivamente digitales.
 *
 * Implementa el puerto de entrada DeliverDigitalOrderUseCase,
 * permitiendo ejecutar la entrega digital sin depender
 * directamente de esta implementación.
 *
 * Un pedido digital no necesita una dirección física,
 * una bodega ni un proceso de despacho. Después de que
 * su pago ha sido confirmado, puede pasar directamente
 * del estado PAID al estado DELIVERED.
 *
 * Este servicio representa la entrega desde el punto de
 * vista del negocio. Actualmente no envía físicamente un
 * archivo, una licencia ni un enlace por correo electrónico.
 *
 * Esa entrega técnica podrá implementarse posteriormente
 * mediante un puerto y un adaptador especializado.
 *
 * Los pedidos que contengan al menos un producto físico
 * deben utilizar el proceso de envíos, incluso si también
 * incluyen productos digitales.
 */
public class DeliverDigitalOrderService
        implements DeliverDigitalOrderUseCase {

    private final OrderRepository orderRepository;

    /**
     * Construye el servicio con el repositorio utilizado
     * para guardar el pedido después de su entrega.
     *
     * @param orderRepository repositorio de pedidos
     *
     * @throws IllegalArgumentException si el repositorio es nulo
     */
    public DeliverDigitalOrderService(
            OrderRepository orderRepository
    ) {
        validateRepository(orderRepository);
        this.orderRepository = orderRepository;
    }

    /**
     * Confirma la entrega de un pedido
     * exclusivamente digital.
     *
     * Para completar la operación deben cumplirse
     * estas reglas:
     *
     * 1. El pedido debe existir.
     * 2. No debe contener productos físicos.
     * 3. Su pago debe haber sido confirmado.
     *
     * La comprobación del pago se realiza dentro del modelo
     * Order mediante markAsDelivered().
     *
     * Para un pedido digital, ese método solamente
     * permite la transición:
     *
     * PAID -> DELIVERED
     *
     * No se valida la dirección del comprador porque no
     * existe un envío físico.
     *
     * @param order pedido digital que se desea entregar
     * @return pedido actualizado en estado DELIVERED
     *
     * @throws IllegalArgumentException si el pedido es nulo
     * @throws IllegalStateException si contiene productos físicos
     * @throws IllegalStateException si el pedido no está pagado
     */
    @Override
    public Order deliver(Order order) {
        validateOrder(order);
        validateDigitalOnlyOrder(order);

        /*
         * Order protege su propio ciclo de vida.
         *
         * Si el pedido digital no está en estado PAID,
         * markAsDelivered() genera una excepción y evita
         * registrar una entrega sin pago.
         */
        order.markAsDelivered();

        /*
         * Se guarda el nuevo estado del pedido.
         *
         * Esta operación registra la entrega comercial,
         * pero todavía no realiza el envío técnico de un
         * archivo, enlace o licencia.
         */
        orderRepository.save(order);

        return order;
    }

    /**
     * Valida que el repositorio requerido exista.
     *
     * @param orderRepository repositorio que será validado
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
     * Valida que exista un pedido para entregar.
     *
     * @param order pedido que será validado
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order must not be null."
            );
        }
    }

    /**
     * Comprueba que el pedido sea exclusivamente digital.
     *
     * No basta con que tenga algún producto digital.
     * Si el pedido contiene al menos un producto físico,
     * se considera mixto y debe pasar por el proceso de
     * preparación y despacho.
     *
     * @param order pedido que será validado
     */
    private void validateDigitalOnlyOrder(Order order) {
        if (order.containsPhysicalProducts()) {
            throw new IllegalStateException(
                    "Orders with physical products require shipment."
            );
        }
    }
}