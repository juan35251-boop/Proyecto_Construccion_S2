package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

import java.util.ArrayList;
import java.util.List;

public class Seller extends User {

    private final List<Product> products;
    private final List<Warehouse> warehouses;

    public Seller(
            String identification,
            String fullName,
            String email,
            UserStatus status,
            Warehouse firstWarehouse
    ) {
        super(identification, fullName, email, status);

        validateSellerWarehouse(firstWarehouse);

        this.products = new ArrayList<>();
        this.warehouses = new ArrayList<>();
        this.warehouses.add(firstWarehouse);
    }

    @Override
    public SystemRole getRole() {
        return SystemRole.SELLER;
    }

    public List<Product> getProducts() {
        return List.copyOf(products);
    }

    public List<Warehouse> getWarehouses() {
        return List.copyOf(warehouses);
    }

    public void registerProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product must not be null."
            );
        }

        products.add(product);
    }

    public void associateWarehouse(Warehouse warehouse) {
        validateSellerWarehouse(warehouse);
        warehouses.add(warehouse);
    }

    public boolean managesProduct(Product product) {
        return products.contains(product);
    }

    public boolean managesWarehouse(Warehouse warehouse) {
        return warehouses.contains(warehouse);
    }

    private void validateSellerWarehouse(Warehouse warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Seller warehouse must not be null."
            );
        }

        if (!warehouse.isSellerWarehouse()) {
            throw new IllegalArgumentException(
                    "A seller can only be associated with seller warehouses."
            );
        }
    }
}
