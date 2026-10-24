package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.OrderItemJpaEntity;
import application.adapters.output.persistence.jpa.entities.OrderJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.User;
import application.ports.output.ProductQueryRepository;
import application.ports.output.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Convierte pedidos entre el modelo del dominio
 * y las entidades utilizadas por JPA.
 *
 * Para reconstruir un pedido, localiza al comprador
 * y los productos mediante sus identificadores.
 */
@Component
public class OrderJpaMapper {

    private final UserRepository userRepository;
    private final ProductQueryRepository productQueryRepository;

    /**
     * Construye el mapper con los repositorios necesarios.
     *
     * @param userRepository repositorio de usuarios
     * @param productQueryRepository repositorio de productos
     */
    public OrderJpaMapper(
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository
    ) {
        validateUserRepository(userRepository);

        validateProductRepository(
                productQueryRepository
        );

        this.userRepository = userRepository;
        this.productQueryRepository =
                productQueryRepository;
    }

    /**
     * Convierte un pedido nuevo en una entidad JPA.
     *
     * Al crear el pedido se almacenan sus productos.
     * Después de su creación, los productos confirmados
     * no deben reemplazarse durante cambios de estado.
     *
     * @param order pedido del dominio
     * @return entidad persistente
     */
    public OrderJpaEntity toEntity(
            Order order
    ) {
        validateOrder(order);

        OrderJpaEntity entity =
                new OrderJpaEntity(
                        order.getOrderId(),
                        order.getBuyer()
                                .getIdentification(),
                        order.getStatus()
                );

        entity.replaceItems(
                toItemEntities(
                        order.getItems()
                )
        );

        return entity;
    }

    /**
     * Actualiza una entidad existente.
     *
     * Conserva la clave técnica, el comprador y los productos
     * originales del pedido. Solamente actualiza su estado,
     * porque los productos quedan confirmados al crear el pedido.
     *
     * Evitar la reconstrucción de los elementos también impide
     * conflictos con orphanRemoval durante las actualizaciones
     * administradas por Hibernate.
     *
     * @param order pedido actualizado
     * @param entity entidad existente
     * @return entidad actualizada
     */
    public OrderJpaEntity updateEntity(
            Order order,
            OrderJpaEntity entity
    ) {
        validateOrder(order);
        validateEntity(entity);

        validateSameOrderIdentifier(
                order,
                entity
        );

        validateSameBuyer(
                order,
                entity
        );

        validateSameItems(
                order,
                entity
        );

        entity.changeStatus(
                order.getStatus()
        );

        return entity;
    }

    /**
     * Reconstruye un pedido del dominio.
     *
     * @param entity entidad persistente
     * @return pedido reconstruido
     */
    public Order toDomain(
            OrderJpaEntity entity
    ) {
        validateEntity(entity);

        Buyer buyer =
                findBuyer(
                        entity.getBuyerIdentification()
                );

        List<OrderItem> items =
                entity.getItems()
                        .stream()
                        .map(this::toDomainItem)
                        .toList();

        return new Order(
                entity.getOrderId(),
                buyer,
                items,
                entity.getStatus()
        );
    }

    /**
     * Comprueba que el identificador del pedido
     * no haya cambiado durante la actualización.
     *
     * @param order pedido del dominio
     * @param entity entidad persistente
     */
    private void validateSameOrderIdentifier(
            Order order,
            OrderJpaEntity entity
    ) {
        if (!order.getOrderId()
                .equals(entity.getOrderId())) {
            throw new IllegalStateException(
                    "Order and JPA entity identifiers do not match."
            );
        }
    }

    /**
     * Comprueba que el comprador del pedido
     * no haya cambiado.
     *
     * @param order pedido del dominio
     * @param entity entidad persistente
     */
    private void validateSameBuyer(
            Order order,
            OrderJpaEntity entity
    ) {
        if (!order.getBuyer()
                .getIdentification()
                .equals(
                        entity.getBuyerIdentification()
                )) {
            throw new IllegalStateException(
                    "Order buyer cannot be changed."
            );
        }
    }

