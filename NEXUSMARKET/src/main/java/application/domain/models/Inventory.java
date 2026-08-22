package application.domain.models;

public class Inventory {

    private final Product product;
    private final Warehouse warehouse;
    private int availableQuantity;

    public Inventory(
            Product product,
            Warehouse warehouse,
            int availableQuantity
    ) {
        validateProduct(product);
        validateWarehouse(warehouse);
        validateNonNegativeQuantity(availableQuantity);

        this.product = product;
        this.warehouse = warehouse;
        this.availableQuantity = availableQuantity;
    }

    public Product getProduct() {
        return product;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void addQuantity(int quantity) {
        validatePositiveQuantity(quantity);
        availableQuantity += quantity;
    }

    public void reserveQuantity(int quantity) {
        validatePositiveQuantity(quantity);

        if (quantity > availableQuantity) {
            throw new IllegalStateException(
                    "Insufficient available inventory."
            );
        }

        availableQuantity -= quantity;
    }

    public void adjustQuantity(int newQuantity) {
        validateNonNegativeQuantity(newQuantity);
        availableQuantity = newQuantity;
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Inventory must be associated with a product."
            );
        }
    }

    private void validateWarehouse(Warehouse warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Inventory must be associated with a warehouse."
            );
        }
    }

    private void validatePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }
    }

    private void validateNonNegativeQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException(
                    "Inventory quantity must not be negative."
            );
        }
    }
}
