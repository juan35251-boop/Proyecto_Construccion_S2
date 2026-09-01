package application.domain.valueobjects;

import java.util.Objects;

/**
 * Representa una variante descriptiva de un producto.
 *
 * Una variante permite identificar una presentación específica, como
 * un color, una talla, una capacidad o una combinación de características.
 *
 * Es un objeto de valor porque se identifica por el contenido de su
 * descripción y no por una identidad propia. Dos variantes con la misma
 * descripción se consideran iguales.
 *
 * La clase es inmutable: después de crear una variante, su descripción
 * no puede cambiar.
 */
public final class ProductVariant {

    /**
     * Descripción que identifica la variante.
     *
     * El texto se almacena sin espacios al inicio ni al final.
     */
    private final String description;

    /**
     * Crea una variante a partir de su descripción.
     *
     * La descripción debe contener información. Antes de almacenarse,
     * se eliminan los espacios ubicados al inicio y al final mediante
     * {@link String#trim()}.
     *
     * @param description descripción de la variante
     *
     * @throws IllegalArgumentException si la descripción es nula,
     *                                  está vacía o solo contiene espacios
     */
    public ProductVariant(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Product variant description must not be empty."
            );
        }

        this.description = description.trim();
    }

    /**
     * Obtiene la descripción de la variante.
     *
     * @return la descripción normalizada
     */
    public String getDescription() {
        return description;
    }

    /**
     * Compara esta variante con otro objeto.
     *
     * Dos variantes se consideran iguales cuando ambas son instancias de
     * {@code ProductVariant} y tienen exactamente la misma descripción.
     *
     * @param other objeto que se desea comparar
     * @return {@code true} si ambos objetos representan el mismo valor;
     *         de lo contrario, {@code false}
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ProductVariant variant)) {
            return false;
        }

        return description.equals(variant.description);
    }

    /**
     * Genera el código hash de la variante a partir de su descripción.
     *
     * Este método es coherente con {@link #equals(Object)}: dos variantes
     * iguales producen el mismo código hash.
     *
     * @return el código hash de la descripción
     */
    @Override
    public int hashCode() {
        return Objects.hash(description);
    }

    /**
     * Devuelve la representación textual de la variante.
     *
     * @return la descripción de la variante
     */
    @Override
    public String toString() {
        return description;
    }
}