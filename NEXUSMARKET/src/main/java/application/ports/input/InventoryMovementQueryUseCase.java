package application.ports.input;

import application.domain.models.InventoryMovement;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar movimientos de inventario.
 *
 * Los movimientos representan la evidencia histórica
 * de ingresos, reservas, salidas, ajustes y devoluciones.
 *
 * La información visible depende del rol, los productos
 * y las bodegas administradas por el usuario solicitante.
 */
public interface InventoryMovementQueryUseCase {

    /**
     * Obtiene los movimientos visibles utilizando
     * la identificación del usuario solicitante.
     *
     * Esta operación está diseñada para ser utilizada
     * desde los controladores REST.
     *
     * @param requesterIdentification identificación del solicitante
     * @return movimientos que el usuario puede consultar
     */
    List<InventoryMovement> findAccessibleMovements(
            String requesterIdentification
    );

    /**
     * Obtiene los movimientos visibles utilizando
     * un usuario previamente localizado.
     *
     * Esta operación se conserva para mantener compatibilidad
     * con las pruebas unitarias y componentes existentes.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return movimientos que el usuario puede consultar
     */
    List<InventoryMovement> findAccessibleMovements(
            User requestedBy
    );
}