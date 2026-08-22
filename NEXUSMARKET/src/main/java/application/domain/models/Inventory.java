package application.domain.models;

import application.domain.valueobjects.InventoryCondition;

public class Inventory {

    private final Product product;
    private final Warehouse warehouse;
    private int availableQuantity;
    private InventoryCondition condition;

    public Inventory(
            Product product,
            Warehouse warehouse,
            int availableQuantity,
            InventoryCondition condition
    ) {
        validateProduct(product);
        validateWarehouse(warehouse);
        validateNonNegativeQuantity(availableQuantity);
        validateCondition(condition);

        this.product = product;
        this.warehouse = warehouse;
        this.availableQuantity = availableQuantity;
        this.condition = condition;
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

    public InventoryCondition getCondition() {
        return condition;
    }

    public void addQuantity(int quantity) {
        validatePositiveQuantity(quantity);
        availableQuantity += quantity;
    }

    public void reserveQuantity(int quantity) {
        validatePositiveQuantity(quantity);

        if (condition == InventoryCondition.DAMAGED) {
            throw new IllegalStateException(
                    "Damaged inventory cannot be reserved."
            );
        }

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

    public void changeCondition(InventoryCondition newCondition) {
        validateCondition(newCondition);
        this.condition = newCondition;
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

    private void validateCondition(InventoryCondition condition) {
        if (condition == null) {
            throw new IllegalArgumentException(
                    "Inventory condition must not be null."
            );
        }
    }
}