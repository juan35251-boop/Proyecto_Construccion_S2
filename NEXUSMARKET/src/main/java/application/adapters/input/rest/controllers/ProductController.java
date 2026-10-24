package application.adapters.input.rest.controllers;

import application.adapters.input.rest.dto.ChangeProductStatusRequest;
import application.adapters.input.rest.dto.ProductResponse;
import application.adapters.input.rest.dto.ProductVariantRequest;
import application.adapters.input.rest.dto.RegisterProductRequest;
import application.domain.models.Product;
import application.domain.valueobjects.ProductVariant;
import application.ports.input.CatalogQueryUseCase;
import application.ports.input.ProductCatalogUseCase;
import application.ports.input.RegisterProductUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * Controlador REST encargado de registrar,
 * consultar y administrar productos.
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final RegisterProductUseCase
            registerProductUseCase;

    private final ProductCatalogUseCase
            productCatalogUseCase;

    private final CatalogQueryUseCase
            catalogQueryUseCase;

    /**
     * Construye el controlador con sus casos de uso.
     */
    public ProductController(
            RegisterProductUseCase registerProductUseCase,
            ProductCatalogUseCase productCatalogUseCase,
            CatalogQueryUseCase catalogQueryUseCase
    ) {
        validateUseCases(
                registerProductUseCase,
                productCatalogUseCase,
                catalogQueryUseCase
        );

        this.registerProductUseCase =
                registerProductUseCase;
        this.productCatalogUseCase =
                productCatalogUseCase;
        this.catalogQueryUseCase =
                catalogQueryUseCase;
    }

    /**
     * Registra un producto para el vendedor solicitante.
     */
    @PostMapping
    public ResponseEntity<ProductResponse> register(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String sellerIdentification,

            @Valid
            @RequestBody
            RegisterProductRequest request
    ) {
        Product product =
                registerProductUseCase.register(
                        sellerIdentification,
                        request.productType(),
                        request.status()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ProductResponse.from(product));
    }

    /**
     * Consulta los productos visibles para el usuario.
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>>
    findAccessibleProducts(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String requesterIdentification
    ) {
        List<ProductResponse> products =
                catalogQueryUseCase
                        .findAccessibleProducts(
                                requesterIdentification
                        )
                        .stream()
                        .map(ProductResponse::from)
                        .toList();

        return ResponseEntity.ok(products);
    }

    /**
     * Agrega una variante al producto.
     */
    @PostMapping("/{productId}/variants")
    public ResponseEntity<ProductResponse> addVariant(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String sellerIdentification,

            @PathVariable
            String productId,

            @Valid
            @RequestBody
            ProductVariantRequest request
    ) {
        Product product =
                productCatalogUseCase.addVariant(
                        sellerIdentification,
                        productId,
                        new ProductVariant(
                                request.description()
                        )
                );

        return ResponseEntity.ok(
                ProductResponse.from(product)
        );
    }

    /**
     * Elimina una variante del producto.
     *
     * La operación es idempotente: si la variante no existe,
     * el estado final continúa siendo correcto.
     */
    @DeleteMapping("/{productId}/variants")
    public ResponseEntity<Void> removeVariant(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String sellerIdentification,

            @PathVariable
            String productId,

            @Valid
            @RequestBody
            ProductVariantRequest request
    ) {
        productCatalogUseCase.removeVariant(
                sellerIdentification,
                productId,
                new ProductVariant(
                        request.description()
                )
        );

        return ResponseEntity.noContent().build();
    }

    /**
     * Cambia el estado de un producto.
     */
    @PatchMapping("/{productId}/status")
    public ResponseEntity<ProductResponse> changeStatus(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String sellerIdentification,

            @PathVariable
            String productId,

            @Valid
            @RequestBody
            ChangeProductStatusRequest request
    ) {
        Product product =
                productCatalogUseCase.changeStatus(
                        sellerIdentification,
                        productId,
                        request.status()
                );

        return ResponseEntity.ok(
                ProductResponse.from(product)
        );
    }

    /**
     * Valida que el controlador reciba
     * todos los casos de uso requeridos.
     */
    private void validateUseCases(
            RegisterProductUseCase registerUseCase,
            ProductCatalogUseCase catalogUseCase,
            CatalogQueryUseCase queryUseCase
    ) {
        if (registerUseCase == null) {
            throw new IllegalArgumentException(
                    "Register product use case must not be null."
            );
        }

        if (catalogUseCase == null) {
            throw new IllegalArgumentException(
                    "Product catalog use case must not be null."
            );
        }

        if (queryUseCase == null) {
            throw new IllegalArgumentException(
                    "Catalog query use case must not be null."
            );
        }
    }
}