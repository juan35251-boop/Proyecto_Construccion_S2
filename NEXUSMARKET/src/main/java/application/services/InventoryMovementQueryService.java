package application.services;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.InventoryMovementQueryUseCase;
import application.ports.output.InventoryMovementQueryRepository;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio de aplicación encargado de consultar
 * movimientos históricos de inventario.
 *
 * Los movimientos pueden almacenarse en MongoDB porque
 * representan evidencia histórica y registros de auditoría.
 *
 * Permisos:
 *
 * - Administrador: consulta todos los movimientos.
 * - Supervisor: consulta todos los movimientos.
 * - Vendedor: consulta movimientos de sus productos
 *   almacenados en sus bodegas.
 * - Operador logístico: consulta movimientos realizados
 *   sobre bodegas del Marketplace.
 * - Comprador: no puede consultar movimientos.
 */
public class InventoryMovementQueryService
        implements InventoryMovementQueryUseCase {

    private final InventoryMovementQueryRepository
            movementQueryRepository;

    private final UserRepository userRepository;

    /**
     * Construye el servicio completo para ser utilizado
     * desde la API mediante identificadores.
     *
     * @param movementQueryRepository repositorio de consulta
     *                                de movimientos
     * @param userRepository repositorio de usuarios
     */
    public InventoryMovementQueryService(
            InventoryMovementQueryRepository movementQueryRepository,
            UserRepository userRepository
    ) {
        validateMovementRepository(
                movementQueryRepository
        );

        validateUserRepository(userRepository);

        this.movementQueryRepository =
                movementQueryRepository;

        this.userRepository =
                userRepository;
    }

    /**
     * Constructor conservado para mantener compatibilidad
     * con las pruebas unitarias existentes.
     *
     * Este constructor permite utilizar la operación
     * que recibe directamente un usuario.
     *
     * @param movementQueryRepository repositorio de consulta
     *                                de movimientos
     */
    public InventoryMovementQueryService(
            InventoryMovementQueryRepository movementQueryRepository
    ) {
        validateMovementRepository(
                movementQueryRepository
        );

        this.movementQueryRepository =
                movementQueryRepository;

        this.userRepository = null;
    }

    /**
     * Consulta movimientos utilizando la identificación
     * del usuario solicitante.
     *
     * @param requesterIdentification identificación del solicitante
     * @return movimientos autorizados
     */
    @Override
    public List<InventoryMovement> findAccessibleMovements(
            String requesterIdentification
    ) {
        validateApiRepository();

        User requestedBy =
                findUser(requesterIdentification);

        return findAccessibleMovements(requestedBy);
    }

    /**
     * Obtiene los movimientos visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return movimientos autorizados
     */
    @Override
    public List<InventoryMovement> findAccessibleMovements(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<InventoryMovement> movements =
                movementQueryRepository.findAll();

        /*
         * Administradores y supervisores pueden consultar
         * todo el historial de movimientos.
         */
        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(movements);
        }

        /*
         * Los vendedores solamente pueden consultar movimientos
         * correspondientes a sus productos y sus bodegas.
         */
        if (requestedBy instanceof Seller seller) {
            return movements.stream()
                    .filter(movement ->
                            isAccessibleForSeller(
                                    seller,
                                    movement
                            )
                    )
                    .toList();
        }

        /*
         * Los operadores logísticos solamente pueden consultar
         * movimientos de bodegas pertenecientes al Marketplace.
         */
        if (requestedBy.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return movements.stream()
                    .filter(
                            this::isMarketplaceMovement
                    )
                    .toList();
        }

        /*
         * Los compradores no administran inventarios
         * y no pueden consultar sus movimientos.
         */
        throw new IllegalStateException(
                "User is not authorized to query inventory movements."
        );
    }

    /**
     * Comprueba si un movimiento pertenece a un producto
     * y una bodega administrados por el vendedor.
     *
     * Los productos recuperados desde MySQL conservan
     * la identificación de su vendedor propietario.
     *
     * Para productos antiguos sin propietario persistido,
     * se conserva la validación mediante managesProduct.
     *
     * @param seller vendedor solicitante
     * @param movement movimiento consultado
     * @return true si el vendedor puede consultarlo
     */
    private boolean isAccessibleForSeller(
            Seller seller,
            InventoryMovement movement
    ) {
        if (movement == null
                || movement.getInventory() == null) {
            return false;
        }

        Inventory inventory =
                movement.getInventory();

        Product product =
                inventory.getProduct();

        boolean managesProduct =
                sellerManagesProduct(
                        seller,
                        product
                );

        boolean managesWarehouse =
                inventory.getWarehouse() != null
                        && seller.managesWarehouse(
                                inventory.getWarehouse()
                        );

        return managesProduct
                && managesWarehouse;
    }

    /**
     * Determina si un producto pertenece al vendedor.
     *
     * La identificación persistida en el producto es la
     * fuente principal para comprobar la propiedad.
     *
     * La lista interna del vendedor se utiliza como respaldo
     * para mantener compatibilidad con productos antiguos
     * creados sin propietario explícito.
     *
     * @param seller vendedor consultado
     * @param product producto relacionado con el movimiento
     * @return true si el producto pertenece al vendedor
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
     * Comprueba si el movimiento corresponde
     * a una bodega del Marketplace.
     *
     * @param movement movimiento consultado
     * @return true si pertenece al Marketplace
     */
    private boolean isMarketplaceMovement(
            InventoryMovement movement
    ) {
        return movement != null
                && movement.getInventory() != null
                && movement.getInventory()
                        .getWarehouse() != null
                && movement.getInventory()
                        .getWarehouse()
                        .isMarketplaceWarehouse();
    }

    /**
     * Localiza al usuario solicitante.
     *
     * @param identification identificación del usuario
     * @return usuario encontrado
     */
    private User findUser(
            String identification
    ) {
        validateText(
                identification,
                "Requester identification"
        );

        return userRepository
                .findByIdentification(identification)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Requesting user was not found."
                        )
                );
    }

    /**
     * Valida que el usuario exista y se encuentre activo.
     *
     * @param requestedBy usuario solicitante
     */
    private void validateActiveRequester(
            User requestedBy
    ) {
        if (requestedBy == null) {
            throw new IllegalArgumentException(
                    "Requesting user must not be null."
            );
        }

        if (!requestedBy.isActive()) {
            throw new IllegalStateException(
                    "Only active users can query inventory movements."
            );
        }
    }

    /**
     * Determina si el usuario tiene acceso global
     * al historial de movimientos.
     *
     * @param user usuario solicitante
     * @return true si es administrador o supervisor
     */
    private boolean hasGlobalAccess(
            User user
    ) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }

    /**
     * Valida el repositorio de movimientos.
     *
     * @param movementQueryRepository repositorio validado
     */
    private void validateMovementRepository(
            InventoryMovementQueryRepository movementQueryRepository
    ) {
        if (movementQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Inventory movement query repository must not be null."
            );
        }
    }

    /**
     * Valida el repositorio de usuarios.
     *
     * @param userRepository repositorio validado
     */
    private void validateUserRepository(
            UserRepository userRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }
    }

    /**
     * Impide utilizar identificadores cuando el servicio
     * fue construido con el constructor anterior.
     */
    private void validateApiRepository() {
        if (userRepository == null) {
            throw new IllegalStateException(
                    "User lookup repository is not configured."
            );
        }
    }

    /**
     * Valida un texto obligatorio.
     *
     * @param value valor validado
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