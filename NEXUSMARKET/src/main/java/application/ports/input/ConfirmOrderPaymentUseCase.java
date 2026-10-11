package application.ports.input;

import application.domain.models.Buyer;
import application.domain.models.Order;

/**
 * Puerto de entrada que define el caso de uso
 * para confirmar el pago de un pedido.
 *
 * Este caso de uso no realiza un cobro bancario.
 * Solamente registra que el pedido fue pagado
 * y actualiza su estado.
 */
public interface ConfirmOrderPaymentUseCase {

    /**
     * Confirma el pago de un pedido perteneciente
     * al comprador indicado.
     *
     * El pedido debe encontrarse en estado
     * PENDING_PAYMENT para poder pasar a PAID.
     *
     * @param buyer comprador que confirma el pago
     * @param order pedido que fue pagado
     * @return pedido actualizado
     *
     * @throws IllegalArgumentException si el comprador
     *         o el pedido son nulos
     * @throws IllegalStateException si el comprador no puede
     *         comprar, el pedido pertenece a otra persona
     *         o el pedido no está pendiente de pago
     */
    Order confirm(
            Buyer buyer,
            Order order
    );
}