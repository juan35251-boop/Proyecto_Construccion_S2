package application.services;

import application.domain.models.Administrator;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.input.RegisterWarehouseUseCase;
import application.ports.output.WarehouseRepository;

/**
 * Servicio de aplicación encargado de registrar bodegas.
 *
 * Implementa el puerto de entrada RegisterWarehouseUseCase,
 * permitiendo que un futuro controlador REST solicite el
 * registro sin depender directamente de esta implementación.
 *
 * Según las responsabilidades definidas para NexusMarket,
 * solamente un administrador activo puede crear bodegas.
 */
public class RegisterWarehouseService
        implements RegisterWarehouseUseCase {

    private final WarehouseRepository warehouseRepository;

    /**
     * Construye el servicio con el repositorio de bodegas.
     *
     * El servicio recibe el puerto de salida y no conoce
     * la tecnología utilizada para almacenar la información.
     *
     * @param warehouseRepository repositorio utilizado
     *                            para guardar bodegas
     */
    public RegisterWarehouseService(
            WarehouseRepository warehouseRepository
    ) {
        validateRepository(warehouseRepository);
        this.warehouseRepository = warehouseRepository;
    }

    /**
     * Registra una nueva bodega.
     *
     * Antes de crearla, valida que la operación sea realizada
     * por un administrador activo y que se haya indicado
     * correctamente el tipo de propietario.
     *
     * @param registeredBy administrador que realiza el registro
     * @param ownerType tipo de propietario de la bodega
     * @return bodega registrada
     */
    @Override
    public Warehouse register(
            Administrator registeredBy,
            WarehouseOwnerType ownerType
    ) {
        validateAdministrator(registeredBy);
        validateOwnerType(ownerType);

        /*
         * Warehouse valida nuevamente que el tipo
         * de propietario no sea nulo.
         */
        Warehouse warehouse = new Warehouse(ownerType);

        /*
         * El servicio solicita conservar la bodega mediante
         * el puerto de salida, sin conocer la base de datos.
         */
        warehouseRepository.save(warehouse);

        return warehouse;
    }

    /**
     * Valida que el repositorio requerido exista.
     *
     * @param warehouseRepository repositorio de bodegas
     */
    private void validateRepository(
            WarehouseRepository warehouseRepository
    ) {
        if (warehouseRepository == null) {
            throw new IllegalArgumentException(
                    "Warehouse repository must not be null."
            );
        }
    }

    /**
     * Valida que la operación sea realizada
     * por un administrador activo.
     *
     * @param administrator administrador que realiza la operación
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
     * Valida que se indique el tipo de propietario
     * operativo de la bodega.
     *
     * @param ownerType tipo de propietario
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