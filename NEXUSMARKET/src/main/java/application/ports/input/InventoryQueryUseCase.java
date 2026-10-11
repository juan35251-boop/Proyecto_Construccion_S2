package application.ports.input;

import application.domain.models.Inventory;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar inventarios.
 *
 * Los inventarios visibles dependen del rol,
 * los productos y las bodegas administradas
 * por el usuario solicitante.
 */
public interface InventoryQueryUseCase {

    /**
     * Obtiene los inventarios visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return lista de inventarios autorizados
     */
    List<Inventory> findAccessibleInventory(
            User requestedBy
    );
}