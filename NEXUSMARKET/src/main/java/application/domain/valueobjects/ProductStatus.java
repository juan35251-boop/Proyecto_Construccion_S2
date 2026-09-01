package application.domain.valueobjects;

/**
 * Representa los estados posibles de un producto dentro de NexusMarket.
 *
 * El estado determina si el producto está disponible para agregarse
 * a un carrito o si fue retirado temporal o permanentemente.
 */
public enum ProductStatus {

    /**
     * Indica que el producto está publicado y puede agregarse
     * a un carrito de compras.
     */
    PUBLISHED,

    /**
     * Indica que el producto está suspendido temporalmente.
     *
     * Mientras permanezca en este estado, no puede agregarse
     * a un carrito.
     */
    SUSPENDED,

    /**
     * Indica que el producto fue retirado de manera definitiva
     * del catálogo comercial.
     *
     * Un producto descontinuado no puede agregarse a un carrito.
     */
    DISCONTINUED
}