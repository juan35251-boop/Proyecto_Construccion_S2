package application.ports.output;

import application.domain.models.Shipment;

/**
 * Puerto de salida encargado de almacenar los envíos.
 *
 * Su implementación concreta podrá conectarse posteriormente
 * con una base de datos.
 */
public interface ShipmentRepository {

    /**
     * Guarda o actualiza un envío.
     *
     * @param shipment envío que se desea almacenar
     */
    void save(Shipment shipment);
}