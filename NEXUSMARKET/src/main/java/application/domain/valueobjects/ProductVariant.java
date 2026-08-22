package application.domain.valueobjects;

import java.util.Objects;

public final class ProductVariant {

    private final String description;

    public ProductVariant(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Product variant description must not be empty."
            );
        }

        this.description = description.trim();
    }

    public String getDescription() {
        return description;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }

    @Override
    public String toString() {
        return description;
    }
}