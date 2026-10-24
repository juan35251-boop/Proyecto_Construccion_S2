package application.ports.input;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Return;

/**
 * Caso de uso encargado de solicitar
 * devoluciones de órdenes.
 */
public interface RequestReturnUseCase {

    /**
     * Solicita una devolución utilizando
     * objetos del dominio.
     *
     * @param buyer comprador solicitante
     * @param order orden que será devuelta
     * @return devolución creada
     */
    Return request(
            Buyer buyer,
            Order order
    );

    /**
     * Solicita una devolución utilizando identificadores.
     *
     * Este método está preparado para ser utilizado
     * desde la API REST.
     *
     * @param buyerIdentification identificación del comprador
     * @param orderId identificador de la orden
     * @return devolución creada
     */
    Return requestByIdentifiers(
            String buyerIdentification,
            String orderId
    );
}