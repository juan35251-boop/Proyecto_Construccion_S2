package application.adapters.output.persistence.jpa.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA utilizada para almacenar carritos
 * en la base de datos relacional.
 *
 * El campo id es una clave técnica de la base de datos,
 * mientras que cartId representa la identidad del dominio.
 */
@Entity
@Table(name = "carts")
public class CartJpaEntity {

    /**
     * Clave primaria técnica.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador estable del carrito en el dominio.
     */
    @Column(
            name = "cart_id",
            nullable = false,
            unique = true,
            length = 36
    )
    private String cartId;

    /**
     * Identificación del comprador propietario.
     *
     * Se marca como única porque cada comprador mantiene
     * un único carrito activo.
     */
    @Column(
            name = "buyer_identification",
            nullable = false,
            unique = true,
            length = 100
    )
    private String buyerIdentification;

    /**
     * Elementos pertenecientes al carrito.
     *
     * Cascade permite almacenar y actualizar los elementos
     * cuando se almacena el carrito.
     *
     * orphanRemoval elimina de la tabla los elementos retirados.
     */
    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<CartItemJpaEntity> items =
            new ArrayList<>();

    /**
     * Constructor requerido por JPA.
     */
    protected CartJpaEntity() {
    }

    /**
     * Crea la representación persistente de un carrito.
     *
     * @param cartId identificador del carrito
     * @param buyerIdentification identificación del comprador
     */
    public CartJpaEntity(
            String cartId,
            String buyerIdentification
    ) {
        validateCartId(cartId);
        validateBuyerIdentification(
                buyerIdentification
        );

        this.cartId = cartId;
        this.buyerIdentification =
                buyerIdentification;
    }

    /**
     * Obtiene la clave técnica.
     *
     * @return clave primaria
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene el identificador del dominio.
     *
     * @return identificador del carrito
     */
    public String getCartId() {
        return cartId;
    }

    /**
     * Obtiene la identificación del comprador.
     *
     * @return identificación del propietario
     */
    public String getBuyerIdentification() {
        return buyerIdentification;
    }

    /**
     * Obtiene una copia de los elementos persistentes.
     *
     * @return elementos del carrito
     */
    public List<CartItemJpaEntity> getItems() {
        return List.copyOf(items);
    }

    /**
     * Agrega un elemento y establece la relación
     * bidireccional con el carrito.
     *
     * @param item elemento agregado
     */
    public void addItem(
            CartItemJpaEntity item
    ) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "Cart JPA item must not be null."
            );
        }

        item.assignCart(this);
        items.add(item);
    }

    /**
     * Reemplaza los elementos persistentes.
     *
     * Al limpiar la colección, orphanRemoval elimina
     * los registros que ya no pertenecen al carrito.
     *
     * @param newItems nuevos elementos
     */
    public void replaceItems(
            List<CartItemJpaEntity> newItems
    ) {
        if (newItems == null) {
            throw new IllegalArgumentException(
                    "Cart JPA items must not be null."
            );
        }

        items.clear();

        for (CartItemJpaEntity item : newItems) {
            addItem(item);
        }
    }

    /**
     * Valida el identificador del carrito.
     *
     * @param cartId identificador validado
     */
    private void validateCartId(
            String cartId
    ) {
        if (cartId == null || cartId.isBlank()) {
            throw new IllegalArgumentException(
                    "Cart identifier must not be empty."
            );
        }
    }

    /**
     * Valida la identificación del comprador.
     *
     * @param buyerIdentification identificación validada
     */
    private void validateBuyerIdentification(
            String buyerIdentification
    ) {
        if (buyerIdentification == null
                || buyerIdentification.isBlank()) {
            throw new IllegalArgumentException(
                    "Buyer identification must not be empty."
            );
        }
    }
}