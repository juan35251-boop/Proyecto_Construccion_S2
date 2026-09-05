package application.ports.output;

import application.domain.models.Refund;

/**
 * Puerto de salida encargado de almacenar
 * los reembolsos procesados.
 */
public interface RefundRepository {

    /**
     * Guarda un reembolso.
     *
     * @param refund reembolso que se desea almacenar
     */
    void save(Refund refund);
}