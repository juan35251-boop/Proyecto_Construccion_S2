package application.ports.input;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.Warehouse;

/**
 * Puerto de entrada que define el caso de uso
 * para asociar bodegas con vendedores.
 *
 * Solamente un administrador activo puede realizar
 * esta operación.
 */
public interface AssociateWarehouseUseCase {

    /**
     * Asocia una bodega registrada con un vendedor.
     *
     * La bodega debe estar registrada previamente y
     * debe pertenecer a la categoría SELLER.
     *
     * @param associatedBy administrador que realiza la asociación
     * @param seller vendedor que administrará la bodega
     * @param warehouse bodega que será asociada
     *
     * @throws IllegalArgumentException si algún dato es nulo
     *         o la bodega no pertenece a un vendedor
     * @throws IllegalStateException si el administrador está
     *         inactivo, la bodega no está registrada o ya
     *         se encuentra asociada con el vendedor
     */
    void associate(
            Administrator associatedBy,
            Seller seller,
            Warehouse warehouse
    );
}