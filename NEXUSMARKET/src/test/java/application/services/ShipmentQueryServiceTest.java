package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.Supervisor;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryShipmentQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio de consulta de envíos.
 */
class ShipmentQueryServiceTest {

    @Test
    @DisplayName("El administrador consulta todos los envíos")
    void administratorShouldFindAllShipments() {
        ShipmentQueryService service =
                new ShipmentQueryService(createRepository());

        List<Shipment> result =
                service.findAccessibleShipments(
                        createAdministrator()
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El supervisor consulta todos los envíos")
    void supervisorShouldFindAllShipments() {
        ShipmentQueryService service =
                new ShipmentQueryService(createRepository());

        List<Shipment> result =
                service.findAccessibleShipments(
                        createSupervisor()
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El operador logístico consulta todos los envíos")
    void logisticsOperatorShouldFindAllShipments() {
        ShipmentQueryService service =
                new ShipmentQueryService(createRepository());

        List<Shipment> result =
                service.findAccessibleShipments(
                        createLogisticsOperator(
                                UserStatus.ACTIVE
                        )
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El comprador consulta envíos de sus pedidos")
    void buyerShouldFindOwnShipments() {
        InMemoryShipmentQueryRepository repository =
                new InMemoryShipmentQueryRepository();

        Buyer buyer = createBuyer("1001");

        Shipment ownShipment = createShipment(
                buyer,
                createProduct()
        );

        Shipment otherShipment = createShipment(
                createBuyer("1002"),
                createProduct()
        );

        repository.add(ownShipment);
        repository.add(otherShipment);

        ShipmentQueryService service =
                new ShipmentQueryService(repository);

        List<Shipment> result =
                service.findAccessibleShipments(buyer);

        assertEquals(1, result.size());
        assertTrue(result.contains(ownShipment));
    }

    @Test
    @DisplayName("El vendedor consulta envíos con sus productos")
    void sellerShouldFindRelatedShipments() {
        InMemoryShipmentQueryRepository repository =
                new InMemoryShipmentQueryRepository();

        Product ownProduct = createProduct();
        Product otherProduct = createProduct();

        Seller seller = createSeller(UserStatus.ACTIVE);
        seller.registerProduct(ownProduct);

        Shipment relatedShipment = createShipment(
                createBuyer("1001"),
                ownProduct
        );

        Shipment unrelatedShipment = createShipment(
                createBuyer("1002"),
                otherProduct
        );

        repository.add(relatedShipment);
        repository.add(unrelatedShipment);

        ShipmentQueryService service =
                new ShipmentQueryService(repository);

        List<Shipment> result =
                service.findAccessibleShipments(seller);

        assertEquals(1, result.size());
        assertTrue(result.contains(relatedShipment));
    }

    @Test
    @DisplayName("Debe rechazar un usuario inactivo")
    void shouldRejectInactiveUser() {
        ShipmentQueryService service =
                new ShipmentQueryService(createRepository());

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleShipments(
                        createLogisticsOperator(
                                UserStatus.INACTIVE
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario nulo")
    void shouldRejectNullUser() {
        ShipmentQueryService service =
                new ShipmentQueryService(createRepository());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findAccessibleShipments(null)
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ShipmentQueryService(null)
        );
    }

    private InMemoryShipmentQueryRepository
            createRepository() {
        InMemoryShipmentQueryRepository repository =
                new InMemoryShipmentQueryRepository();

        repository.add(
                createShipment(
                        createBuyer("1001"),
                        createProduct()
                )
        );

        repository.add(
                createShipment(
                        createBuyer("1002"),
                        createProduct()
                )
        );

        return repository;
    }

    private Shipment createShipment(
            Buyer buyer,
            Product product
    ) {
        Order order = createOrder(buyer, product);
        order.markAsPaid();

        return new Shipment(order);
    }

    private Order createOrder(
            Buyer buyer,
            Product product
    ) {
        Cart cart = new Cart(buyer);
        cart.addProduct(product, 1);

        return new Order(cart);
    }

    private Product createProduct() {
        return new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );
    }

    private Buyer createBuyer(String identification) {
        return new Buyer(
                identification,
                "Buyer " + identification,
                identification + "@email.com",
                UserStatus.ACTIVE,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );
    }

    private Seller createSeller(UserStatus status) {
        return new Seller(
                "2001",
                "Seller",
                "seller@email.com",
                status,
                new Warehouse(WarehouseOwnerType.SELLER)
        );
    }

    private LogisticsOperator createLogisticsOperator(
            UserStatus status
    ) {
        return new LogisticsOperator(
                "3001",
                "Logistics Operator",
                "operator@email.com",
                status
        );
    }

    private Administrator createAdministrator() {
        return new Administrator(
                "9001",
                "Administrator",
                "admin@email.com",
                UserStatus.ACTIVE
        );
    }

    private Supervisor createSupervisor() {
        return new Supervisor(
                "8001",
                "Supervisor",
                "supervisor@email.com",
                UserStatus.ACTIVE
        );
    }
}