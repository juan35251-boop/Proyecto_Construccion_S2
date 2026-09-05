package application.services;

import application.domain.models.Administrator;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.output.WarehouseRepository;

/**
 * Servicio de aplicación encargado de registrar bodegas.
 *
 * Según las responsabilidades del sistema, solamente un
 * administrador activo puede crear bodegas.
 */
public class RegisterWarehouseService {

    private final WarehouseRepository warehouseRepository;

    /**
     * Construye el servicio con el repositorio de bodegas.
     *
     * @param warehouseRepository repositorio donde se guardan las bodegas
     */
    public RegisterWarehouseService(
            WarehouseRepository warehouseRepository
    ) {
        if (warehouseRepository == null) {
            throw new IllegalArgumentException(
                    "Warehouse repository must not be null."
            );
        }

        this.warehouseRepository = warehouseRepository;
    }

    /**
     * Registra una nueva bodega.
     *
     * @param registeredBy administrador que realiza el registro
     * @param ownerType tipo de propietario de la bodega
     * @return bodega registrada
     */
    public Warehouse register(
            Administrator registeredBy,
            WarehouseOwnerType ownerType
    ) {
        validateAdministrator(registeredBy);
        validateOwnerType(ownerType);

        Warehouse warehouse = new Warehouse(ownerType);
        warehouseRepository.save(warehouse);

        return warehouse;
    }

    /**
     * Valida que la operación sea realizada por un
     * administrador activo.
     */
    private void validateAdministrator(
            Administrator administrator
    ) {
        if (administrator == null) {
            throw new IllegalArgumentException(
                    "Administrator must not be null."
            );
        }

        if (!administrator.isActive()) {
            throw new IllegalStateException(
                    "Only an active administrator can register warehouses."
            );
        }
    }

    /**
     * Valida que se indique quién será el propietario
     * operativo de la bodega.
     */
    private void validateOwnerType(
            WarehouseOwnerType ownerType
    ) {
        if (ownerType == null) {
            throw new IllegalArgumentException(
                    "Warehouse owner type must not be null."
            );
        }
    }
}