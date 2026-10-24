package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.CartItemJpaEntity;
import application.adapters.output.persistence.jpa.entities.CartJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Product;
import application.domain.models.User;
import application.ports.output.ProductQueryRepository;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Convierte carritos entre el modelo del dominio
 * y las entidades utilizadas por JPA.
 *
 * Para reconstruir un carrito, localiza al comprador
 * y los productos mediante sus respectivos puertos.
 */
public class CartJpaMapper {

    private final UserRepository userRepository;
    private final ProductQueryRepository productQueryRepository;

    /**
     * Construye el mapper con los repositorios necesarios
     * para reconstruir los objetos del dominio.
     *
     * @param userRepository repositorio de usuarios
     * @param productQueryRepository repositorio de productos
     */
    public CartJpaMapper(
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
     * Convierte un carrito nuevo en una entidad JPA.
     *
     * @param cart carrito del dominio
     * @return entidad persistente
     */
    public CartJpaEntity toEntity(
            Cart cart
    ) {
        validateCart(cart);

        CartJpaEntity entity =
                new CartJpaEntity(
                        cart.getCartId(),
                        cart.getBuyer()
                                .getIdentification()
                );

        entity.replaceItems(
                toItemEntities(
                        cart.getItems()
                )
        );

        return entity;
    }

    /**
     * Actualiza una entidad existente con el estado
     * actual de un carrito.
     *
     * Esto permite que JPA ejecute una actualización
     * y no intente insertar otro carrito con el mismo cartId.
     *
     * @param cart carrito actualizado
     * @param entity entidad persistente existente
     * @return entidad actualizada
     */
    public CartJpaEntity updateEntity(
            Cart cart,
            CartJpaEntity entity
    ) {
        validateCart(cart);
        validateEntity(entity);

        if (!cart.getCartId()
                .equals(entity.getCartId())) {
            throw new IllegalStateException(
                    "Cart and JPA entity identifiers do not match."
            );
        }

        if (!cart.getBuyer()
                .getIdentification()
                .equals(
                        entity.getBuyerIdentification()
                )) {
            throw new IllegalStateException(
                    "Cart buyer cannot be changed."
            );
        }

        entity.replaceItems(
                toItemEntities(
                        cart.getItems()
                )
        );

        return entity;
    }

    /**
     * Convierte una entidad JPA en un carrito del dominio.
     *
     * @param entity entidad persistente
     * @return carrito reconstruido
     */
    public Cart toDomain(
            CartJpaEntity entity
    ) {
        validateEntity(entity);

        Buyer buyer =
                findBuyer(
                        entity.getBuyerIdentification()
                );

        List<CartItem> items =
                entity.getItems()
                        .stream()
                        .map(this::toDomainItem)
                        .toList();

        return new Cart(
                entity.getCartId(),
                buyer,
                items
        );
    }

    /**
     * Convierte los elementos del dominio
     * en entidades persistentes.
     */
    private List<CartItemJpaEntity> toItemEntities(
            List<CartItem> items
    ) {
        if (items == null) {
            throw new IllegalArgumentException(
                    "Cart items must not be null."
            );
        }

        return items.stream()
                .map(this::toEntityItem)
                .toList();
    }

    /**
     * Convierte un elemento del dominio.
     */
    private CartItemJpaEntity toEntityItem(
            CartItem item
    ) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "Cart item must not be null."
            );
        }

        if (item.getProduct() == null) {
            throw new IllegalStateException(
                    "Cart item product must not be null."
            );
        }

        return new CartItemJpaEntity(
                item.getProduct().getProductId(),
                item.getQuantity()
        );
    }

    /**
     * Reconstruye un elemento del carrito.
     */
    private CartItem toDomainItem(
            CartItemJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Cart item JPA entity must not be null."
            );
        }

        Product product =
                productQueryRepository
                        .findById(
                                entity.getProductId()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Cart product was not found."
                                )
                        );

        return new CartItem(
                product,
                entity.getQuantity()
        );
    }

    /**
     * Localiza y valida al comprador propietario.
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
                                        "Cart buyer was not found."
                                )
                        );

        if (!(user instanceof Buyer buyer)) {
            throw new IllegalStateException(
                    "Cart owner must be a buyer."
            );
        }

        return buyer;
    }

    /**
     * Valida el carrito recibido.
     */
    private void validateCart(
            Cart cart
    ) {
        if (cart == null) {
            throw new IllegalArgumentException(
                    "Cart must not be null."
            );
        }

        if (cart.getBuyer() == null) {
            throw new IllegalStateException(
                    "Cart buyer must not be null."
            );
        }
    }

    /**
     * Valida la entidad persistente.
     */
    private void validateEntity(
            CartJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Cart JPA entity must not be null."
            );
        }
    }

    /**
     * Valida el repositorio de usuarios.
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