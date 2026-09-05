package application.services;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Return;
import application.ports.output.ReturnRepository;

/**
 * Servicio de aplicación encargado de registrar
 * las solicitudes de devolución.
 *
 * Un comprador activo solamente puede solicitar la devolución
 * de un pedido que le pertenece y que fue entregado o finalizado.
 */
public class RequestReturnService {

    private final ReturnRepository returnRepository;

    /**
     * Construye el servicio con el repositorio de devoluciones.
     *
     * @param returnRepository repositorio utilizado para guardar devoluciones
     */
    public RequestReturnService(
            ReturnRepository returnRepository
    ) {
        validateRepository(returnRepository);
        this.returnRepository = returnRepository;
    }

    /**
     * Registra una solicitud de devolución.
     *
     * El modelo Return valida que el pedido pertenezca al comprador
     * y que tenga un estado válido para ser devuelto.
     *
     * @param buyer comprador que solicita la devolución
     * @param order pedido que se desea devolver
     * @return devolución registrada
     */
    public Return request(
            Buyer buyer,
            Order order
    ) {
        validateBuyer(buyer);
        validateActiveBuyer(buyer);

        Return returnProcess = new Return(order, buyer);

        returnRepository.save(returnProcess);

        return returnProcess;
    }

    /**
     * Valida que el repositorio exista.
     */
    private void validateRepository(
            ReturnRepository returnRepository
    ) {
        if (returnRepository == null) {
            throw new IllegalArgumentException(
                    "Return repository must not be null."
            );
        }
    }

    /**
     * Valida que la solicitud sea realizada por un comprador.
     */
    private void validateBuyer(Buyer buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Return must be requested by a buyer."
            );
        }
    }

    /**
     * Verifica que la cuenta del comprador esté activa.
     *
     * No utilizamos canPurchase(), porque un comprador suspendido
     * comercialmente podría conservar el derecho de devolver una
     * compra anterior. Solamente exigimos que su usuario esté activo.
     */
    private void validateActiveBuyer(Buyer buyer) {
        if (!buyer.isActive()) {
            throw new IllegalStateException(
                    "Only an active buyer can request returns."
            );
        }
    }
}