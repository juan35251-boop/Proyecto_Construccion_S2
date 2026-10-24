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
     * Obtiene los inventarios visibles utilizando
     * la identificación del usuario solicitante.
     *
     * Esta operación está diseñada para ser utilizada
     * desde los controladores REST.
     *
     * @param requesterIdentification identificación del solicitante
     * @return inventarios que el usuario puede consultar
     */
    List<Inventory> findAccessibleInventory(
            String requesterIdentification
    );

    /**
     * Obtiene los inventarios visibles utilizando
     * un usuario previamente localizado.
     *
     * Esta operación se conserva para mantener compatibilidad
     * con las pruebas y componentes existentes.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return inventarios que el usuario puede consultar
     */
    List<Inventory> findAccessibleInventory(
            User requestedBy
    );
}