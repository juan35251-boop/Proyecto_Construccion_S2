package application.ports.output;

import application.domain.models.Shipment;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida utilizado para consultar envíos.
 */
public interface ShipmentQueryRepository {

    /**
     * Busca un envío mediante su identificador.
     *
     * @param shipmentId identificador del envío
     * @return envío encontrado o vacío
     */
    default Optional<Shipment> findById(
            String shipmentId
    ) {
        return Optional.empty();
    }

    /**
     * Busca el envío asociado a un pedido.
     *
     * @param orderId identificador del pedido
     * @return envío encontrado o vacío
     */
    default Optional<Shipment> findByOrderId(
            String orderId
    ) {
        return Optional.empty();
    }

    /**
     * Obtiene todos los envíos registrados.
     *
     * @return lista de envíos
     */
    List<Shipment> findAll();
}