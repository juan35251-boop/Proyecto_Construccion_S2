package application.adapters.input.rest.controllers;

import application.adapters.input.rest.exception.GlobalExceptionHandler;
import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import application.ports.input.CatalogQueryUseCase;
import application.ports.input.ProductCatalogUseCase;
import application.ports.input.RegisterProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Pruebas del controlador REST de productos.
 */
class ProductControllerTest {

    private RegisterProductUseCase
            registerProductUseCase;

    private ProductCatalogUseCase
            productCatalogUseCase;

    private CatalogQueryUseCase
            catalogQueryUseCase;

    private MockMvc mockMvc;

    /**
     * Prepara los casos de uso simulados y el controlador.
     */
    @BeforeEach
    void setUp() {
        registerProductUseCase =
                mock(RegisterProductUseCase.class);

        productCatalogUseCase =
                mock(ProductCatalogUseCase.class);

        catalogQueryUseCase =
                mock(CatalogQueryUseCase.class);

        ProductController controller =
                new ProductController(
                        registerProductUseCase,
                        productCatalogUseCase,
                        catalogQueryUseCase
                );

        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }

    /**
     * Comprueba el registro de un producto.
     */
    @Test
    void shouldRegisterProduct() throws Exception {
        Product product =
                createProduct(
                        "PRODUCT-001",
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                );

        when(registerProductUseCase.register(
                "SELLER-001",
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        )).thenReturn(product);

        String requestBody = """
                {
                    "productType": "PHYSICAL",
                    "status": "PUBLISHED"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/products")
                                .header(
                                        "X-Requester-Identification",
                                        "SELLER-001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId")
                        .value("PRODUCT-001"))
                .andExpect(
                        jsonPath("$.sellerIdentification")
                                .value("SELLER-001")
                )
                .andExpect(jsonPath("$.productType")
                        .value("PHYSICAL"))
                .andExpect(jsonPath("$.status")
                        .value("PUBLISHED"))
                .andExpect(jsonPath("$.variants")
                        .isEmpty());

        verify(registerProductUseCase).register(
                "SELLER-001",
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );
    }

    /**
     * Comprueba la consulta del catálogo.
     */
    @Test
    void shouldFindAccessibleProducts()
            throws Exception {

        Product physicalProduct =
                createProduct(
                        "PRODUCT-001",
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                );

        Product digitalProduct =
                createProduct(
                        "PRODUCT-002",
                        ProductType.DIGITAL,
                        ProductStatus.PUBLISHED
                );

        when(catalogQueryUseCase
                .findAccessibleProducts(
                        "BUYER-001"
                ))
                .thenReturn(
                        List.of(
                                physicalProduct,
                                digitalProduct
                        )
                );

        mockMvc.perform(
                        get("/api/v1/products")
                                .header(
                                        "X-Requester-Identification",
                                        "BUYER-001"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()")
                        .value(2))
                .andExpect(jsonPath("$[0].productId")
                        .value("PRODUCT-001"))
                .andExpect(jsonPath("$[1].productId")
                        .value("PRODUCT-002"));

        verify(catalogQueryUseCase)
                .findAccessibleProducts(
                        "BUYER-001"
                );
    }

    /**
     * Comprueba que un vendedor pueda agregar
     * una variante a su producto.
     */
    @Test
    void shouldAddProductVariant()
            throws Exception {

        Product product =
                createProduct(
                        "PRODUCT-001",
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                );

        ProductVariant variant =
                new ProductVariant("Color negro");

        product.addVariant(variant);

        when(productCatalogUseCase.addVariant(
                "SELLER-001",
                "PRODUCT-001",
                variant
        )).thenReturn(product);

        String requestBody = """
                {
                    "description": "Color negro"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/products/PRODUCT-001/variants"
                        )
                                .header(
                                        "X-Requester-Identification",
                                        "SELLER-001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId")
                        .value("PRODUCT-001"))
                .andExpect(jsonPath("$.variants[0]")
                        .value("Color negro"));

