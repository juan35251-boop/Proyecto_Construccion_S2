package application.services;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.LogisticsOperator;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.InventoryMovementType;
import application.ports.output.InventoryMovementRepository;
import application.ports.output.InventoryRepository;

/**
 * Servicio de aplicación encargado de registrar
 * el inventario inicial de un producto.
 *
 * También comprueba que cada usuario solamente administre
 * las bodegas y productos correspondientes a su responsabilidad.
 */
public class RegisterInventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;

    /**
     * Construye el servicio con los repositorios necesarios.
     *
     * @param inventoryRepository repositorio de inventarios
     * @param movementRepository repositorio de movimientos
     */
    public RegisterInventoryService(
            InventoryRepository inventoryRepository,
            InventoryMovementRepository movementRepository
    ) {
        validateRepositories(
                inventoryRepository,
                movementRepository
        );

        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
    }

    /**
     * Registra el inventario de un producto en una bodega.
     *
     * @param performedBy usuario que realiza el registro
     * @param product producto almacenado
     * @param warehouse bodega donde se encuentra
     * @param initialQuantity cantidad inicial
     * @param condition condición inicial del inventario
     * @return inventario registrado
     */
    public Inventory register(
            User performedBy,
            Product product,
            Warehouse warehouse,
            int initialQuantity,
            InventoryCondition condition
    ) {
        validateRequiredData(product, warehouse);
        validateUserAccess(
                performedBy,
                product,
                warehouse
        );

        validatePhysicalProduct(product);

        /*
         * Inventory valida que la cantidad no sea negativa
         * y que la condición no sea nula.
         */
        Inventory inventory = new Inventory(
                product,
                warehouse,
                initialQuantity,
                condition
        );

        inventoryRepository.save(inventory);

        /*
         * Si existen unidades iniciales, se registra su ingreso.
         * Un inventario iniciado en cero no genera movimiento.
         */
        if (initialQuantity > 0) {
            InventoryMovement movement =
                    new InventoryMovement(
                            inventory,
                            InventoryMovementType.ENTRY,
                            initialQuantity,
                            performedBy
                    );

            movementRepository.save(movement);
        }

        return inventory;
    }

    /**
     * Valida que existan los repositorios requeridos.
     */
    private void validateRepositories(
            InventoryRepository inventoryRepository,
            InventoryMovementRepository movementRepository
    ) {
        if (inventoryRepository == null) {
            throw new IllegalArgumentException(
                    "Inventory repository must not be null."
            );
        }

        if (movementRepository == null) {
            throw new IllegalArgumentException(
                    "Inventory movement repository must not be null."
            );
        }
    }

    /**
     * Valida los objetos requeridos antes de comprobar permisos.
     */
    private void validateRequiredData(
            Product product,
            Warehouse warehouse
    ) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Inventory product must not be null."
            );
        }

        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Inventory warehouse must not be null."
            );
        }
    }

    /**
     * Comprueba que el usuario esté activo y tenga acceso
     * al producto y a la bodega seleccionada.
     */
    private void validateUserAccess(
            User user,
            Product product,
            Warehouse warehouse
    ) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "Inventory registration requires a user."
            );
        }

        if (!user.isActive()) {
            throw new IllegalStateException(
                    "Only active users can register inventory."
            );
        }

        if (user instanceof Seller seller) {
            validateSellerAccess(
                    seller,
                    product,
                    warehouse
            );
            return;
        }

        if (user instanceof LogisticsOperator) {
            validateLogisticsAccess(warehouse);
            return;
        }

        throw new IllegalStateException(
                "User is not authorized to register inventory."
        );
    }

    /**
     * Comprueba que el vendedor administre tanto el producto
     * como la bodega donde registra sus existencias.
     */
    private void validateSellerAccess(
            Seller seller,
            Product product,
            Warehouse warehouse
    ) {
        if (!seller.managesProduct(product)) {
            throw new IllegalStateException(
                    "Seller can only register inventory for their own products."
            );
        }

        if (!seller.managesWarehouse(warehouse)) {
            throw new IllegalStateException(
                    "Seller can only register inventory in their own warehouses."
            );
        }
    }

    /**
     * Comprueba que el operador logístico utilice
     * una bodega perteneciente al Marketplace.
     */
    private void validateLogisticsAccess(
            Warehouse warehouse
    ) {
        if (!warehouse.isMarketplaceWarehouse()) {
            throw new IllegalStateException(
                    "Logistics operator can only manage marketplace warehouses."
            );
        }
    }

    /**
     * Comprueba que el inventario corresponda
     * a un producto físico.
     */
    private void validatePhysicalProduct(Product product) {
        if (!product.isPhysical()) {
            throw new IllegalStateException(
                    "Digital products do not require inventory."
            );
        }
    }
}