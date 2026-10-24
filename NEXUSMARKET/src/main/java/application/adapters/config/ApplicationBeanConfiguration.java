package application.adapters.config;

import application.ports.input.AssociateWarehouseUseCase;
import application.ports.input.BuyerAddressUseCase;
import application.ports.input.BuyerQueryUseCase;
import application.ports.input.CartManagementUseCase;
import application.ports.input.CatalogQueryUseCase;
import application.ports.input.CompleteOrderUseCase;
import application.ports.input.ConfirmOrderPaymentUseCase;
import application.ports.input.CreateOrderUseCase;
import application.ports.input.DeliverDigitalOrderUseCase;
import application.ports.input.GenerateInvoiceUseCase;
import application.ports.input.InventoryManagementUseCase;
import application.ports.input.InventoryMovementQueryUseCase;
import application.ports.input.InventoryQueryUseCase;
import application.ports.input.InvoiceQueryUseCase;
import application.ports.input.OrderQueryUseCase;
import application.ports.input.ProcessRefundUseCase;
import application.ports.input.ProductCatalogUseCase;
import application.ports.input.RefundQueryUseCase;
import application.ports.input.RegisterBuyerUseCase;
import application.ports.input.RegisterInventoryUseCase;
import application.ports.input.RegisterProductUseCase;
import application.ports.input.RegisterSellerUseCase;
import application.ports.input.RegisterWarehouseUseCase;
import application.ports.input.RequestReturnUseCase;
import application.ports.input.ReturnQueryUseCase;
import application.ports.input.SellerQueryUseCase;
import application.ports.input.ShipmentManagementUseCase;
import application.ports.input.ShipmentQueryUseCase;
import application.ports.input.WarehouseQueryUseCase;
import application.ports.output.CartRepository;
import application.ports.output.InventoryMovementQueryRepository;
import application.ports.output.InventoryMovementRepository;
import application.ports.output.InventoryQueryRepository;
import application.ports.output.InventoryRepository;
import application.ports.output.InvoiceQueryRepository;
import application.ports.output.InvoiceRepository;
import application.ports.output.OrderQueryRepository;
import application.ports.output.OrderRepository;
import application.ports.output.ProductQueryRepository;
import application.ports.output.ProductRepository;
import application.ports.output.RefundQueryRepository;
import application.ports.output.RefundRepository;
import application.ports.output.ReturnQueryRepository;
import application.ports.output.ReturnRepository;
import application.ports.output.ShipmentQueryRepository;
import application.ports.output.ShipmentRepository;
import application.ports.output.UserRepository;
import application.ports.output.WarehouseRepository;
import application.services.AssociateWarehouseService;
import application.services.BuyerAddressService;
import application.services.BuyerQueryService;
import application.services.CartManagementService;
import application.services.CatalogQueryService;
import application.services.CompleteOrderService;
import application.services.ConfirmOrderPaymentService;
import application.services.CreateOrderService;
import application.services.DeliverDigitalOrderService;
import application.services.GenerateInvoiceService;
import application.services.InventoryManagementService;
import application.services.InventoryMovementQueryService;
import application.services.InventoryQueryService;
import application.services.InvoiceQueryService;
import application.services.OrderQueryService;
import application.services.ProcessRefundService;
import application.services.ProductCatalogService;
import application.services.RefundQueryService;
import application.services.RegisterBuyerService;
import application.services.RegisterInventoryService;
import application.services.RegisterProductService;
import application.services.RegisterSellerService;
import application.services.RegisterWarehouseService;
import application.services.RequestReturnService;
import application.services.ReturnQueryService;
import application.services.SellerQueryService;
import application.services.ShipmentManagementService;
import application.services.ShipmentQueryService;
import application.services.WarehouseQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de los servicios de aplicación.
 *
 * Registra las implementaciones de los puertos de entrada
 * como componentes administrados por Spring.
 */
