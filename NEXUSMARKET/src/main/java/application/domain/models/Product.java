package application.domain.models;

import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;

public class Product {

    private final ProductType productType;
    private ProductStatus status;

    public Product(ProductType productType, ProductStatus status) {
        validateProductType(productType);
        validateStatus(status);

        this.productType = productType;
        this.status = status;
    }

    public ProductType getProductType() {
        return productType;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void changeStatus(ProductStatus newStatus) {
        validateStatus(newStatus);
        this.status = newStatus;
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
}
