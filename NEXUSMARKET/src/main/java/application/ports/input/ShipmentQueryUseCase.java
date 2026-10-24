package application.ports.input;

import application.domain.models.Shipment;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada para consultar envíos.
 *
 * Los envíos visibles dependen del rol
 * del usuario solicitante.
 */
public interface ShipmentQueryUseCase {

    /**
     * Consulta los envíos visibles utilizando
     * un usuario del dominio.
     *
     * @param requestedBy usuario solicitante
     * @return envíos autorizados
     */
    List<Shipment> findAccessibleShipments(
            User requestedBy
    );

    /**
     * Consulta los envíos visibles utilizando
     * la identificación del solicitante.
     *
     * @param requesterIdentification identificación del usuario
     * @return envíos autorizados
     */
    List<Shipment>
    findAccessibleShipmentsByRequesterIdentification(
            String requesterIdentification
    );

    /**
     * Consulta un envío específico y comprueba
     * que el solicitante tenga acceso.
     *
     * @param requesterIdentification identificación del usuario
     * @param shipmentId identificador del envío
     * @return envío autorizado
     */
    Shipment findAccessibleShipmentByIdentifiers(
            String requesterIdentification,
            String shipmentId
    );
}