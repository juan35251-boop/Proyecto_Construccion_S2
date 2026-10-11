package application.services;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.LogisticsOperator;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;
import application.ports.input.InventoryManagementUseCase;
import application.ports.output.InventoryMovementRepository;
import application.ports.output.InventoryRepository;

/**
 * Servicio de aplicación encargado de gestionar el inventario.
 *
 * Implementa el puerto de entrada InventoryManagementUseCase,
 * permitiendo administrar el inventario sin depender
 * directamente de esta implementación.
 *
 * Coordina los cambios en las cantidades disponibles,
 * comprueba los permisos del usuario y registra los
 * movimientos que sirven como evidencia.
 */
public class InventoryManagementService
        implements InventoryManagementUseCase {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;

    /**
     * Construye el servicio con los repositorios necesarios.
     *
     * @param inventoryRepository repositorio del inventario
     * @param movementRepository repositorio de movimientos
     */
    public InventoryManagementService(
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
     * Registra el ingreso de nuevas unidades al inventario.
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
        InventoryMovement movement = createMovement(
                performedBy,
                inventory,
                InventoryMovementType.ENTRY,
                quantity
        );

        inventory.addQuantity(quantity);

        saveOperation(
                inventory,
                movement
        );

        return movement;
    }

    /**
     * Reserva unidades disponibles del inventario.
     *
     * Inventory impide reservar existencias dañadas
     * o cantidades superiores a las disponibles.
     *
     * @param performedBy usuario que realiza la reserva
     * @param inventory inventario que será afectado
     * @param quantity cantidad que se desea reservar
     * @return movimiento generado
     */
    @Override
    public InventoryMovement reserveQuantity(
            User performedBy,
            Inventory inventory,
            int quantity
    ) {
        InventoryMovement movement = createMovement(
                performedBy,
                inventory,
                InventoryMovementType.RESERVATION,
                quantity
        );

        inventory.reserveQuantity(quantity);

        saveOperation(
                inventory,
                movement
        );

        return movement;
    }

    /**
     * Ajusta la cantidad disponible a un nuevo valor.
     *
     * El movimiento almacena la diferencia entre la cantidad
     * anterior y la nueva cantidad.
     *
     * No se registra un ajuste si ambas cantidades son iguales.
     *
     * @param performedBy usuario que realiza el ajuste
     * @param inventory inventario que será ajustado
     * @param newQuantity nueva cantidad disponible
     * @return movimiento generado
     */
    @Override
    public InventoryMovement adjustQuantity(
            User performedBy,
            Inventory inventory,
            int newQuantity
    ) {
        validateInventory(inventory);
        validateNonNegativeQuantity(newQuantity);

        int previousQuantity =
                inventory.getAvailableQuantity();

        if (previousQuantity == newQuantity) {
            throw new IllegalStateException(
                    "Inventory adjustment must change the quantity."
            );
        }

        int adjustedQuantity =
                Math.abs(previousQuantity - newQuantity);

        InventoryMovement movement = createMovement(
                performedBy,
                inventory,
                InventoryMovementType.ADJUSTMENT,
                adjustedQuantity
        );

        inventory.adjustQuantity(newQuantity);

        saveOperation(
                inventory,
                movement
        );

        return movement;
    }

    /**
     * Registra la devolución de unidades al inventario.
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
        InventoryMovement movement = createMovement(
                performedBy,
                inventory,
                InventoryMovementType.RETURN,
                quantity
        );

        inventory.addQuantity(quantity);

        saveOperation(
                inventory,
                movement
        );

        return movement;
    }

    /**
     * Cambia la condición operativa del inventario.
     *
     * Permite marcarlo, por ejemplo, como disponible
     * o dañado.
     *
     * Esta operación no modifica cantidades y, por lo tanto,
     * no genera un movimiento de inventario.
     *
     * @param performedBy usuario que realiza el cambio
     * @param inventory inventario que será modificado
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

        inventory.changeCondition(newCondition);
        inventoryRepository.save(inventory);
    }

    /**
     * Crea un movimiento después de verificar
     * que el usuario tenga permiso sobre el
     * inventario específico.
     *
     * @param performedBy usuario responsable
     * @param inventory inventario afectado
     * @param movementType tipo de movimiento
     * @param quantity cantidad involucrada
     * @return movimiento creado
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
     * Guarda el inventario actualizado y su movimiento.
     *
     * Cuando se implemente la infraestructura, ambas
     * operaciones deberán ejecutarse dentro de una
     * misma transacción.
     *
     * @param inventory inventario actualizado
     * @param movement movimiento generado
     */
    private void saveOperation(
            Inventory inventory,
            InventoryMovement movement
    ) {
        inventoryRepository.save(inventory);
        movementRepository.save(movement);
    }

    /**
     * Valida que los repositorios requeridos estén disponibles.
     *
     * @param inventoryRepository repositorio de inventarios
     * @param movementRepository repositorio de movimientos
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
     * Valida que el inventario exista.
     *
     * @param inventory inventario que será validado
     */
    private void validateInventory(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException(
                    "Inventory must not be null."
            );
        }
    }

    /**
     * Valida que la nueva cantidad no sea negativa.
     *
     * @param quantity cantidad que será validada
     */
    private void validateNonNegativeQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException(
                    "Inventory quantity must not be negative."
            );
        }
    }

    /**
     * Comprueba que el usuario tenga permiso para modificar
     * el inventario específico.
     *
     * Un vendedor solamente puede administrar sus propios
     * productos y bodegas.
     *
     * Un operador logístico solamente puede administrar
     * bodegas del Marketplace.
     *
     * @param user usuario que realiza la operación
     * @param inventory inventario administrado
     */
    private void validateInventoryAccess(
            User user,
            Inventory inventory
    ) {
        validateAuthorizedUser(user);
        validateInventory(inventory);

        if (user instanceof Seller seller) {
            if (!seller.managesProduct(
                    inventory.getProduct()
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
     * Comprueba que el usuario esté activo y pertenezca
     * a uno de los roles autorizados para gestionar inventario.
     *
     * @param user usuario que realiza la operación
     */
    private void validateAuthorizedUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "Inventory operation must be performed by a user."
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
}