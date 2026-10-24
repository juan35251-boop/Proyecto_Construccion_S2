package application.services;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Return;
import application.domain.models.User;
import application.ports.input.RequestReturnUseCase;
import application.ports.output.OrderRepository;
import application.ports.output.ReturnQueryRepository;
import application.ports.output.ReturnRepository;
import application.ports.output.UserRepository;

/**
 * Servicio encargado de solicitar devoluciones.
 */
public class RequestReturnService
        implements RequestReturnUseCase {

    private final ReturnRepository returnRepository;

    private final ReturnQueryRepository
            returnQueryRepository;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    /**
     * Constructor conservado para mantener compatibilidad
     * con las pruebas unitarias existentes.
     *
     * @param returnRepository repositorio de devoluciones
     */
    public RequestReturnService(
            ReturnRepository returnRepository
    ) {
        validateReturnRepository(returnRepository);

        this.returnRepository = returnRepository;
        this.returnQueryRepository = null;
        this.userRepository = null;
        this.orderRepository = null;
    }

    /**
     * Construye el servicio completo utilizado por la API.
     *
     * @param returnRepository repositorio de escritura
     * @param returnQueryRepository repositorio de consulta
     * @param userRepository repositorio de usuarios
     * @param orderRepository repositorio de órdenes
     */
    public RequestReturnService(
            ReturnRepository returnRepository,
            ReturnQueryRepository returnQueryRepository,
            UserRepository userRepository,
            OrderRepository orderRepository
    ) {
        validateReturnRepository(returnRepository);

        validateReturnQueryRepository(
                returnQueryRepository
        );

        validateUserRepository(userRepository);
        validateOrderRepository(orderRepository);

        this.returnRepository = returnRepository;

        this.returnQueryRepository =
                returnQueryRepository;

        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Solicita una devolución utilizando
     * objetos del dominio.
     *
     * El comprador debe encontrarse activo en el sistema.
     * Su estado comercial no impide devolver una compra
     * realizada anteriormente.
     *
     * @param buyer comprador solicitante
     * @param order orden devuelta
     * @return devolución creada
     */
    @Override
    public Return request(
            Buyer buyer,
            Order order
    ) {
        validateBuyer(buyer);
        validateActiveBuyer(buyer);
        validateOrder(order);

        Return returnProcess =
                new Return(
                        order,
                        buyer
                );

        returnRepository.save(returnProcess);

        return returnProcess;
    }

    /**
     * Solicita una devolución utilizando identificadores.
     *
     * @param buyerIdentification identificación del comprador
     * @param orderId identificador de la orden
     * @return devolución creada
     */
    @Override
    public Return requestByIdentifiers(
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

        validateIdentifierRepositories();

        User user =
                userRepository
                        .findByIdentification(
                                buyerIdentification
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Buyer was not found."
                                )
                        );

        if (!(user instanceof Buyer buyer)) {
            throw new IllegalStateException(
                    "Only a buyer can request a return."
            );
        }

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Order was not found."
                                )
                        );

        if (returnQueryRepository
                .existsByOrderId(orderId)) {
            throw new IllegalStateException(
                    "Order already has a return request."
            );
        }

        return request(buyer, order);
    }

    /**
     * Valida que exista un comprador.
     */
    private void validateBuyer(
            Buyer buyer
    ) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Return must be requested by a buyer."
            );
        }
    }

    /**
     * Comprueba únicamente el estado general del comprador.
     *
     * Un comprador suspendido comercialmente puede solicitar
     * la devolución de una compra anterior.
     */
    private void validateActiveBuyer(
            Buyer buyer
    ) {
        if (!buyer.isActive()) {
            throw new IllegalStateException(
                    "Only an active buyer can request returns."
            );
        }
    }

    /**
     * Valida que exista una orden.
     */
    private void validateOrder(
            Order order
    ) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Return order must not be null."
            );
        }
    }

    /**
     * Valida un texto obligatorio.
     */
    private void validateText(
            String value,
            String fieldName
    ) {
        if (value == null
                || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }

    /**
     * Comprueba que estén disponibles los repositorios
     * necesarios para operar con identificadores.
     */
    private void validateIdentifierRepositories() {
        if (returnQueryRepository == null
                || userRepository == null
                || orderRepository == null) {
            throw new IllegalStateException(
                    "Repositories required for identifier-based return requests are not configured."
            );
        }
    }

    private void validateReturnRepository(
            ReturnRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Return repository must not be null."
            );
        }
    }

    private void validateReturnQueryRepository(
            ReturnQueryRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Return query repository must not be null."
            );
        }
    }

    private void validateUserRepository(
            UserRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }
    }

    private void validateOrderRepository(
            OrderRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Order repository must not be null."
            );
        }
    }
}