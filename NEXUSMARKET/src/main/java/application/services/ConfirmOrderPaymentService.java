package application.services;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.ports.output.OrderRepository;

/**
 * Servicio de aplicación encargado de confirmar
 * el pago de un pedido.
 *
 * No realiza un cobro bancario. Su responsabilidad es cambiar
 * el pedido de pendiente de pago a pagado y guardar el cambio.
 */
public class ConfirmOrderPaymentService {

    private final OrderRepository orderRepository;

    /**
     * Construye el servicio con el repositorio de pedidos.
     *
     * @param orderRepository repositorio utilizado para guardar el pedido
     */
    public ConfirmOrderPaymentService(
            OrderRepository orderRepository
    ) {
        validateRepository(orderRepository);
        this.orderRepository = orderRepository;
    }

    /**
     * Confirma el pago de un pedido perteneciente al comprador.
     *
     * @param buyer comprador que confirma el pago
     * @param order pedido que fue pagado
     * @return pedido actualizado
     */
    public Order confirm(
            Buyer buyer,
            Order order
    ) {
        validateBuyer(buyer);
        validateOrder(order);
        validateBuyerCanPurchase(buyer);
        validateOrderOwner(buyer, order);

        /*
         * Order controla internamente que solamente un pedido
         * pendiente de pago pueda pasar al estado pagado.
         */
        order.markAsPaid();

        /*
         * Se solicita guardar el nuevo estado del pedido.
         */
        orderRepository.save(order);

        return order;
    }

    /**
     * Valida que el repositorio exista.
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
     * Valida que la operación sea realizada por un comprador.
     */
    private void validateBuyer(Buyer buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Payment must be confirmed by a buyer."
            );
        }
    }

    /**
     * Valida que exista un pedido.
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order must not be null."
            );
        }
    }

    /**
     * Comprueba que el comprador esté autorizado para comprar.
     */
    private void validateBuyerCanPurchase(Buyer buyer) {
        if (!buyer.canPurchase()) {
            throw new IllegalStateException(
                    "Buyer is not authorized to confirm payments."
            );
        }
    }

    /**
     * Comprueba que el comprador solamente pague su propio pedido.
     */
    private void validateOrderOwner(
            Buyer buyer,
            Order order
    ) {
        if (order.getBuyer() != buyer) {
            throw new IllegalStateException(
                    "Buyer can only pay their own order."
            );
        }
    }
}