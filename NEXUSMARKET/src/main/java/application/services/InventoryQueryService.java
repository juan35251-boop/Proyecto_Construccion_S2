package application.services;

import application.domain.models.Inventory;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.InventoryQueryUseCase;
import application.ports.output.InventoryQueryRepository;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio de aplicación encargado de consultar inventarios.
 *
 * Implementa el puerto de entrada InventoryQueryUseCase
 * y aplica las restricciones de acceso según el rol
 * del usuario solicitante.
 *
 * Permisos:
 *
 * - Administrador: puede consultar todos los inventarios.
 * - Supervisor: puede consultar todos los inventarios.
 * - Vendedor: puede consultar inventarios de sus productos
 *   almacenados en sus propias bodegas.
 * - Operador logístico: puede consultar inventarios ubicados
 *   en bodegas pertenecientes al Marketplace.
 * - Comprador: no puede consultar inventarios.
 */
public class InventoryQueryService
        implements InventoryQueryUseCase {

    private final InventoryQueryRepository
            inventoryQueryRepository;

    private final UserRepository userRepository;

    /**
     * Construye el servicio completo para ser utilizado
     * desde la API mediante identificadores.
     *
     * @param inventoryQueryRepository repositorio
     *                                 para consultar inventarios
     * @param userRepository repositorio para localizar usuarios
     */
    public InventoryQueryService(
            InventoryQueryRepository inventoryQueryRepository,
            UserRepository userRepository
    ) {
        validateInventoryQueryRepository(
                inventoryQueryRepository
        );

        validateUserRepository(userRepository);

        this.inventoryQueryRepository =
                inventoryQueryRepository;

        this.userRepository =
                userRepository;
    }

    /**
     * Constructor conservado para mantener compatibilidad
     * con las pruebas unitarias existentes.
     *
     * Este constructor permite utilizar la operación
     * que recibe un usuario completo.
     *
     * @param inventoryQueryRepository repositorio
     *                                 para consultar inventarios
     */
    public InventoryQueryService(
            InventoryQueryRepository inventoryQueryRepository
    ) {
        validateInventoryQueryRepository(
                inventoryQueryRepository
        );

        this.inventoryQueryRepository =
                inventoryQueryRepository;

        this.userRepository = null;
    }

    /**
     * Consulta los inventarios mediante la identificación
     * del usuario solicitante.
     *
     * @param requesterIdentification identificación del solicitante
     * @return inventarios autorizados
     */
    @Override
    public List<Inventory> findAccessibleInventory(
            String requesterIdentification
    ) {
        validateApiRepository();

        User requestedBy =
                findUser(requesterIdentification);

        return findAccessibleInventory(requestedBy);
    }

    /**
     * Obtiene los inventarios visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return inventarios autorizados
     */
    @Override
    public List<Inventory> findAccessibleInventory(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Inventory> inventories =
                inventoryQueryRepository.findAll();

        /*
         * Administradores y supervisores tienen acceso
         * global de lectura.
         */
        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(inventories);
        }

        /*
         * Los vendedores solamente pueden consultar
         * inventarios correspondientes a sus productos
         * y a sus bodegas.
         */
        if (requestedBy instanceof Seller seller) {
            return inventories.stream()
                    .filter(inventory ->
                            seller.managesProduct(
                                    inventory.getProduct()
                            )
                    )
                    .filter(inventory ->
                            seller.managesWarehouse(
                                    inventory.getWarehouse()
                            )
                    )
                    .toList();
        }

        /*
         * Los operadores logísticos solamente pueden consultar
         * inventarios almacenados en bodegas del Marketplace.
         */
        if (requestedBy.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return inventories.stream()
                    .filter(inventory ->
                            inventory.getWarehouse()
                                    .isMarketplaceWarehouse()
                    )
                    .toList();
        }

        /*
         * Los compradores y cualquier otro rol sin autorización
         * no pueden consultar información de inventarios.
         */
        throw new IllegalStateException(
                "User is not authorized to query inventory."
        );
    }

    /**
     * Localiza el usuario solicitante.
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
     * Valida que el usuario exista y esté activo.
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
                    "Only active users can query inventory."
            );
        }
    }

    /**
     * Determina si el usuario posee acceso global.
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
     * Valida el repositorio de inventarios.
     *
     * @param inventoryQueryRepository repositorio validado
     */
    private void validateInventoryQueryRepository(
            InventoryQueryRepository inventoryQueryRepository
    ) {
        if (inventoryQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Inventory query repository must not be null."
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
     * Comprueba que el repositorio necesario para utilizar
     * identificadores haya sido configurado.
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