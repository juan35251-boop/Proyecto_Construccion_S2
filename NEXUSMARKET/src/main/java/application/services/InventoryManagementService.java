package application.services;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.LogisticsOperator;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;
import application.ports.input.InventoryManagementUseCase;
import application.ports.output.InventoryMovementRepository;
import application.ports.output.InventoryQueryRepository;
import application.ports.output.InventoryRepository;
import application.ports.output.UserRepository;

/**
 * Servicio de aplicación encargado de administrar inventarios.
 *
 * Permite registrar entradas, reservas, ajustes y devoluciones,
 * además de modificar la condición operativa del inventario.
 *
 * Cada operación que cambia una cantidad genera un movimiento
 * que sirve como evidencia histórica.
 */
public class InventoryManagementService
        implements InventoryManagementUseCase {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;
    private final UserRepository userRepository;
    private final InventoryQueryRepository inventoryQueryRepository;

    /**
     * Construye el servicio completo para ser utilizado
     * desde la API mediante identificadores.
     *
     * @param inventoryRepository repositorio de inventarios
     * @param movementRepository repositorio de movimientos
     * @param userRepository repositorio de usuarios
     * @param inventoryQueryRepository repositorio para consultar inventarios
     */
    public InventoryManagementService(
            InventoryRepository inventoryRepository,
            InventoryMovementRepository movementRepository,
            UserRepository userRepository,
            InventoryQueryRepository inventoryQueryRepository
    ) {
        validatePersistenceRepositories(
                inventoryRepository,
                movementRepository
        );

        validateLookupRepositories(
                userRepository,
                inventoryQueryRepository
        );

        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
        this.inventoryQueryRepository =
                inventoryQueryRepository;
    }

    /**
     * Constructor conservado para mantener compatibilidad
     * con las pruebas unitarias existentes.
     *
     * Este constructor solamente permite utilizar las operaciones
     * que reciben objetos completos del dominio.
     *
     * @param inventoryRepository repositorio de inventarios
     * @param movementRepository repositorio de movimientos
     */
    public InventoryManagementService(
            InventoryRepository inventoryRepository,
            InventoryMovementRepository movementRepository
    ) {
        validatePersistenceRepositories(
                inventoryRepository,
                movementRepository
        );

        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.userRepository = null;
        this.inventoryQueryRepository = null;
    }

    /**
     * Registra una entrada mediante identificadores.
     *
     * @param performedByIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param quantity cantidad que ingresa
     * @return movimiento generado
     */
    @Override
    public InventoryMovement registerEntry(
            String performedByIdentification,
            String inventoryId,
            int quantity
    ) {
        User performedBy =
                findUser(performedByIdentification);

        Inventory inventory =
                findInventory(inventoryId);

        return registerEntry(
                performedBy,
                inventory,
                quantity
        );
    }

    /**
     * Reserva unidades mediante identificadores.
     *
     * @param performedByIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param quantity cantidad reservada
     * @return movimiento generado
     */
    @Override
    public InventoryMovement reserveQuantity(
            String performedByIdentification,
            String inventoryId,
            int quantity
    ) {
        User performedBy =
                findUser(performedByIdentification);

        Inventory inventory =
                findInventory(inventoryId);

        return reserveQuantity(
                performedBy,
                inventory,
                quantity
        );
    }

    /**
     * Ajusta la cantidad mediante identificadores.
     *
     * @param performedByIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param newQuantity nueva cantidad disponible
     * @return movimiento generado
     */
    @Override
    public InventoryMovement adjustQuantity(
            String performedByIdentification,
            String inventoryId,
            int newQuantity
    ) {
        User performedBy =
                findUser(performedByIdentification);

        Inventory inventory =
                findInventory(inventoryId);

        return adjustQuantity(
                performedBy,
                inventory,
                newQuantity
        );
    }

    /**
     * Registra una devolución mediante identificadores.
     *
     * @param performedByIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param quantity cantidad devuelta
     * @return movimiento generado
     */
    @Override
    public InventoryMovement registerReturn(
            String performedByIdentification,
            String inventoryId,
            int quantity
    ) {
        User performedBy =
                findUser(performedByIdentification);

        Inventory inventory =
                findInventory(inventoryId);

        return registerReturn(
                performedBy,
                inventory,
                quantity
        );
    }

    /**
     * Cambia la condición mediante identificadores.
     *
     * @param performedByIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param newCondition nueva condición
     */
    @Override
    public void changeCondition(
            String performedByIdentification,
            String inventoryId,
            InventoryCondition newCondition
    ) {
        User performedBy =
                findUser(performedByIdentification);

        Inventory inventory =
                findInventory(inventoryId);

        changeCondition(
                performedBy,
                inventory,
                newCondition
        );
    }

    /**
     * Registra el ingreso de nuevas unidades.
     *
     * @param performedBy usuario que realiza la operación
     * @param inventory inventario que recibe las unidades
     * @param quantity cantidad que ingresa
     * @return movimiento generado
     */
    @Override
    public InventoryMovement registerEntry(
            User performedBy,
            Inventory inventory,
            int quantity
    ) {
        InventoryMovement movement =
                createMovement(
                        performedBy,
                        inventory,
                        InventoryMovementType.ENTRY,
                        quantity
                );

        inventory.addQuantity(quantity);
        saveOperation(inventory, movement);

        return movement;
    }

    /**
     * Reserva unidades disponibles.
     *
     * El modelo de dominio impide reservar una cantidad superior
     * a las existencias disponibles o inventario dañado.
     *
     * @param performedBy usuario que realiza la reserva
     * @param inventory inventario afectado
     * @param quantity cantidad que se reserva
     * @return movimiento generado
     */
    @Override
    public InventoryMovement reserveQuantity(
            User performedBy,
            Inventory inventory,
            int quantity
    ) {
        InventoryMovement movement =
                createMovement(
                        performedBy,
                        inventory,
                        InventoryMovementType.RESERVATION,
                        quantity
                );

        inventory.reserveQuantity(quantity);
        saveOperation(inventory, movement);

        return movement;
    }

    /**
     * Ajusta la cantidad disponible.
     *
     * El movimiento almacena la diferencia absoluta entre
     * la cantidad anterior y la nueva cantidad.
     *
     * @param performedBy usuario que realiza el ajuste
     * @param inventory inventario ajustado
     * @param newQuantity nueva cantidad disponible
     * @return movimiento generado
     */
    @Override
    public InventoryMovement adjustQuantity(
            User performedBy,
            Inventory inventory,
            int newQuantity
    ) {
        validateInventoryAccess(
                performedBy,
                inventory
        );

        validateNonNegativeQuantity(newQuantity);

        int previousQuantity =
                inventory.getAvailableQuantity();

        if (previousQuantity == newQuantity) {
            throw new IllegalStateException(
                    "Inventory adjustment must change the quantity."
            );
        }

        int adjustedQuantity =
                Math.abs(
                        previousQuantity - newQuantity
                );

        InventoryMovement movement =
                new InventoryMovement(
                        inventory,
                        InventoryMovementType.ADJUSTMENT,
                        adjustedQuantity,
                        performedBy
                );

        inventory.adjustQuantity(newQuantity);
        saveOperation(inventory, movement);

        return movement;
    }

    /**
     * Registra el retorno de unidades.
     *
     * @param performedBy usuario que procesa la devolución
     * @param inventory inventario que recibe las unidades
     * @param quantity cantidad devuelta
     * @return movimiento generado
     */
    @Override
    public InventoryMovement registerReturn(
            User performedBy,
            Inventory inventory,
            int quantity
    ) {
        InventoryMovement movement =
                createMovement(
                        performedBy,
                        inventory,
                        InventoryMovementType.RETURN,
                        quantity
                );

        inventory.addQuantity(quantity);
        saveOperation(inventory, movement);

        return movement;
    }

    /**
     * Cambia la condición operativa del inventario.
     *
     * @param performedBy usuario que realiza el cambio
     * @param inventory inventario modificado
     * @param newCondition nueva condición
     */
    @Override
    public void changeCondition(
            User performedBy,
            Inventory inventory,
            InventoryCondition newCondition
    ) {
        validateInventoryAccess(
                performedBy,
                inventory
        );

        if (newCondition == null) {
            throw new IllegalArgumentException(
                    "Inventory condition must not be null."
            );
        }

        inventory.changeCondition(newCondition);
        inventoryRepository.save(inventory);
    }

    /**
     * Crea un movimiento después de validar el acceso.
     *
     * @param performedBy usuario que realiza la operación
     * @param inventory inventario afectado
     * @param movementType tipo de movimiento
     * @param quantity cantidad involucrada
     * @return movimiento generado
     */
    private InventoryMovement createMovement(
            User performedBy,
            Inventory inventory,
            InventoryMovementType movementType,
            int quantity
    ) {
        validateInventoryAccess(
                performedBy,
                inventory
        );

        return new InventoryMovement(
                inventory,
                movementType,
                quantity,
                performedBy
        );
    }

    /**
     * Guarda el inventario actualizado en MySQL
     * y el movimiento histórico en MongoDB.
     *
     * @param inventory inventario actualizado
     * @param movement movimiento histórico
     */
    private void saveOperation(
            Inventory inventory,
            InventoryMovement movement
    ) {
        inventoryRepository.save(inventory);
        movementRepository.save(movement);
    }

    /**
     * Busca un usuario mediante su identificación.
     *
     * @param identification identificación del usuario
     * @return usuario encontrado
     */
    private User findUser(
            String identification
    ) {
        validateApiRepositories();

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

    /**
     * Busca un inventario mediante su identificador.
     *
     * @param inventoryId identificador del inventario
     * @return inventario encontrado
     */
    private Inventory findInventory(
            String inventoryId
    ) {
        validateApiRepositories();

        validateText(
                inventoryId,
                "Inventory identifier"
        );

        return inventoryQueryRepository
                .findById(inventoryId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Inventory was not found."
                        )
                );
    }

    /**
     * Valida que los repositorios de almacenamiento existan.
     *
     * @param inventoryRepository repositorio de inventarios
     * @param movementRepository repositorio de movimientos
     */
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

    /**
     * Valida los repositorios utilizados para resolver identificadores.
     *
     * @param userRepository repositorio de usuarios
     * @param inventoryQueryRepository repositorio de inventarios
     */
    private void validateLookupRepositories(
            UserRepository userRepository,
            InventoryQueryRepository inventoryQueryRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }

        if (inventoryQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Inventory query repository must not be null."
            );
        }
    }

    /**
     * Impide utilizar las operaciones por identificadores
     * cuando se construyó el servicio con el constructor anterior.
     */
    private void validateApiRepositories() {
        if (userRepository == null
                || inventoryQueryRepository == null) {
            throw new IllegalStateException(
                    "Identifier lookup repositories are not configured."
            );
        }
    }

    /**
     * Comprueba que el usuario pueda administrar inventarios.
     *
     * @param user usuario que realiza la operación
     */
    private void validateAuthorizedUser(
            User user
    ) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "Inventory operation requires a user."
            );
        }

        if (!user.isActive()) {
            throw new IllegalStateException(
                    "Only active users can manage inventory."
            );
        }

        boolean isSeller =
                user.getRole() == SystemRole.SELLER;

        boolean isLogisticsOperator =
                user.getRole()
                        == SystemRole.LOGISTICS_OPERATOR;

        if (!isSeller && !isLogisticsOperator) {
            throw new IllegalStateException(
                    "User is not authorized to manage inventory."
            );
        }
    }

    /**
     * Valida que el inventario exista.
     *
     * @param inventory inventario que será validado
     */
    private void validateInventory(
            Inventory inventory
    ) {
        if (inventory == null) {
            throw new IllegalArgumentException(
                    "Inventory must not be null."
            );
        }
    }

    /**
     * Comprueba que el usuario tenga acceso
     * al inventario específico.
     *
     * Un vendedor puede administrar un inventario cuando
     * el producto pertenece a su identificación y la bodega
     * se encuentra asociada con él.
     *
     * Para productos antiguos sin propietario persistido,
     * se conserva la validación mediante managesProduct.
     *
     * @param user usuario que realiza la operación
     * @param inventory inventario afectado
     */
    private void validateInventoryAccess(
            User user,
            Inventory inventory
    ) {
        validateAuthorizedUser(user);
        validateInventory(inventory);

        if (user instanceof Seller seller) {
            Product product =
                    inventory.getProduct();

            if (!sellerManagesProduct(
                    seller,
                    product
            )) {
                throw new IllegalStateException(
                        "Seller can only manage inventory for their own products."
                );
            }

            if (!seller.managesWarehouse(
                    inventory.getWarehouse()
            )) {
                throw new IllegalStateException(
                        "Seller can only manage inventory in their own warehouses."
                );
            }

            return;
        }

        if (user instanceof LogisticsOperator
                && !inventory.getWarehouse()
                        .isMarketplaceWarehouse()) {
            throw new IllegalStateException(
                    "Logistics operator can only manage marketplace inventory."
            );
        }
    }

    /**
     * Determina si el producto pertenece al vendedor.
     *
     * Los productos recuperados desde MySQL conservan
     * sellerIdentification, por lo que la autorización
     * se comprueba mediante esa identificación.
     *
     * El uso de managesProduct se conserva únicamente
     * para productos antiguos creados sin propietario.
     *
     * @param seller vendedor que realiza la operación
     * @param product producto relacionado con el inventario
     * @return true si el vendedor administra el producto
     */
    private boolean sellerManagesProduct(
            Seller seller,
            Product product
    ) {
        if (product == null) {
            return false;
        }

        if (product.getSellerIdentification() != null) {
            return product.belongsToSeller(
                    seller.getIdentification()
            );
        }

        return seller.managesProduct(product);
    }

    /**
     * Valida que una cantidad no sea negativa.
     *
     * @param quantity cantidad que será validada
     */
    private void validateNonNegativeQuantity(
            int quantity
    ) {
        if (quantity < 0) {
            throw new IllegalArgumentException(
                    "Inventory quantity must not be negative."
            );
        }
    }

    /**
     * Valida un texto obligatorio.
     *
     * @param value texto que será validado
     * @param fieldName nombre del campo
     */
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