package application.domain.models;

import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;

import java.util.ArrayList;
import java.util.List;

public class Product {

    private final ProductType productType;
    private final List<ProductVariant> variants;
    private ProductStatus status;

    public Product(ProductType productType, ProductStatus status) {
        validateProductType(productType);
        validateStatus(status);

        this.productType = productType;
        this.status = status;
        this.variants = new ArrayList<>();
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
        return variant != null && variants.contains(variant);
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

    private void validateProductType(ProductType productType) {
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

    private void validateVariant(ProductVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException(
                    "Product variant must not be null."
            );
        }
    }
}