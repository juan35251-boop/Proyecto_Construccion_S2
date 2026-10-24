package application.adapters.input.rest.controllers;

import application.adapters.input.rest.dto.RegisterWarehouseRequest;
import application.adapters.input.rest.dto.WarehouseResponse;
import application.domain.models.Warehouse;
import application.ports.input.RegisterWarehouseUseCase;
import application.ports.input.WarehouseQueryUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST encargado de las operaciones
 * relacionadas con las bodegas de NexusMarket.
 *
 * Permite registrar bodegas y consultar las bodegas
 * visibles para el usuario solicitante.
 */
@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {

    private final RegisterWarehouseUseCase
            registerWarehouseUseCase;

    private final WarehouseQueryUseCase
            warehouseQueryUseCase;

    /**
     * Construye el controlador con los casos de uso
     * requeridos para administrar bodegas.
     *
     * @param registerWarehouseUseCase caso de uso
     *        para registrar bodegas
     * @param warehouseQueryUseCase caso de uso
     *        para consultar bodegas
     */
    public WarehouseController(
            RegisterWarehouseUseCase registerWarehouseUseCase,
            WarehouseQueryUseCase warehouseQueryUseCase
    ) {
        validateRegisterWarehouseUseCase(
                registerWarehouseUseCase
        );

        validateWarehouseQueryUseCase(
                warehouseQueryUseCase
        );

        this.registerWarehouseUseCase =
                registerWarehouseUseCase;

        this.warehouseQueryUseCase =
                warehouseQueryUseCase;
    }

    /**
     * Registra una nueva bodega.
     *
     * Solamente un administrador activo puede ejecutar
     * esta operación. La identificación del solicitante
     * se recibe mediante un encabezado HTTP.
     *
     * @param administratorIdentification identificación
     *        del administrador solicitante
     * @param request información de la nueva bodega
     * @return bodega registrada
     */
    @PostMapping
    public ResponseEntity<WarehouseResponse> register(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String administratorIdentification,

            @Valid
            @RequestBody
            RegisterWarehouseRequest request
    ) {
        Warehouse warehouse =
                registerWarehouseUseCase.register(
                        administratorIdentification,
                        request.ownerType()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        WarehouseResponse.from(warehouse)
                );
    }

    /**
     * Obtiene las bodegas visibles para el usuario.
     *
     * Los permisos dependen del rol:
     *
     * - Administrador y supervisor: todas las bodegas.
     * - Vendedor: sus bodegas asociadas.
     * - Operador logístico: bodegas del Marketplace.
     * - Comprador: no tiene autorización.
     *
     * @param requesterIdentification identificación
     *        del usuario solicitante
     * @return lista de bodegas autorizadas
     */
    @GetMapping
    public ResponseEntity<List<WarehouseResponse>>
    findAccessibleWarehouses(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String requesterIdentification
    ) {
        List<WarehouseResponse> warehouses =
                warehouseQueryUseCase
                        .findAccessibleWarehouses(
                                requesterIdentification
                        )
                        .stream()
                        .map(WarehouseResponse::from)
                        .toList();

        return ResponseEntity.ok(warehouses);
    }

    /**
     * Valida que exista el caso de uso para registrar bodegas.
     *
     * @param useCase caso de uso recibido
     */
    private void validateRegisterWarehouseUseCase(
            RegisterWarehouseUseCase useCase
    ) {
        if (useCase == null) {
            throw new IllegalArgumentException(
                    "Register warehouse use case must not be null."
            );
        }
    }

    /**
     * Valida que exista el caso de uso para consultar bodegas.
     *
     * @param useCase caso de uso recibido
     */
    private void validateWarehouseQueryUseCase(
            WarehouseQueryUseCase useCase
    ) {
        if (useCase == null) {
            throw new IllegalArgumentException(
                    "Warehouse query use case must not be null."
            );
        }
    }
}