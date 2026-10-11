package application.ports.input;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Return;

/**
 * Puerto de entrada que define el caso de uso
 * para solicitar devoluciones en NexusMarket.
 *
 * Un comprador activo puede solicitar la devolución
 * de un pedido propio que haya sido entregado
 * o finalizado.
 */
public interface RequestReturnUseCase {

    /**
     * Registra una solicitud de devolución.
     *
     * El comprador debe tener su cuenta activa.
     * Sin embargo, puede encontrarse suspendido
     * comercialmente y conservar su derecho a devolver
     * una compra anterior.
     *
     * @param buyer comprador que solicita la devolución
     * @param order pedido que se desea devolver
     * @return proceso de devolución registrado
     *
     * @throws IllegalArgumentException si el comprador
     *         o el pedido son nulos
     * @throws IllegalStateException si el comprador está
     *         inactivo, el pedido pertenece a otra persona
     *         o todavía no fue entregado
     */
    Return request(
            Buyer buyer,
            Order order
    );
}