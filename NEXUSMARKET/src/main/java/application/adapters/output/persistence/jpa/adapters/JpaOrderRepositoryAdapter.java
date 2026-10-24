package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.output.persistence.jpa.mappers.OrderJpaMapper;
import application.adapters.output.persistence.jpa.repositories.SpringDataOrderRepository;
import application.domain.models.Order;
import application.ports.output.OrderQueryRepository;
import application.ports.output.OrderRepository;
import application.ports.output.ProductQueryRepository;
import application.ports.output.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA que implementa los puertos utilizados
 * para guardar y consultar pedidos.
 *
 * El estado actual de los pedidos se conserva en MySQL.
 */
@Repository
@Transactional
public class JpaOrderRepositoryAdapter
        implements OrderRepository,
        OrderQueryRepository {

    private final SpringDataOrderRepository
            springDataOrderRepository;

    private final OrderJpaMapper orderJpaMapper;

    /**
     * Construye el adaptador.
     *
     * @param springDataOrderRepository repositorio Spring Data
     * @param userRepository repositorio de usuarios
     * @param productQueryRepository repositorio de productos
     */
    public JpaOrderRepositoryAdapter(
            SpringDataOrderRepository springDataOrderRepository,
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository
    ) {
        validateSpringDataRepository(
                springDataOrderRepository
        );

        this.springDataOrderRepository =
                springDataOrderRepository;

        this.orderJpaMapper =
                new OrderJpaMapper(
                        userRepository,
                        productQueryRepository
                );
    }

    /**
     * Guarda un pedido nuevo o actualiza uno existente.
     *
     * @param order pedido almacenado
     */
    @Override
    public void save(
            Order order
    ) {
        validateOrder(order);

        OrderJpaEntity entity =
                springDataOrderRepository
                        .findByOrderId(
                                order.getOrderId()
                        )
                        .map(existingEntity ->
                                orderJpaMapper.updateEntity(
                                        order,
                                        existingEntity
                                )
                        )
                        .orElseGet(() ->
                                orderJpaMapper.toEntity(order)
                        );

        springDataOrderRepository.save(entity);
    }

    /**
     * Busca un pedido mediante orderId.
     *
     * Esta implementación satisface tanto OrderRepository
     * como OrderQueryRepository.
     *
     * @param orderId identificador del pedido
     * @return pedido encontrado o resultado vacío
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(
            String orderId
    ) {
        validateText(
                orderId,
                "Order identifier"
        );

        return springDataOrderRepository
                .findByOrderId(orderId)
                .map(orderJpaMapper::toDomain);
    }

    /**
     * Obtiene los pedidos de un comprador.
     *
     * @param buyerIdentification identificación del comprador
     * @return pedidos encontrados
     */
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByBuyerIdentification(
            String buyerIdentification
    ) {
        validateText(
                buyerIdentification,
                "Buyer identification"
        );

        return springDataOrderRepository
                .findAllByBuyerIdentification(
                        buyerIdentification
                )
                .stream()
                .map(orderJpaMapper::toDomain)
                .toList();
    }

    /**
     * Obtiene todos los pedidos.
     *
     * @return pedidos registrados
     */
    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return springDataOrderRepository
                .findAll()
                .stream()
                .map(orderJpaMapper::toDomain)
                .toList();
    }

    /**
     * Valida el repositorio Spring Data.
     */
    private void validateSpringDataRepository(
            SpringDataOrderRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Spring Data order repository must not be null."
            );
        }
    }

    /**
     * Valida el pedido.
     */
    private void validateOrder(
            Order order
    ) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order must not be null."
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
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }
}