@Configuration
public class ApplicationBeanConfiguration {

    @Bean
    public RegisterBuyerUseCase registerBuyerUseCase(
            UserRepository userRepository
    ) {
        return new RegisterBuyerService(
                userRepository
        );
    }

    @Bean
    public BuyerQueryUseCase buyerQueryUseCase(
            UserRepository userRepository
    ) {
        return new BuyerQueryService(
                userRepository
        );
    }

    @Bean
    public BuyerAddressUseCase buyerAddressUseCase(
            UserRepository userRepository
    ) {
        return new BuyerAddressService(
                userRepository
        );
    }

    @Bean
    public RegisterSellerUseCase registerSellerUseCase(
            UserRepository userRepository,
            WarehouseRepository warehouseRepository
    ) {
        return new RegisterSellerService(
                userRepository,
                warehouseRepository
        );
    }

    @Bean
    public SellerQueryUseCase sellerQueryUseCase(
            UserRepository userRepository
    ) {
        return new SellerQueryService(
                userRepository
        );
    }

    @Bean
    public RegisterWarehouseUseCase registerWarehouseUseCase(
            UserRepository userRepository,
            WarehouseRepository warehouseRepository
    ) {
        return new RegisterWarehouseService(
                userRepository,
                warehouseRepository
        );
    }

    @Bean
    public WarehouseQueryUseCase warehouseQueryUseCase(
            UserRepository userRepository,
            WarehouseRepository warehouseRepository
    ) {
        return new WarehouseQueryService(
                userRepository,
                warehouseRepository
        );
    }

    @Bean
    public AssociateWarehouseUseCase associateWarehouseUseCase(
            UserRepository userRepository,
            WarehouseRepository warehouseRepository
    ) {
        return new AssociateWarehouseService(
                userRepository,
                warehouseRepository
        );
    }

    @Bean
    public RegisterProductUseCase registerProductUseCase(
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        return new RegisterProductService(
                userRepository,
                productRepository
        );
    }

    @Bean
    public ProductCatalogUseCase productCatalogUseCase(
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository,
            ProductRepository productRepository
    ) {
        return new ProductCatalogService(
                userRepository,
                productQueryRepository,
                productRepository
        );
    }

    @Bean
    public CatalogQueryUseCase catalogQueryUseCase(
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository
    ) {
        return new CatalogQueryService(
                userRepository,
                productQueryRepository
        );
    }

    @Bean
    public RegisterInventoryUseCase registerInventoryUseCase(
            InventoryRepository inventoryRepository,
            InventoryMovementRepository movementRepository,
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository,
            WarehouseRepository warehouseRepository
    ) {
        return new RegisterInventoryService(
                inventoryRepository,
                movementRepository,
                userRepository,
                productQueryRepository,
                warehouseRepository
        );
    }

    @Bean
    public InventoryManagementUseCase
    inventoryManagementUseCase(
            InventoryRepository inventoryRepository,
            InventoryMovementRepository movementRepository,
            UserRepository userRepository,
            InventoryQueryRepository inventoryQueryRepository
    ) {
        return new InventoryManagementService(
                inventoryRepository,
                movementRepository,
                userRepository,
                inventoryQueryRepository
        );
    }

    @Bean
    public InventoryQueryUseCase inventoryQueryUseCase(
            InventoryQueryRepository inventoryQueryRepository,
            UserRepository userRepository
    ) {
        return new InventoryQueryService(
                inventoryQueryRepository,
                userRepository
        );
    }

    @Bean
    public InventoryMovementQueryUseCase
    inventoryMovementQueryUseCase(
            InventoryMovementQueryRepository
                    movementQueryRepository,
            UserRepository userRepository
    ) {
        return new InventoryMovementQueryService(
                movementQueryRepository,
                userRepository
        );
    }

