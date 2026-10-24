package application.adapters.input.rest.controllers;

import application.adapters.input.rest.dto.RegisterSellerRequest;
import application.adapters.input.rest.dto.SellerResponse;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.input.RegisterSellerUseCase;
import application.ports.input.SellerQueryUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST encargado de las operaciones
 * relacionadas con vendedores.
 */
@RestController
@RequestMapping("/api/v1/sellers")
public class SellerController {

    private final RegisterSellerUseCase
            registerSellerUseCase;

    private final SellerQueryUseCase
            sellerQueryUseCase;

    /**
     * Construye el controlador con los casos de uso requeridos.
     *
     * @param registerSellerUseCase caso de uso para registrar vendedores
     * @param sellerQueryUseCase caso de uso para consultar vendedores
     */
    public SellerController(
            RegisterSellerUseCase registerSellerUseCase,
            SellerQueryUseCase sellerQueryUseCase
    ) {
        if (registerSellerUseCase == null) {
            throw new IllegalArgumentException(
                    "Register seller use case must not be null."
            );
        }

        if (sellerQueryUseCase == null) {
            throw new IllegalArgumentException(
                    "Seller query use case must not be null."
            );
        }

        this.registerSellerUseCase =
                registerSellerUseCase;

        this.sellerQueryUseCase =
                sellerQueryUseCase;
    }

    /**
     * Registra un nuevo vendedor.
     *
     * Solamente un administrador activo puede ejecutar
     * esta operación.
     *
     * @param administratorIdentification identificación del administrador
     * @param request información del vendedor
     * @return vendedor registrado
     */
    @PostMapping
    public ResponseEntity<SellerResponse> register(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String administratorIdentification,

            @Valid
            @RequestBody
            RegisterSellerRequest request
    ) {
        Warehouse firstWarehouse =
                new Warehouse(
                        WarehouseOwnerType.SELLER
                );

        Seller seller =
                registerSellerUseCase.register(
                        administratorIdentification,
                        request.identification(),
                        request.fullName(),
                        request.email(),
                        request.status(),
                        firstWarehouse
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        SellerResponse.from(seller)
                );
    }

    /**
     * Consulta un vendedor mediante su identificación.
     *
     * Un vendedor puede consultar su propia información.
     * Los administradores y supervisores pueden consultar
     * cualquier vendedor.
     *
     * @param requesterIdentification identificación del solicitante
     * @param identification identificación del vendedor
     * @return vendedor encontrado
     */
    @GetMapping("/{identification}")
    public ResponseEntity<SellerResponse>
    findByIdentification(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String requesterIdentification,

            @PathVariable
            String identification
    ) {
        Seller seller =
                sellerQueryUseCase
                        .findByIdentification(
                                requesterIdentification,
                                identification
                        );

        return ResponseEntity.ok(
                SellerResponse.from(seller)
        );
    }

    /**
     * Consulta todos los vendedores registrados.
     *
     * Solamente los administradores y supervisores activos
     * pueden ejecutar esta operación.
     *
     * @param requesterIdentification identificación del solicitante
     * @return lista de vendedores
     */
    @GetMapping
    public ResponseEntity<List<SellerResponse>>
    findAll(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String requesterIdentification
    ) {
        List<SellerResponse> sellers =
                sellerQueryUseCase
                        .findAll(
                                requesterIdentification
                        )
                        .stream()
                        .map(SellerResponse::from)
                        .toList();

        return ResponseEntity.ok(sellers);
    }
}