        verify(productCatalogUseCase).addVariant(
                "SELLER-001",
                "PRODUCT-001",
                variant
        );
    }

    /**
     * Comprueba que un vendedor pueda eliminar
     * una variante de su producto.
     */
    @Test
    void shouldRemoveProductVariant()
            throws Exception {

        ProductVariant variant =
                new ProductVariant("Color negro");

        when(productCatalogUseCase.removeVariant(
                "SELLER-001",
                "PRODUCT-001",
                variant
        )).thenReturn(true);

        String requestBody = """
                {
                    "description": "Color negro"
                }
                """;

        mockMvc.perform(
                        delete(
                                "/api/v1/products/PRODUCT-001/variants"
                        )
                                .header(
                                        "X-Requester-Identification",
                                        "SELLER-001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isNoContent());

        verify(productCatalogUseCase)
                .removeVariant(
                        "SELLER-001",
                        "PRODUCT-001",
                        variant
                );
    }

    /**
     * Comprueba el cambio de estado de un producto.
     */
    @Test
    void shouldChangeProductStatus()
            throws Exception {

        Product product =
                createProduct(
                        "PRODUCT-001",
                        ProductType.PHYSICAL,
                        ProductStatus.SUSPENDED
                );

        when(productCatalogUseCase.changeStatus(
                "SELLER-001",
                "PRODUCT-001",
                ProductStatus.SUSPENDED
        )).thenReturn(product);

        String requestBody = """
                {
                    "status": "SUSPENDED"
                }
                """;

        mockMvc.perform(
                        patch(
                                "/api/v1/products/PRODUCT-001/status"
                        )
                                .header(
                                        "X-Requester-Identification",
                                        "SELLER-001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId")
                        .value("PRODUCT-001"))
                .andExpect(jsonPath("$.status")
                        .value("SUSPENDED"));

        verify(productCatalogUseCase)
                .changeStatus(
                        "SELLER-001",
                        "PRODUCT-001",
                        ProductStatus.SUSPENDED
                );
    }

    /**
     * Comprueba la validación de los datos
     * obligatorios del registro.
     */
    @Test
    void shouldRejectInvalidProductRequest()
            throws Exception {

        String requestBody = """
                {
                    "productType": null,
                    "status": null
                }
                """;

        mockMvc.perform(
                        post("/api/v1/products")
                                .header(
                                        "X-Requester-Identification",
                                        "SELLER-001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Request validation failed."
                        ))
                .andExpect(
                        jsonPath(
                                "$.validationErrors.productType"
                        ).exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.status"
                        ).exists()
                );
    }

    /**
     * Comprueba que una variante vacía sea rechazada.
     */
    @Test
    void shouldRejectEmptyVariantDescription()
            throws Exception {

        String requestBody = """
                {
                    "description": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/products/PRODUCT-001/variants"
                        )
                                .header(
                                        "X-Requester-Identification",
                                        "SELLER-001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(
                        jsonPath(
                                "$.validationErrors.description"
                        ).exists()
                );
    }

    /**
     * Comprueba la respuesta cuando el vendedor intenta
     * modificar un producto que no le pertenece.
     */
    @Test
    void shouldReturnConflictWhenSellerIsNotOwner()
            throws Exception {

        ProductVariant variant =
                new ProductVariant("Color negro");

        when(productCatalogUseCase.addVariant(
                "SELLER-002",
                "PRODUCT-001",
                variant
        )).thenThrow(
                new IllegalStateException(
                        "Seller can only manage their own products."
                )
        );

        String requestBody = """
                {
                    "description": "Color negro"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/products/PRODUCT-001/variants"
                        )
                                .header(
                                        "X-Requester-Identification",
                                        "SELLER-002"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.error")
                        .value("Conflict"))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Seller can only manage their own products."
                                )
                );
    }

    /**
     * Crea un producto de prueba perteneciente
     * al vendedor principal.
     */
    private Product createProduct(
            String productId,
            ProductType productType,
            ProductStatus status
    ) {
        return new Product(
                productId,
                "SELLER-001",
                productType,
                status
        );
    }
}