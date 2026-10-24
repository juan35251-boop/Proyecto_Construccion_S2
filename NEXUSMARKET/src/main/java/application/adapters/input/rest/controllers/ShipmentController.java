package application.adapters.input.rest.controllers;

import application.adapters.input.rest.dto.CreateShipmentRequest;
import application.adapters.input.rest.dto.ShipmentResponse;
import application.domain.models.Shipment;
import application.ports.input.ShipmentManagementUseCase;
import application.ports.input.ShipmentQueryUseCase;
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
 * Controlador REST encargado de gestionar
 * y consultar envíos físicos.
 *
 * Las operaciones de creación, despacho y entrega
 * solamente pueden ser realizadas por un operador
 * logístico activo.
 */
@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentManagementUseCase
            shipmentManagementUseCase;

    private final ShipmentQueryUseCase
            shipmentQueryUseCase;

    /**
     * Construye el controlador con sus casos de uso.
     *
     * @param shipmentManagementUseCase administración de envíos
     * @param shipmentQueryUseCase consultas de envíos
     */
    public ShipmentController(
            ShipmentManagementUseCase
                    shipmentManagementUseCase,
            ShipmentQueryUseCase
                    shipmentQueryUseCase
    ) {
        validateDependency(
                shipmentManagementUseCase,
                "Shipment management use case"
        );

        validateDependency(
                shipmentQueryUseCase,
                "Shipment query use case"
        );

        this.shipmentManagementUseCase =
                shipmentManagementUseCase;

        this.shipmentQueryUseCase =
                shipmentQueryUseCase;
    }

    /**
     * Crea un envío para un pedido físico pagado.
     *
     * @param operatorIdentification identificación del operador
     * @param request pedido que será enviado
     * @return envío creado
     */
    @PostMapping
    public ResponseEntity<ShipmentResponse>
    createShipment(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String operatorIdentification,

            @Valid
            @RequestBody
            CreateShipmentRequest request
    ) {
        Shipment shipment =
                shipmentManagementUseCase
                        .createShipmentByIdentifiers(
                                operatorIdentification,
                                request.orderId()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ShipmentResponse.from(
                                shipment
                        )
                );
    }

    /**
     * Consulta los envíos visibles para el solicitante.
     *
     * @param requesterIdentification identificación del usuario
     * @return lista de envíos autorizados
     */
    @GetMapping
    public ResponseEntity<List<ShipmentResponse>>
    findAccessibleShipments(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String requesterIdentification
    ) {
        List<ShipmentResponse> shipments =
                shipmentQueryUseCase
                        .findAccessibleShipmentsByRequesterIdentification(
                                requesterIdentification
                        )
                        .stream()
                        .map(ShipmentResponse::from)
                        .toList();

        return ResponseEntity.ok(shipments);
    }

    /**
     * Consulta un envío específico.
     *
     * @param requesterIdentification identificación del solicitante
     * @param shipmentId identificador del envío
     * @return envío autorizado
     */
    @GetMapping("/{shipmentId}")
    public ResponseEntity<ShipmentResponse>
    findShipmentById(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String requesterIdentification,

            @PathVariable
            String shipmentId
    ) {
        Shipment shipment =
                shipmentQueryUseCase
                        .findAccessibleShipmentByIdentifiers(
                                requesterIdentification,
                                shipmentId
                        );

        return ResponseEntity.ok(
                ShipmentResponse.from(shipment)
        );
    }

    /**
     * Registra el despacho físico del envío.
     *
     * El pedido asociado cambia desde PAID
     * hasta DISPATCHED.
     *
     * @param operatorIdentification identificación del operador
     * @param shipmentId identificador del envío
     * @return envío despachado
     */
    @PatchMapping("/{shipmentId}/dispatch")
    public ResponseEntity<ShipmentResponse>
    dispatchShipment(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String operatorIdentification,

            @PathVariable
            String shipmentId
    ) {
        Shipment shipment =
                shipmentManagementUseCase
                        .dispatchShipmentByIdentifiers(
                                operatorIdentification,
                                shipmentId
                        );

        return ResponseEntity.ok(
                ShipmentResponse.from(shipment)
        );
    }

    /**
     * Confirma la entrega física del envío.
     *
     * El pedido asociado cambia desde DISPATCHED
     * hasta DELIVERED.
     *
     * @param operatorIdentification identificación del operador
     * @param shipmentId identificador del envío
     * @return envío entregado
     */
    @PatchMapping("/{shipmentId}/delivery")
    public ResponseEntity<ShipmentResponse>
    confirmDelivery(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String operatorIdentification,

            @PathVariable
            String shipmentId
    ) {
        Shipment shipment =
                shipmentManagementUseCase
                        .confirmDeliveryByIdentifiers(
                                operatorIdentification,
                                shipmentId
                        );

        return ResponseEntity.ok(
                ShipmentResponse.from(shipment)
        );
    }

    /**
     * Valida una dependencia requerida
     * por el controlador.
     */
    private void validateDependency(
            Object dependency,
            String dependencyName
    ) {
        if (dependency == null) {
            throw new IllegalArgumentException(
                    dependencyName + " must not be null."
            );
        }
    }
}