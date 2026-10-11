package application.ports.input;

import application.domain.models.Shipment;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar envíos.
 *
 * Los envíos visibles dependen del rol y la
 * relación del usuario con los pedidos.
 */
public interface ShipmentQueryUseCase {

    /**
     * Obtiene los envíos visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return lista de envíos autorizados
     */
    List<Shipment> findAccessibleShipments(
            User requestedBy
    );
}
