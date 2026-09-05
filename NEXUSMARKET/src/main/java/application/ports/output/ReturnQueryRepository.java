package application.ports.output;

import application.domain.models.Return;

import java.util.List;

/**
 * Puerto de salida para consultar devoluciones.
 */
public interface ReturnQueryRepository {

    /**
     * Obtiene todas las devoluciones registradas.
     *
     * @return lista de devoluciones
     */
    List<Return> findAll();
}