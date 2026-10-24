package application.services;

import application.domain.models.Cart;
import application.domain.models.Order;
import application.ports.input.CreateOrderUseCase;
import application.ports.output.CartRepository;
import application.ports.output.OrderRepository;

/**
 * Servicio de aplicación encargado de crear pedidos.
 *
 * Convierte el contenido de un carrito en un compromiso
 * comercial formal y solicita su almacenamiento.
 *
 * Para las operaciones provenientes de la API, el servicio
 * localiza el carrito mediante su identificador y valida que
 * pertenezca al comprador que realiza la solicitud.
 */
public class CreateOrderService
        implements CreateOrderUseCase {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    /**
     * Construye el servicio para operaciones internas
     * que ya proporcionan el carrito del dominio.
     *
     * Este constructor se conserva para mantener compatibilidad
     * con las pruebas unitarias existentes.
     *
     * @param orderRepository repositorio utilizado
     *                        para guardar pedidos
     */
    public CreateOrderService(
            OrderRepository orderRepository
    ) {
        validateOrderRepository(orderRepository);

        this.cartRepository = null;
        this.orderRepository = orderRepository;
    }

    /**
     * Construye el servicio completo utilizado por la API.
     *
     * @param cartRepository repositorio utilizado para
     *                       localizar carritos
     * @param orderRepository repositorio utilizado para
     *                        guardar pedidos
     */
    public CreateOrderService(
            CartRepository cartRepository,
            OrderRepository orderRepository
    ) {
        validateCartRepository(cartRepository);
        validateOrderRepository(orderRepository);

        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Crea un pedido a partir de un carrito del dominio.
     *
     * El constructor de Order comprueba que:
     *
     * - El carrito exista.
     * - El carrito no esté vacío.
     * - El comprador pueda realizar compras.
     * - Todos los productos se encuentren publicados.
     *
     * El pedido comienza en estado PENDING_PAYMENT.
     *
     * @param cart carrito que se desea confirmar
     * @return pedido creado y almacenado
     */
    @Override
    public Order create(Cart cart) {
        validateCart(cart);

        Order order = new Order(cart);

        orderRepository.save(order);

        return order;
    }

    /**
     * Crea un pedido localizando el carrito mediante
     * su identificador.
     *
     * También comprueba que el comprador indicado sea
     * el propietario del carrito. Esto impide que un comprador
     * convierta en pedido el carrito de otro usuario.
     *
     * @param buyerIdentification identificación del comprador
     * @param cartId identificador del carrito
     * @return pedido creado y almacenado
     */
    @Override
    public Order createByCartId(
            String buyerIdentification,
            String cartId
    ) {
        validateText(
                buyerIdentification,
                "Buyer identification"
        );

        validateText(
                cartId,
                "Cart identifier"
        );

        validateCartRepositoryAvailable();

        Cart cart = cartRepository
                .findById(cartId)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Cart was not found."
                        )
                );

        validateCartOwner(
                buyerIdentification,
                cart
        );

        return create(cart);
    }

    /**
     * Valida que exista un carrito.
     *
     * @param cart carrito que será validado
     */
    private void validateCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException(
                    "Cart must not be null."
            );
        }
    }

    /**
     * Comprueba que el carrito pertenezca
     * al comprador solicitante.
     *
     * @param buyerIdentification identificación del comprador
     * @param cart carrito localizado
     */
    private void validateCartOwner(
            String buyerIdentification,
            Cart cart
    ) {
        if (cart.getBuyer() == null
                || !buyerIdentification.equals(
                        cart.getBuyer().getIdentification()
                )) {
            throw new IllegalStateException(
                    "Buyer can only create an order from their own cart."
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
     * Valida el repositorio de carritos.
     *
     * @param cartRepository repositorio que será validado
     */
    private void validateCartRepository(
            CartRepository cartRepository
    ) {
        if (cartRepository == null) {
            throw new IllegalArgumentException(
                    "Cart repository must not be null."
            );
        }
    }

    /**
     * Comprueba que la instancia del servicio tenga disponible
     * el repositorio de carritos.
     */
    private void validateCartRepositoryAvailable() {
        if (cartRepository == null) {
            throw new IllegalStateException(
                    "Cart repository is required for identifier-based operations."
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