    /**
     * Comprueba que los elementos confirmados del pedido
     * no hayan cambiado después de su creación.
     *
     * La comparación se realiza por identificador de producto,
     * cantidad y posición.
     *
     * @param order pedido del dominio
     * @param entity entidad persistente
     */
    private void validateSameItems(
            Order order,
            OrderJpaEntity entity
    ) {
        List<OrderItem> domainItems =
                order.getItems();

        List<OrderItemJpaEntity> persistedItems =
                entity.getItems();

        if (domainItems.size()
                != persistedItems.size()) {
            throw new IllegalStateException(
                    "Order items cannot be changed after creation."
            );
        }

        for (int index = 0;
             index < domainItems.size();
             index++) {

            OrderItem domainItem =
                    domainItems.get(index);

            OrderItemJpaEntity persistedItem =
                    persistedItems.get(index);

            if (domainItem == null
                    || domainItem.getProduct() == null
                    || persistedItem == null) {
                throw new IllegalStateException(
                        "Order items cannot be changed after creation."
                );
            }

            boolean sameProduct =
                    domainItem.getProduct()
                            .getProductId()
                            .equals(
                                    persistedItem
                                            .getProductId()
                            );

            boolean sameQuantity =
                    domainItem.getQuantity()
                            == persistedItem.getQuantity();

            if (!sameProduct || !sameQuantity) {
                throw new IllegalStateException(
                        "Order items cannot be changed after creation."
                );
            }
        }
    }

    /**
     * Convierte los elementos del pedido
     * en entidades persistentes.
     *
     * @param items elementos del pedido
     * @return entidades persistentes
     */
    private List<OrderItemJpaEntity> toItemEntities(
            List<OrderItem> items
    ) {
        if (items == null) {
            throw new IllegalArgumentException(
                    "Order items must not be null."
            );
        }

        return items.stream()
                .map(this::toEntityItem)
                .toList();
    }

    /**
     * Convierte un elemento del dominio.
     *
     * @param item elemento del pedido
     * @return entidad persistente
     */
    private OrderItemJpaEntity toEntityItem(
            OrderItem item
    ) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "Order item must not be null."
            );
        }

        if (item.getProduct() == null) {
            throw new IllegalStateException(
                    "Order item product must not be null."
            );
        }

        return new OrderItemJpaEntity(
                item.getProduct().getProductId(),
                item.getQuantity()
        );
    }

    /**
     * Reconstruye un elemento del pedido.
     *
     * @param entity entidad persistente
     * @return elemento del dominio
     */
    private OrderItem toDomainItem(
            OrderItemJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Order item JPA entity must not be null."
            );
        }

        Product product =
                productQueryRepository
                        .findById(
                                entity.getProductId()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Order product was not found."
                                )
                        );

        return new OrderItem(
                product,
                entity.getQuantity()
        );
    }

    /**
     * Localiza al comprador propietario.
     *
     * @param identification identificación del comprador
     * @return comprador encontrado
     */
    private Buyer findBuyer(
            String identification
    ) {
        User user =
                userRepository
                        .findByIdentification(
                                identification
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Order buyer was not found."
                                )
                        );

        if (!(user instanceof Buyer buyer)) {
            throw new IllegalStateException(
                    "Order owner must be a buyer."
            );
        }

        return buyer;
    }

    /**
     * Valida el pedido del dominio.
     *
     * @param order pedido validado
     */
    private void validateOrder(
            Order order
    ) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order must not be null."
            );
        }

        if (order.getBuyer() == null) {
            throw new IllegalStateException(
                    "Order buyer must not be null."
            );
        }

        if (order.getItems() == null) {
            throw new IllegalStateException(
                    "Order items must not be null."
            );
        }
    }

    /**
     * Valida la entidad persistente.
     *
     * @param entity entidad validada
     */
    private void validateEntity(
            OrderJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Order JPA entity must not be null."
            );
        }
    }

    /**
     * Valida el repositorio de usuarios.
     *
     * @param userRepository repositorio validado
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
     * Valida el repositorio de productos.
     *
     * @param productQueryRepository repositorio validado
     */
    private void validateProductRepository(
            ProductQueryRepository productQueryRepository
    ) {
        if (productQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Product query repository must not be null."
            );
        }
    }
}