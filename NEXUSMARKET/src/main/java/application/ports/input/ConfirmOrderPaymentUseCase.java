package application.ports.input;

import application.domain.models.Buyer;
import application.domain.models.Order;

/**
 * Puerto de entrada que define el caso de uso
 * para confirmar el pago de un pedido.
 *
 * Este caso de uso no realiza el cobro mediante
 * una entidad financiera. Solamente registra que
 * el pago fue aprobado y cambia el pedido desde
 * PENDING_PAYMENT hasta PAID.
 */
public interface ConfirmOrderPaymentUseCase {

    /**
     * Confirma el pago utilizando objetos del dominio.
     *
     * Este método se conserva para operaciones internas
     * y para mantener las pruebas unitarias existentes.
     *
     * @param buyer comprador que confirma el pago
     * @param order pedido que fue pagado
     * @return pedido actualizado
     */
    Order confirm(
            Buyer buyer,
            Order order
    );

    /**
     * Confirma el pago utilizando los identificadores
     * recibidos desde la API REST.
     *
     * El comprador y el pedido deben existir. Además,
     * el pedido debe pertenecer al comprador indicado.
     *
     * @param buyerIdentification identificación del comprador
     * @param orderId identificador del pedido
     * @return pedido actualizado
     */
    Order confirmByIdentifiers(
            String buyerIdentification,
            String orderId
    );
}