package application.services;

import application.domain.models.Cart;
import application.domain.models.Order;
import application.ports.output.OrderRepository;

/**
 * Servicio de aplicación encargado de crear pedidos.
 *
 * Convierte el contenido de un carrito en un compromiso
 * comercial formal y solicita su almacenamiento.
 */
public class CreateOrderService {

    private final OrderRepository orderRepository;

    /**
     * Construye el servicio con el repositorio de pedidos.
     *
     * @param orderRepository repositorio utilizado para guardar pedidos
     */
    public CreateOrderService(OrderRepository orderRepository) {
        validateRepository(orderRepository);
        this.orderRepository = orderRepository;
    }

    /**
     * Crea un pedido a partir de un carrito.
     *
     * El constructor de Order se encarga de comprobar que:
     *
     * - El carrito exista.
     * - El carrito no esté vacío.
     * - El comprador pueda comprar.
     * - Todos los productos estén publicados.
     *
     * El pedido comienza con el estado PENDING_PAYMENT,
     * que significa pendiente de pago.
     *
     * @param cart carrito que se desea confirmar
     * @return pedido creado y almacenado
     */
    public Order create(Cart cart) {
        /*
         * Las reglas relacionadas con el carrito y el comprador
         * son protegidas directamente por el modelo Order.
         */
        Order order = new Order(cart);

        /*
         * El servicio solicita guardar el pedido mediante el puerto.
         */
        orderRepository.save(order);

        return order;
    }

    /**
     * Valida que el servicio tenga un repositorio disponible.
     *
     * @param orderRepository repositorio que se desea validar
     */
    private void validateRepository(
            OrderRepository orderRepository
    ) {
        if (orderRepository == null) {
            throw new IllegalArgumentException(
                    "Order repository must not be null."
            );
        }
    }
}
