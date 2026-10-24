package application.adapters.input.rest.controllers;

import application.ports.input.AssociateWarehouseUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encargado de asociar bodegas
 * registradas con vendedores.
 */
@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseAssociationController {

    private final AssociateWarehouseUseCase
            associateWarehouseUseCase;

    public WarehouseAssociationController(
            AssociateWarehouseUseCase associateWarehouseUseCase
    ) {
        if (associateWarehouseUseCase == null) {
            throw new IllegalArgumentException(
                    "Associate warehouse use case must not be null."
            );
        }

        this.associateWarehouseUseCase =
                associateWarehouseUseCase;
    }

    /**
     * Asocia una bodega existente con un vendedor.
     *
     * Solamente un administrador activo puede realizar
     * esta operación.
     *
     * @param administratorIdentification administrador responsable
     * @param warehouseId identificador de la bodega
     * @param sellerIdentification identificación del vendedor
     * @return respuesta sin contenido
     */
    @PostMapping(
            "/{warehouseId}/sellers/{sellerIdentification}"
    )
    public ResponseEntity<Void> associate(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String administratorIdentification,

            @PathVariable
            String warehouseId,

            @PathVariable
            String sellerIdentification
    ) {
        associateWarehouseUseCase.associate(
                administratorIdentification,
                sellerIdentification,
                warehouseId
        );

        return ResponseEntity.noContent().build();
    }
}