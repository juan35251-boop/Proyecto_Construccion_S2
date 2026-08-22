package application.domain.models;

import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SellerTest {

    @Test
    void shouldCreateSellerWithFirstWarehouse() {
        Warehouse warehouse = createSellerWarehouse();
        Seller seller = createSeller(warehouse);

        assertEquals(SystemRole.SELLER, seller.getRole());
        assertEquals(1, seller.getWarehouses().size());
        assertTrue(seller.managesWarehouse(warehouse));
    }

    @Test
    void shouldRejectMarketplaceAsFirstWarehouse() {
        Warehouse marketplaceWarehouse = new Warehouse(
                WarehouseOwnerType.MARKETPLACE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> createSeller(marketplaceWarehouse)
        );
    }

    @Test
    void shouldRegisterProduct() {
        Seller seller = createSeller(createSellerWarehouse());
        Product product = createProduct();

        seller.registerProduct(product);

        assertEquals(1, seller.getProducts().size());
        assertTrue(seller.managesProduct(product));
    }

    @Test
    void shouldRejectNullProduct() {
        Seller seller = createSeller(createSellerWarehouse());

        assertThrows(
                IllegalArgumentException.class,
                () -> seller.registerProduct(null)
        );
    }

    @Test
    void shouldAssociateAnotherSellerWarehouse() {
        Seller seller = createSeller(createSellerWarehouse());
        Warehouse anotherWarehouse = createSellerWarehouse();

        seller.associateWarehouse(anotherWarehouse);

        assertEquals(2, seller.getWarehouses().size());
        assertTrue(seller.managesWarehouse(anotherWarehouse));
    }

    @Test
    void shouldRejectMarketplaceWarehouseAssociation() {
        Seller seller = createSeller(createSellerWarehouse());
        Warehouse marketplaceWarehouse = new Warehouse(
                WarehouseOwnerType.MARKETPLACE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> seller.associateWarehouse(marketplaceWarehouse)
        );
    }

    private Seller createSeller(Warehouse firstWarehouse) {
        return new Seller(
                "2001",
                "Nexus Seller",
                "seller@email.com",
                UserStatus.ACTIVE,
                firstWarehouse
        );
    }

    private Warehouse createSellerWarehouse() {
        return new Warehouse(
                WarehouseOwnerType.SELLER
        );
    }

    private Product createProduct() {
        return new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );
    }
}
