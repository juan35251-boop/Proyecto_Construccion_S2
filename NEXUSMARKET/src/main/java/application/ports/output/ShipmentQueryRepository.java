package application.ports.output;

import application.domain.models.Shipment;

import java.util.List;

/**
 * Puerto de salida para consultar envíos.
 */
public interface ShipmentQueryRepository {

    /**
     * Obtiene todos los envíos registrados.
     *
     * @return lista de envíos
     */
    List<Shipment> findAll();
}