    @Bean
    public CartManagementUseCase cartManagementUseCase(
            CartRepository cartRepository,
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository
    ) {
        return new CartManagementService(
                cartRepository,
                userRepository,
                productQueryRepository
        );
    }

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            CartRepository cartRepository,
            OrderRepository orderRepository
    ) {
        return new CreateOrderService(
                cartRepository,
                orderRepository
        );
    }

    @Bean
    public ConfirmOrderPaymentUseCase
    confirmOrderPaymentUseCase(
            UserRepository userRepository,
            OrderRepository orderRepository
    ) {
        return new ConfirmOrderPaymentService(
                userRepository,
                orderRepository
        );
    }

    @Bean
    public DeliverDigitalOrderUseCase
    deliverDigitalOrderUseCase(
            OrderRepository orderRepository
    ) {
        return new DeliverDigitalOrderService(
                orderRepository
        );
    }

    @Bean
    public CompleteOrderUseCase completeOrderUseCase(
            OrderRepository orderRepository
    ) {
        return new CompleteOrderService(
                orderRepository
        );
    }

    @Bean
    public OrderQueryUseCase orderQueryUseCase(
            UserRepository userRepository,
            OrderQueryRepository orderQueryRepository
    ) {
        return new OrderQueryService(
                userRepository,
                orderQueryRepository
        );
    }

    @Bean
    public ShipmentManagementUseCase
    shipmentManagementUseCase(
            ShipmentRepository shipmentRepository,
            OrderRepository orderRepository,
            UserRepository userRepository
    ) {
        return new ShipmentManagementService(
                shipmentRepository,
                orderRepository,
                userRepository
        );
    }

    @Bean
    public ShipmentQueryUseCase shipmentQueryUseCase(
            ShipmentQueryRepository shipmentQueryRepository,
            UserRepository userRepository
    ) {
        return new ShipmentQueryService(
                shipmentQueryRepository,
                userRepository
        );
    }

    @Bean
    public GenerateInvoiceUseCase generateInvoiceUseCase(
            InvoiceRepository invoiceRepository,
            InvoiceQueryRepository invoiceQueryRepository,
            OrderRepository orderRepository
    ) {
        return new GenerateInvoiceService(
                invoiceRepository,
                invoiceQueryRepository,
                orderRepository
        );
    }

    @Bean
    public InvoiceQueryUseCase invoiceQueryUseCase(
            InvoiceQueryRepository invoiceQueryRepository,
            UserRepository userRepository
    ) {
        return new InvoiceQueryService(
                invoiceQueryRepository,
                userRepository
        );
    }

    @Bean
    public RequestReturnUseCase requestReturnUseCase(
            ReturnRepository returnRepository,
            ReturnQueryRepository returnQueryRepository,
            UserRepository userRepository,
            OrderRepository orderRepository
    ) {
        return new RequestReturnService(
                returnRepository,
                returnQueryRepository,
                userRepository,
                orderRepository
        );
    }

    @Bean
    public ReturnQueryUseCase returnQueryUseCase(
            ReturnQueryRepository returnQueryRepository,
            UserRepository userRepository
    ) {
        return new ReturnQueryService(
                returnQueryRepository,
                userRepository
        );
    }

    /**
     * Registra el caso de uso encargado
     * de procesar reembolsos.
     */
    @Bean
    public ProcessRefundUseCase processRefundUseCase(
            RefundRepository refundRepository,
            RefundQueryRepository refundQueryRepository,
            ReturnQueryRepository returnQueryRepository,
            UserRepository userRepository
    ) {
        return new ProcessRefundService(
                refundRepository,
                refundQueryRepository,
                returnQueryRepository,
                userRepository
        );
    }

    /**
     * Registra el caso de uso encargado
     * de consultar reembolsos.
     */
    @Bean
    public RefundQueryUseCase refundQueryUseCase(
            RefundQueryRepository refundQueryRepository,
            UserRepository userRepository
    ) {
        return new RefundQueryService(
                refundQueryRepository,
                userRepository
        );
    }
}