package application.ports.input;

import application.domain.models.Administrator;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseOwnerType;

/**
 * Puerto de entrada que define el caso de uso
 * para registrar bodegas en NexusMarket.
 *
 * Solamente un administrador activo puede realizar
 * esta operación.
 */
public interface RegisterWarehouseUseCase {

    /**
     * Registra una nueva bodega.
     *
     * El tipo de propietario permite diferenciar entre
     * una bodega del Marketplace y una bodega perteneciente
     * a un vendedor.
     *
     * @param registeredBy administrador que realiza el registro
     * @param ownerType tipo de propietario de la bodega
     * @return bodega registrada
     *
     * @throws IllegalArgumentException si el administrador
     *         o el tipo de propietario son nulos
     * @throws IllegalStateException si el administrador
     *         no se encuentra activo
     */
    Warehouse register(
            Administrator registeredBy,
            WarehouseOwnerType ownerType
    );
}