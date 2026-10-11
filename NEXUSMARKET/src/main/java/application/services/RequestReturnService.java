package application.services;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Return;
import application.ports.input.RequestReturnUseCase;
import application.ports.output.ReturnRepository;

/**
 * Servicio de aplicación encargado de registrar
 * las solicitudes de devolución.
 *
 * Implementa el puerto de entrada RequestReturnUseCase,
 * permitiendo solicitar una devolución sin depender
 * directamente de esta implementación.
 *
 * Un comprador activo solamente puede solicitar
 * la devolución de un pedido que le pertenece
 * y que fue entregado o finalizado.
 */
public class RequestReturnService
        implements RequestReturnUseCase {

    private final ReturnRepository returnRepository;

    /**
     * Construye el servicio con el repositorio
     * de devoluciones.
     *
     * @param returnRepository repositorio utilizado
     *                         para guardar devoluciones
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
     * El modelo Return valida que el pedido pertenezca
     * al comprador y que tenga un estado válido
     * para ser devuelto.
     *
     * @param buyer comprador que solicita la devolución
     * @param order pedido que se desea devolver
     * @return devolución registrada
     */
    @Override
    public Return request(
            Buyer buyer,
            Order order
    ) {
        validateBuyer(buyer);
        validateActiveBuyer(buyer);

        /*
         * Return comprueba que:
         *
         * - El pedido exista.
         * - El pedido pertenezca al comprador.
         * - El pedido haya sido entregado o finalizado.
         */
        Return returnProcess =
                new Return(order, buyer);

        /*
         * Se almacena la solicitud mediante
         * el puerto de salida.
         */
        returnRepository.save(returnProcess);

        return returnProcess;
    }

    /**
     * Valida que el repositorio exista.
     *
     * @param returnRepository repositorio de devoluciones
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
     * Valida que la solicitud sea realizada
     * por un comprador.
     *
     * @param buyer comprador que realiza la solicitud
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
     * No utilizamos canPurchase(), porque un comprador
     * suspendido comercialmente podría conservar el derecho
     * de devolver una compra anterior.
     *
     * Solamente se exige que su usuario esté activo.
     *
     * @param buyer comprador que realiza la solicitud
     */
    private void validateActiveBuyer(Buyer buyer) {
        if (!buyer.isActive()) {
            throw new IllegalStateException(
                    "Only an active buyer can request returns."
            );
        }
    }
}