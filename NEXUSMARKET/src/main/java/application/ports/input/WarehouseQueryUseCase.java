package application.ports.input;

import application.domain.models.Warehouse;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar bodegas.
 *
 * Las bodegas visibles dependen del rol y de las
 * responsabilidades del usuario solicitante.
 */
public interface WarehouseQueryUseCase {

    /**
     * Obtiene las bodegas visibles para el usuario
     * identificado.
     *
     * @param requesterIdentification identificación
     *        del usuario que realiza la consulta
     * @return lista de bodegas autorizadas
     */
    List<Warehouse> findAccessibleWarehouses(
            String requesterIdentification
    );
}