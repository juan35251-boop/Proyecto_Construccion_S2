package application.services;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.User;
import application.ports.input.ConfirmOrderPaymentUseCase;
import application.ports.output.OrderRepository;
import application.ports.output.UserRepository;

/**
 * Servicio de aplicación encargado de confirmar
 * el pago de un pedido.
 *
 * Este servicio no realiza un cobro bancario.
 * Su responsabilidad consiste en comprobar que
 * el comprador y el pedido sean válidos, cambiar
 * el estado del pedido a PAID y guardar el cambio.
 */
public class ConfirmOrderPaymentService
        implements ConfirmOrderPaymentUseCase {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    /**
     * Construye el servicio para operaciones que ya
     * proporcionan los objetos del dominio.
     *
     * Este constructor se conserva para mantener
     * compatibilidad con las pruebas existentes.
     *
     * @param orderRepository repositorio de pedidos
     */
    public ConfirmOrderPaymentService(
            OrderRepository orderRepository
    ) {
        validateOrderRepository(orderRepository);

        this.userRepository = null;
        this.orderRepository = orderRepository;
    }

    /**
     * Construye el servicio completo utilizado por la API.
     *
     * @param userRepository repositorio utilizado
     *                       para localizar al comprador
     * @param orderRepository repositorio utilizado
     *                        para consultar y guardar pedidos
     */
    public ConfirmOrderPaymentService(
            UserRepository userRepository,
            OrderRepository orderRepository
    ) {
        validateUserRepository(userRepository);
        validateOrderRepository(orderRepository);

        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Confirma el pago utilizando objetos del dominio.
     *
     * Para realizar la operación:
     *
     * - El comprador debe existir.
     * - El pedido debe existir.
     * - El comprador debe estar autorizado para comprar.
     * - El pedido debe pertenecer al comprador.
     * - El pedido debe encontrarse en PENDING_PAYMENT.
     *
     * La última regla es protegida internamente por Order
     * mediante el método markAsPaid().
     *
     * @param buyer comprador que confirma el pago
     * @param order pedido que fue pagado
     * @return pedido actualizado
     */
    @Override
    public Order confirm(
            Buyer buyer,
            Order order
    ) {
        validateBuyer(buyer);
        validateOrder(order);
        validateBuyerCanPurchase(buyer);
        validateOrderOwner(buyer, order);

        /*
         * El modelo Order controla su ciclo de vida.
         * Solamente un pedido pendiente de pago puede
         * cambiar al estado PAID.
         */
        order.markAsPaid();

        /*
         * Se conserva el nuevo estado mediante
         * el puerto de salida.
         */
        orderRepository.save(order);

        return order;
    }

    /**
     * Confirma el pago utilizando identificadores.
     *
     * Este método está preparado para ser invocado
     * desde un controlador REST.
     *
     * Primero localiza al comprador y al pedido.
     * Después reutiliza confirm() para aplicar todas
     * las reglas de negocio.
     *
     * @param buyerIdentification identificación del comprador
     * @param orderId identificador del pedido
     * @return pedido actualizado
     */
    @Override
    public Order confirmByIdentifiers(
            String buyerIdentification,
            String orderId
    ) {
        validateText(
                buyerIdentification,
                "Buyer identification"
        );

        validateText(
                orderId,
                "Order identifier"
        );

        validateUserRepositoryAvailable();

        User user = userRepository
                .findByIdentification(
                        buyerIdentification
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Buyer was not found."
                        )
                );

        if (!(user instanceof Buyer buyer)) {
            throw new IllegalStateException(
                    "Only a buyer can confirm an order payment."
            );
        }

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Order was not found."
                        )
                );

        return confirm(buyer, order);
    }

    /**
     * Valida que la operación sea realizada
     * por un comprador.
     *
     * @param buyer comprador que será validado
     */
    private void validateBuyer(Buyer buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Payment must be confirmed by a buyer."
            );
        }
    }

    /**
     * Valida que exista un pedido.
     *
     * @param order pedido que será validado
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order must not be null."
            );
        }
    }

    /**
     * Comprueba que el comprador se encuentre
     * autorizado para realizar compras.
     *
     * @param buyer comprador que será validado
     */
    private void validateBuyerCanPurchase(
            Buyer buyer
    ) {
        if (!buyer.canPurchase()) {
            throw new IllegalStateException(
                    "Buyer is not authorized to confirm payments."
            );
        }
    }

    /**
     * Comprueba que el pedido pertenezca al comprador.
     *
     * La comparación se realiza mediante la identificación,
     * porque el comprador recuperado desde la base de datos
     * puede ser una instancia diferente a la almacenada
     * dentro del pedido.
     *
     * @param buyer comprador que confirma el pago
     * @param order pedido que será pagado
     */
    private void validateOrderOwner(
            Buyer buyer,
            Order order
    ) {
        if (order.getBuyer() == null
                || !buyer.getIdentification().equals(
                        order.getBuyer().getIdentification()
                )) {
            throw new IllegalStateException(
                    "Buyer can only pay their own order."
            );
        }
    }

    /**
     * Valida un identificador obligatorio.
     *
     * @param value valor recibido
     * @param fieldName nombre funcional del campo
     */
    private void validateText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }

    /**
     * Valida el repositorio de usuarios.
     *
     * @param userRepository repositorio que será validado
     */
    private void validateUserRepository(
            UserRepository userRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }
    }

    /**
     * Comprueba que esta instancia tenga disponible
     * el repositorio de usuarios.
     */
    private void validateUserRepositoryAvailable() {
        if (userRepository == null) {
            throw new IllegalStateException(
                    "User repository is required for identifier-based operations."
            );
        }
    }

    /**
     * Valida el repositorio de pedidos.
     *
     * @param orderRepository repositorio que será validado
     */
    private void validateOrderRepository(
            OrderRepository orderRepository
    ) {
        if (orderRepository == null) {
            throw new IllegalArgumentException(
                    "Order repository must not be null."
            );
        }
    }
}