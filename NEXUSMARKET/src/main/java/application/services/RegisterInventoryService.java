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
import application.domain.valueobjects.SystemRole;
import application.ports.input.RegisterInventoryUseCase;
import application.ports.output.InventoryMovementRepository;
import application.ports.output.InventoryRepository;
import application.ports.output.ProductQueryRepository;
import application.ports.output.UserRepository;
import application.ports.output.WarehouseRepository;

/**
 * Servicio encargado de registrar el inventario inicial
 * de productos físicos.
 *
 * Resuelve los objetos mediante sus identificadores,
 * verifica los permisos y conserva el estado actual en
 * MySQL y el movimiento histórico en MongoDB.
 */
public class RegisterInventoryService
        implements RegisterInventoryUseCase {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;
    private final UserRepository userRepository;
    private final ProductQueryRepository productQueryRepository;
    private final WarehouseRepository warehouseRepository;

    /**
     * Constructor completo utilizado por la API.
     */
    public RegisterInventoryService(
            InventoryRepository inventoryRepository,
            InventoryMovementRepository movementRepository,
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository,
            WarehouseRepository warehouseRepository
    ) {
        validatePersistenceRepositories(
                inventoryRepository,
                movementRepository
        );

        validateLookupRepositories(
                userRepository,
                productQueryRepository,
                warehouseRepository
        );

        this.inventoryRepository =
                inventoryRepository;
        this.movementRepository =
                movementRepository;
        this.userRepository =
                userRepository;
        this.productQueryRepository =
                productQueryRepository;
        this.warehouseRepository =
                warehouseRepository;
    }

    /**
     * Constructor conservado para operaciones y pruebas
     * que utilizan objetos del dominio directamente.
     */
    public RegisterInventoryService(
            InventoryRepository inventoryRepository,
            InventoryMovementRepository movementRepository
    ) {
        validatePersistenceRepositories(
                inventoryRepository,
                movementRepository
        );

        this.inventoryRepository =
                inventoryRepository;
        this.movementRepository =
                movementRepository;

        this.userRepository = null;
        this.productQueryRepository = null;
        this.warehouseRepository = null;
    }

    /**
     * Registra inventario mediante identificadores.
     */
    @Override
    public Inventory register(
            String performedByIdentification,
            String productId,
            String warehouseId,
            int initialQuantity,
            InventoryCondition condition
    ) {
        validateApiRepositories();

        User performedBy =
                findUser(performedByIdentification);

        Product product =
                findProduct(productId);

        Warehouse warehouse =
                findWarehouse(warehouseId);

        return register(
                performedBy,
                product,
                warehouse,
                initialQuantity,
                condition
        );
    }

    /**
     * Registra inventario utilizando objetos
     * previamente resueltos.
     */
    @Override
    public Inventory register(
            User performedBy,
            Product product,
            Warehouse warehouse,
            int initialQuantity,
            InventoryCondition condition
    ) {
        validateAuthorizedUser(performedBy);
        validateProduct(product);
        validateWarehouse(warehouse);
        validateInitialQuantity(initialQuantity);
        validateCondition(condition);
        validatePhysicalProduct(product);

        validateInventoryAccess(
                performedBy,
                product,
                warehouse
        );

        Inventory inventory =
                new Inventory(
                        product,
                        warehouse,
                        initialQuantity,
                        condition
                );

        inventoryRepository.save(inventory);

        /*
         * Una cantidad inicial positiva representa
         * un ingreso de unidades y se registra como
         * evidencia histórica en MongoDB.
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

    private User findUser(
            String identification
    ) {
        validateText(
                identification,
                "User identification"
        );

        return userRepository
                .findByIdentification(identification)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User was not found."
                        )
                );
    }

    private Product findProduct(
            String productId
    ) {
        validateText(
                productId,
                "Product identifier"
        );

        return productQueryRepository
                .findById(productId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Product was not found."
                        )
                );
    }

    private Warehouse findWarehouse(
            String warehouseId
    ) {
        validateText(
                warehouseId,
                "Warehouse identifier"
        );

        return warehouseRepository
                .findById(warehouseId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Warehouse was not found."
                        )
                );
    }

    private void validatePersistenceRepositories(
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

    private void validateLookupRepositories(
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository,
            WarehouseRepository warehouseRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }

        if (productQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Product query repository must not be null."
            );
        }

        if (warehouseRepository == null) {
            throw new IllegalArgumentException(
                    "Warehouse repository must not be null."
            );
        }
    }

    private void validateApiRepositories() {
        if (userRepository == null
                || productQueryRepository == null
                || warehouseRepository == null) {
            throw new IllegalStateException(
                    "Identifier lookup repositories are not configured."
            );
        }
    }

    private void validateAuthorizedUser(
            User user
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

        boolean isSeller =
                user.getRole() == SystemRole.SELLER;

        boolean isLogisticsOperator =
                user.getRole()
                        == SystemRole.LOGISTICS_OPERATOR;

        if (!isSeller && !isLogisticsOperator) {
            throw new IllegalStateException(
                    "User is not authorized to register inventory."
            );
        }
    }

    private void validateProduct(
            Product product
    ) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Inventory must be associated with a product."
            );
        }
    }

    private void validateWarehouse(
            Warehouse warehouse
    ) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Inventory must be associated with a warehouse."
            );
        }
    }

    private void validateInitialQuantity(
            int initialQuantity
    ) {
        if (initialQuantity < 0) {
            throw new IllegalArgumentException(
                    "Initial inventory quantity must not be negative."
            );
        }
    }

    private void validateCondition(
            InventoryCondition condition
    ) {
        if (condition == null) {
            throw new IllegalArgumentException(
                    "Inventory condition must not be null."
            );
        }
    }

    private void validatePhysicalProduct(
            Product product
    ) {
        if (!product.isPhysical()) {
            throw new IllegalStateException(
                    "Digital products do not require inventory."
            );
        }
    }

    /**
     * Verifica el alcance operativo del usuario.
     */
    private void validateInventoryAccess(
            User user,
            Product product,
            Warehouse warehouse
    ) {
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
        }
    }

    /**
     * Valida que el producto y la bodega pertenezcan
     * al vendedor.
     *
     * Para productos persistidos, sellerIdentification
     * representa la fuente principal de propiedad.
     * El método managesProduct se conserva como respaldo
     * para productos antiguos sin propietario explícito.
     */
    private void validateSellerAccess(
            Seller seller,
            Product product,
            Warehouse warehouse
    ) {
        boolean ownsProduct;

        if (product.getSellerIdentification() != null) {
            ownsProduct =
                    product.belongsToSeller(
                            seller.getIdentification()
                    );
        } else {
            ownsProduct =
                    seller.managesProduct(product);
        }

        if (!ownsProduct) {
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

    private void validateLogisticsAccess(
            Warehouse warehouse
    ) {
        if (!warehouse.isMarketplaceWarehouse()) {
            throw new IllegalStateException(
                    "Logistics operator can only manage marketplace warehouses."
            );
        }
    }

    private void validateText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }
}