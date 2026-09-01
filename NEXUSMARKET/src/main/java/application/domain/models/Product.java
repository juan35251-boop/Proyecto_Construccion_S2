package application.domain.models;

import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un producto ofrecido dentro de NexusMarket.
 *
 * Cada producto tiene un tipo, un estado de publicación y una colección
 * opcional de variantes. El producto puede ser físico o digital, y solamente
 * puede agregarse a un carrito cuando se encuentra publicado.
 *
 * El tipo del producto no puede cambiar después de su creación, pero su
 * estado y sus variantes pueden administrarse mediante métodos controlados.
 */
public class Product {

    /**
     * Tipo del producto.
     *
     * Determina si el producto es físico o digital y no puede reemplazarse
     * después de crear el producto.
     */
    private final ProductType productType;

    /**
     * Variantes asociadas al producto.
     *
     * Una variante puede representar una presentación o combinación
     * específica de las características del producto.
     */
    private final List<ProductVariant> variants;

    /**
     * Estado actual del producto.
     *
     * Determina, entre otras cosas, si está publicado y disponible
     * para agregarse a un carrito.
     */
    private ProductStatus status;

    /**
     * Crea un producto con un tipo y un estado inicial.
     *
     * El producto se crea inicialmente sin variantes.
     *
     * @param productType tipo del producto
     * @param status estado inicial del producto
     *
     * @throws IllegalArgumentException si el tipo del producto es nulo
     * @throws IllegalArgumentException si el estado es nulo
     */
    public Product(ProductType productType, ProductStatus status) {
        validateProductType(productType);
        validateStatus(status);

        this.productType = productType;
        this.status = status;
        this.variants = new ArrayList<>();
    }

    /**
     * Obtiene el tipo del producto.
     *
     * @return el tipo del producto
     */
    public ProductType getProductType() {
        return productType;
    }

    /**
     * Obtiene el estado actual del producto.
     *
     * @return el estado del producto
     */
    public ProductStatus getStatus() {
        return status;
    }

    /**
     * Obtiene una copia no modificable de las variantes.
     *
     * Esto impide agregar o eliminar variantes directamente desde fuera
     * de la clase. Las modificaciones deben realizarse mediante los
     * métodos definidos por el producto.
     *
     * @return una copia de las variantes registradas
     */
    public List<ProductVariant> getVariants() {
        return List.copyOf(variants);
    }

    /**
     * Cambia el estado actual del producto.
     *
     * @param newStatus nuevo estado del producto
     *
     * @throws IllegalArgumentException si el nuevo estado es nulo
     */
    public void changeStatus(ProductStatus newStatus) {
        validateStatus(newStatus);
        this.status = newStatus;
    }

    /**
     * Agrega una variante al producto.
     *
     * La variante no puede ser nula ni estar previamente registrada.
     *
     * @param variant variante que se desea agregar
     *
     * @throws IllegalArgumentException si la variante es nula
     * @throws IllegalStateException si la variante ya está registrada
     */
    public void addVariant(ProductVariant variant) {
        validateVariant(variant);

        if (variants.contains(variant)) {
            throw new IllegalStateException(
                    "Product variant already exists."
            );
        }

        variants.add(variant);
    }

    /**
     * Elimina una variante del producto.
     *
     * @param variant variante que se desea eliminar
     * @return {@code true} si la variante existía y fue eliminada;
     *         {@code false} si no estaba registrada
     *
     * @throws IllegalArgumentException si la variante es nula
     */
    public boolean removeVariant(ProductVariant variant) {
        validateVariant(variant);
        return variants.remove(variant);
    }

    /**
     * Determina si una variante está registrada en el producto.
     *
     * Si la variante recibida es nula, devuelve {@code false}.
     *
     * @param variant variante que se desea buscar
     * @return {@code true} si la variante está registrada;
     *         de lo contrario, {@code false}
     */
    public boolean hasVariant(ProductVariant variant) {
        return variant != null && variants.contains(variant);
    }

    /**
     * Indica si el producto tiene al menos una variante registrada.
     *
     * @return {@code true} si contiene variantes;
     *         de lo contrario, {@code false}
     */
    public boolean hasVariants() {
        return !variants.isEmpty();
    }

    /**
     * Indica si el producto requiere existencia y despacho físico.
     *
     * @return {@code true} si el tipo es
     *         {@link ProductType#PHYSICAL}; de lo contrario,
     *         {@code false}
     */
    public boolean isPhysical() {
        return productType == ProductType.PHYSICAL;
    }

    /**
     * Indica si el producto se entrega de manera digital.
     *
     * @return {@code true} si el tipo es
     *         {@link ProductType#DIGITAL}; de lo contrario,
     *         {@code false}
     */
    public boolean isDigital() {
        return productType == ProductType.DIGITAL;
    }

    /**
     * Indica si el producto se encuentra publicado.
     *
     * Un producto publicado puede agregarse a un carrito.
     *
     * @return {@code true} si su estado es
     *         {@link ProductStatus#PUBLISHED}; de lo contrario,
     *         {@code false}
     */
    public boolean isPublished() {
        return status == ProductStatus.PUBLISHED;
    }

    /**
     * Valida que el producto tenga un tipo definido.
     *
     * @param productType tipo que se desea validar
     *
     * @throws IllegalArgumentException si el tipo es nulo
     */
    private void validateProductType(ProductType productType) {
        if (productType == null) {
            throw new IllegalArgumentException(
                    "Product type must not be null."
            );
        }
    }

    /**
     * Valida que el producto tenga un estado definido.
     *
     * @param status estado que se desea validar
     *
     * @throws IllegalArgumentException si el estado es nulo
     */
    private void validateStatus(ProductStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Product status must not be null."
            );
        }
    }

    /**
     * Valida que una variante no sea nula.
     *
     * @param variant variante que se desea validar
     *
     * @throws IllegalArgumentException si la variante es nula
     */
    private void validateVariant(ProductVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException(
                    "Product variant must not be null."
            );
        }
    }
}