package application.ports.output;

import application.domain.models.Refund;

import java.util.List;

/**
 * Puerto de salida para consultar reembolsos.
 */
public interface RefundQueryRepository {

    /**
     * Obtiene todos los reembolsos registrados.
     *
     * @return lista de reembolsos
     */
    List<Refund> findAll();
}