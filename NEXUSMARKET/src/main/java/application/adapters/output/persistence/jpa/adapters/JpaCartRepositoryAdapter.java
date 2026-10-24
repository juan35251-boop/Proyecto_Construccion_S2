package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.CartJpaEntity;
import application.adapters.output.persistence.jpa.mappers.CartJpaMapper;
import application.adapters.output.persistence.jpa.repositories.SpringDataCartRepository;
import application.domain.models.Cart;
import application.ports.output.CartRepository;
import application.ports.output.ProductQueryRepository;
import application.ports.output.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Adaptador JPA que implementa el puerto de salida
 * utilizado para almacenar y consultar carritos.
 *
 * El estado actual del carrito se conserva en MySQL.
 */
@Repository
@Transactional
public class JpaCartRepositoryAdapter
        implements CartRepository {

    private final SpringDataCartRepository
            springDataCartRepository;

    private final CartJpaMapper cartJpaMapper;

    /**
     * Construye el adaptador con sus dependencias.
     *
     * @param springDataCartRepository repositorio Spring Data
     * @param userRepository repositorio de usuarios
     * @param productQueryRepository repositorio de productos
     */
    public JpaCartRepositoryAdapter(
            SpringDataCartRepository springDataCartRepository,
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository
    ) {
        validateSpringDataRepository(
                springDataCartRepository
        );

        this.springDataCartRepository =
                springDataCartRepository;

        this.cartJpaMapper =
                new CartJpaMapper(
                        userRepository,
                        productQueryRepository
                );
    }

    /**
     * Guarda un carrito nuevo o actualiza uno existente.
     *
     * Si el cartId ya existe, se conserva la clave técnica
     * y solamente se reemplazan sus productos.
     *
     * @param cart carrito almacenado
     */
    @Override
    public void save(
            Cart cart
    ) {
        validateCart(cart);

        CartJpaEntity entity =
                springDataCartRepository
                        .findByCartId(
                                cart.getCartId()
                        )
                        .map(existingEntity ->
                                cartJpaMapper.updateEntity(
                                        cart,
                                        existingEntity
                                )
                        )
                        .orElseGet(() ->
                                cartJpaMapper.toEntity(cart)
                        );

        springDataCartRepository.save(entity);
    }

    /**
     * Busca un carrito mediante su cartId.
     *
     * @param cartId identificador del carrito
     * @return carrito encontrado o resultado vacío
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> findById(
            String cartId
    ) {
        validateText(
                cartId,
                "Cart identifier"
        );

        return springDataCartRepository
                .findByCartId(cartId)
                .map(cartJpaMapper::toDomain);
    }

    /**
     * Busca el carrito de un comprador.
     *
     * @param buyerIdentification identificación del comprador
     * @return carrito encontrado o resultado vacío
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> findByBuyerIdentification(
            String buyerIdentification
    ) {
        validateText(
                buyerIdentification,
                "Buyer identification"
        );

        return springDataCartRepository
                .findByBuyerIdentification(
                        buyerIdentification
                )
                .map(cartJpaMapper::toDomain);
    }

    /**
     * Comprueba si un comprador ya posee carrito.
     *
     * @param buyerIdentification identificación del comprador
     * @return true si existe
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByBuyerIdentification(
            String buyerIdentification
    ) {
        validateText(
                buyerIdentification,
                "Buyer identification"
        );

        return springDataCartRepository
                .existsByBuyerIdentification(
                        buyerIdentification
                );
    }

    /**
     * Valida el repositorio de Spring Data.
     */
    private void validateSpringDataRepository(
            SpringDataCartRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Spring Data cart repository must not be null."
            );
        }
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