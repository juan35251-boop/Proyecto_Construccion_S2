package application.domain.models;

import application.domain.valueobjects.WarehouseOwnerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WarehouseTest {

    @Test
    void shouldCreateMarketplaceWarehouse() {
        Warehouse warehouse = new Warehouse(
                WarehouseOwnerType.MARKETPLACE
        );

        assertEquals(
                WarehouseOwnerType.MARKETPLACE,
                warehouse.getOwnerType()
        );

        assertTrue(warehouse.isMarketplaceWarehouse());
        assertFalse(warehouse.isSellerWarehouse());
    }

    @Test
    void shouldCreateSellerWarehouse() {
        Warehouse warehouse = new Warehouse(
                WarehouseOwnerType.SELLER
        );

        assertEquals(
                WarehouseOwnerType.SELLER,
                warehouse.getOwnerType()
        );

        assertTrue(warehouse.isSellerWarehouse());
        assertFalse(warehouse.isMarketplaceWarehouse());
    }

    @Test
    void shouldRejectNullOwnerType() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Warehouse(null)
        );
    }
}