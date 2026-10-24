package application.adapters.input.rest.controllers;

import application.adapters.input.rest.dto.InventoryAdjustmentRequest;
import application.adapters.input.rest.dto.InventoryConditionRequest;
import application.adapters.input.rest.dto.InventoryMovementResponse;
import application.adapters.input.rest.dto.InventoryQuantityRequest;
import application.adapters.input.rest.dto.InventoryResponse;
import application.adapters.input.rest.dto.RegisterInventoryRequest;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.ports.input.InventoryManagementUseCase;
import application.ports.input.InventoryMovementQueryUseCase;
import application.ports.input.InventoryQueryUseCase;
import application.ports.input.RegisterInventoryUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST encargado de registrar, consultar
 * y administrar los inventarios de NexusMarket.
 *
 * La identificación del usuario solicitante se recibe
 * temporalmente mediante el encabezado:
 *
 * X-Requester-Identification
 *
 * El encabezado representa al usuario que ejecuta la operación.
 * La autenticación técnica mediante tokens se podrá incorporar
 * posteriormente sin modificar las reglas del dominio.
 */
@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

    private static final String REQUESTER_HEADER =
            "X-Requester-Identification";

    private final RegisterInventoryUseCase
            registerInventoryUseCase;

    private final InventoryManagementUseCase
            inventoryManagementUseCase;

    private final InventoryQueryUseCase
            inventoryQueryUseCase;

    private final InventoryMovementQueryUseCase
            inventoryMovementQueryUseCase;

    /**
     * Construye el controlador con los casos
     * de uso necesarios.
     *
     * @param registerInventoryUseCase caso de uso para registrar inventario
     * @param inventoryManagementUseCase caso de uso para administrarlo
     * @param inventoryQueryUseCase caso de uso para consultar inventarios
     * @param inventoryMovementQueryUseCase caso de uso para consultar movimientos
     */
    public InventoryController(
            RegisterInventoryUseCase registerInventoryUseCase,
            InventoryManagementUseCase inventoryManagementUseCase,
            InventoryQueryUseCase inventoryQueryUseCase,
            InventoryMovementQueryUseCase inventoryMovementQueryUseCase
    ) {
        validateRegisterInventoryUseCase(
                registerInventoryUseCase
        );

        validateInventoryManagementUseCase(
                inventoryManagementUseCase
        );

        validateInventoryQueryUseCase(
                inventoryQueryUseCase
        );

        validateMovementQueryUseCase(
                inventoryMovementQueryUseCase
        );

        this.registerInventoryUseCase =
                registerInventoryUseCase;

        this.inventoryManagementUseCase =
                inventoryManagementUseCase;

        this.inventoryQueryUseCase =
                inventoryQueryUseCase;

        this.inventoryMovementQueryUseCase =
                inventoryMovementQueryUseCase;
    }

    /**
     * Registra el inventario inicial de un producto físico.
     *
     * Solamente un vendedor autorizado o un operador
     * logístico activo puede realizar esta operación.
     *
     * @param requesterIdentification identificación del usuario
     * @param request datos del inventario
     * @return inventario registrado
     */
    @PostMapping
    public ResponseEntity<InventoryResponse> register(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @Valid
            @RequestBody
            RegisterInventoryRequest request
    ) {
        Inventory inventory =
                registerInventoryUseCase.register(
                        requesterIdentification,
                        request.productId(),
                        request.warehouseId(),
                        request.initialQuantity(),
                        request.condition()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        InventoryResponse.from(inventory)
                );
    }

    /**
     * Obtiene los inventarios visibles para el usuario.
     *
     * @param requesterIdentification identificación del solicitante
     * @return inventarios autorizados
     */
    @GetMapping
    public ResponseEntity<List<InventoryResponse>>
    findAccessibleInventory(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification
    ) {
        List<InventoryResponse> response =
                inventoryQueryUseCase
                        .findAccessibleInventory(
                                requesterIdentification
                        )
                        .stream()
                        .map(InventoryResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Consulta los movimientos históricos visibles
     * para el usuario.
     *
     * @param requesterIdentification identificación del solicitante
     * @return movimientos autorizados
     */
    @GetMapping("/movements")
    public ResponseEntity<List<InventoryMovementResponse>>
    findAccessibleMovements(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification
    ) {
        List<InventoryMovementResponse> response =
                inventoryMovementQueryUseCase
                        .findAccessibleMovements(
                                requesterIdentification
                        )
                        .stream()
                        .map(InventoryMovementResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Registra el ingreso de unidades.
     *
     * @param requesterIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param request cantidad que ingresa
     * @return movimiento generado
     */
    @PostMapping("/{inventoryId}/entries")
    public ResponseEntity<InventoryMovementResponse>
    registerEntry(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @PathVariable
            String inventoryId,

            @Valid
            @RequestBody
            InventoryQuantityRequest request
    ) {
        InventoryMovement movement =
                inventoryManagementUseCase
                        .registerEntry(
                                requesterIdentification,
                                inventoryId,
                                request.quantity()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        InventoryMovementResponse.from(
                                movement
                        )
                );
    }

    /**
     * Reserva unidades disponibles.
     *
     * @param requesterIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param request cantidad que se reserva
     * @return movimiento generado
     */
    @PostMapping("/{inventoryId}/reservations")
    public ResponseEntity<InventoryMovementResponse>
    reserveQuantity(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @PathVariable
            String inventoryId,

            @Valid
            @RequestBody
            InventoryQuantityRequest request
    ) {
        InventoryMovement movement =
                inventoryManagementUseCase
                        .reserveQuantity(
                                requesterIdentification,
                                inventoryId,
                                request.quantity()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        InventoryMovementResponse.from(
                                movement
                        )
                );
    }

    /**
     * Ajusta la cantidad disponible del inventario.
     *
     * @param requesterIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param request nueva cantidad disponible
     * @return movimiento generado
     */
    @PatchMapping("/{inventoryId}/quantity")
    public ResponseEntity<InventoryMovementResponse>
    adjustQuantity(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @PathVariable
            String inventoryId,

            @Valid
            @RequestBody
            InventoryAdjustmentRequest request
    ) {
        InventoryMovement movement =
                inventoryManagementUseCase
                        .adjustQuantity(
                                requesterIdentification,
                                inventoryId,
                                request.newQuantity()
                        );

        return ResponseEntity.ok(
                InventoryMovementResponse.from(
                        movement
                )
        );
    }

    /**
     * Registra la devolución de unidades al inventario.
     *
     * @param requesterIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param request cantidad devuelta
     * @return movimiento generado
     */
    @PostMapping("/{inventoryId}/returns")
    public ResponseEntity<InventoryMovementResponse>
    registerReturn(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @PathVariable
            String inventoryId,

            @Valid
            @RequestBody
            InventoryQuantityRequest request
    ) {
        InventoryMovement movement =
                inventoryManagementUseCase
                        .registerReturn(
                                requesterIdentification,
                                inventoryId,
                                request.quantity()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        InventoryMovementResponse.from(
                                movement
                        )
                );
    }

    /**
     * Cambia la condición operativa del inventario.
     *
     * La respuesta utiliza el estado HTTP 204 porque la operación
     * se completa correctamente, pero no devuelve contenido.
     *
     * @param requesterIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param request nueva condición
     * @return respuesta sin contenido
     */
    @PatchMapping("/{inventoryId}/condition")
    public ResponseEntity<Void> changeCondition(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @PathVariable
            String inventoryId,

            @Valid
            @RequestBody
            InventoryConditionRequest request
    ) {
        inventoryManagementUseCase.changeCondition(
                requesterIdentification,
                inventoryId,
                request.condition()
        );

        return ResponseEntity.noContent().build();
    }

    /**
     * Valida el caso de uso para registrar inventario.
     */
    private void validateRegisterInventoryUseCase(
            RegisterInventoryUseCase useCase
    ) {
        if (useCase == null) {
            throw new IllegalArgumentException(
                    "Register inventory use case must not be null."
            );
        }
    }

    /**
     * Valida el caso de uso para administrar inventario.
     */
    private void validateInventoryManagementUseCase(
            InventoryManagementUseCase useCase
    ) {
        if (useCase == null) {
            throw new IllegalArgumentException(
                    "Inventory management use case must not be null."
            );
        }
    }

    /**
     * Valida el caso de uso para consultar inventario.
     */
    private void validateInventoryQueryUseCase(
            InventoryQueryUseCase useCase
    ) {
        if (useCase == null) {
            throw new IllegalArgumentException(
                    "Inventory query use case must not be null."
            );
        }
    }

    /**
     * Valida el caso de uso para consultar movimientos.
     */
    private void validateMovementQueryUseCase(
            InventoryMovementQueryUseCase useCase
    ) {
        if (useCase == null) {
            throw new IllegalArgumentException(
                    "Inventory movement query use case must not be null."
            );
        }
    }
}