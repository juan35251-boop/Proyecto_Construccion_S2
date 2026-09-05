package application.ports.output;

import application.domain.models.Return;

/**
 * Puerto de salida encargado de almacenar
 * las solicitudes de devolución.
 */
public interface ReturnRepository {

    /**
     * Guarda una solicitud de devolución.
     *
     * @param returnProcess devolución que se desea almacenar
     */
    void save(Return returnProcess);
}