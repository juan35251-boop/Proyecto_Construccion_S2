package application.ports.input;

import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseOwnerType;

/**
 * Puerto de entrada que define el caso de uso
 * para registrar bodegas en NexusMarket.
 *
 * El adaptador de entrada entrega la identificación
 * del usuario que solicita la operación.
 */
public interface RegisterWarehouseUseCase {

    /**
     * Registra una nueva bodega.
     *
     * Solamente un administrador activo puede ejecutar
     * esta operación.
     *
     * @param administratorIdentification identificación
     *        del administrador que realiza el registro
     * @param ownerType tipo de propietario de la bodega
     * @return bodega registrada
     */
    Warehouse register(
            String administratorIdentification,
            WarehouseOwnerType ownerType
    );
}