package application.ports.input;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.Warehouse;

/**
 * Puerto de entrada para asociar bodegas registradas
 * con vendedores.
 *
 * Solamente un administrador activo puede ejecutar
 * esta operación.
 */
public interface AssociateWarehouseUseCase {

    /**
     * Asocia objetos del dominio ya localizados.
     *
     * @param associatedBy administrador responsable
     * @param seller vendedor que administrará la bodega
     * @param warehouse bodega registrada
     */
    void associate(
            Administrator associatedBy,
            Seller seller,
            Warehouse warehouse
    );

    /**
     * Localiza y asocia los objetos mediante
     * sus identificadores.
     *
     * Esta operación es utilizada por la API REST.
     *
     * @param administratorIdentification administrador responsable
     * @param sellerIdentification vendedor seleccionado
     * @param warehouseId bodega seleccionada
     */
    void associate(
            String administratorIdentification,
            String sellerIdentification,
            String warehouseId
    );
}