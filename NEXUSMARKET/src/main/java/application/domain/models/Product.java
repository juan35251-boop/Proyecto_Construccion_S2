package application.domain.models;

import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa un producto ofrecido dentro de NexusMarket.
 *
 * Cada producto posee un identificador único, un vendedor
 * propietario, un tipo, un estado y una lista de variantes.
 */
public class Product {

    private final String productId;
    private final String sellerIdentification;
    private final ProductType productType;
    private final List<ProductVariant> variants;
    private ProductStatus status;

    /**
     * Crea un producto sin propietario explícito.
     *
     * Este constructor se conserva para compatibilidad con
     * las pruebas y operaciones internas existentes.
     *
     * @param productType tipo del producto
     * @param status estado inicial
     */
    public Product(
            ProductType productType,
            ProductStatus status
    ) {
        this(
                UUID.randomUUID().toString(),
                null,
                productType,
                status
        );
    }

    /**
     * Reconstruye un producto sin propietario explícito.
     *
     * @param productId identificador del producto
     * @param productType tipo del producto
     * @param status estado del producto
     */
    public Product(
            String productId,
            ProductType productType,
            ProductStatus status
    ) {
        this(
                productId,
                null,
                productType,
                status
        );
    }

    /**
     * Reconstruye un producto con toda su información.
     *
     * @param productId identificador del producto
     * @param sellerIdentification identificación del vendedor
     * @param productType tipo del producto
     * @param status estado del producto
     */
    public Product(
            String productId,
            String sellerIdentification,
            ProductType productType,
            ProductStatus status
    ) {
        validateProductId(productId);
        validateOptionalSellerIdentification(
                sellerIdentification
        );
        validateProductType(productType);
        validateStatus(status);

        this.productId = productId;
        this.sellerIdentification =
                normalizeOptionalText(
                        sellerIdentification
                );
        this.productType = productType;
        this.status = status;
        this.variants = new ArrayList<>();
    }

    /**
     * Crea un producto perteneciente a un vendedor.
     *
     * @param sellerIdentification identificación del vendedor
     * @param productType tipo del producto
     * @param status estado inicial
     * @return producto nuevo
     */
    public static Product createForSeller(
            String sellerIdentification,
            ProductType productType,
            ProductStatus status
    ) {
        validateRequiredText(
                sellerIdentification,
                "Seller identification"
        );

        return new Product(
                UUID.randomUUID().toString(),
                sellerIdentification,
                productType,
                status
        );
    }

    public String getProductId() {
        return productId;
    }

    public String getSellerIdentification() {
        return sellerIdentification;
    }

    public ProductType getProductType() {
        return productType;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public List<ProductVariant> getVariants() {
        return List.copyOf(variants);
    }

    /**
     * Determina si el producto pertenece al vendedor indicado.
     *
     * La comparación se hace por identificación y no por
     * referencia de memoria.
     *
     * @param identification identificación del vendedor
     * @return true si el vendedor es propietario
     */
    public boolean belongsToSeller(
            String identification
    ) {
        return sellerIdentification != null
                && identification != null
                && sellerIdentification.equals(
                        identification
                );
    }

    public void changeStatus(ProductStatus newStatus) {
        validateStatus(newStatus);
        this.status = newStatus;
    }

    public void addVariant(ProductVariant variant) {
        validateVariant(variant);

        if (variants.contains(variant)) {
            throw new IllegalStateException(
                    "Product variant already exists."
            );
        }

        variants.add(variant);
    }

    public boolean removeVariant(ProductVariant variant) {
        validateVariant(variant);
        return variants.remove(variant);
    }

    public boolean hasVariant(ProductVariant variant) {
        return variant != null
                && variants.contains(variant);
    }

    public boolean hasVariants() {
        return !variants.isEmpty();
    }

    public boolean isPhysical() {
        return productType == ProductType.PHYSICAL;
    }

    public boolean isDigital() {
        return productType == ProductType.DIGITAL;
    }

    public boolean isPublished() {
        return status == ProductStatus.PUBLISHED;
    }

    private void validateProductId(String productId) {
        validateRequiredText(
                productId,
                "Product identification"
        );
    }

    private void validateOptionalSellerIdentification(
            String sellerIdentification
    ) {
        if (sellerIdentification != null
                && sellerIdentification.isBlank()) {
            throw new IllegalArgumentException(
                    "Seller identification must not be empty."
            );
        }
    }

    private void validateProductType(
            ProductType productType
    ) {
        if (productType == null) {
            throw new IllegalArgumentException(
                    "Product type must not be null."
            );
        }
    }

    private void validateStatus(ProductStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Product status must not be null."
            );
        }
    }

    private void validateVariant(
            ProductVariant variant
    ) {
        if (variant == null) {
            throw new IllegalArgumentException(
                    "Product variant must not be null."
            );
        }
    }

    private static void validateRequiredText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }

    private String normalizeOptionalText(
            String value
    ) {
        return value == null
                ? null
                : value.trim();
    }
}