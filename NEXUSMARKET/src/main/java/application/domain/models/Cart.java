package application.domain.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private final Buyer buyer;
    private final List<CartItem> items;

    public Cart(Buyer buyer) {
        validateBuyer(buyer);

        this.buyer = buyer;
        this.items = new ArrayList<>();
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean containsProduct(Product product) {
        return findItem(product) != null;
    }

    public void addProduct(Product product, int quantity) {
        validatePublishedProduct(product);

        CartItem existingItem = findItem(product);

        if (existingItem != null) {
            existingItem.increaseQuantity(quantity);
            return;
        }

        items.add(new CartItem(product, quantity));
    }

    public void changeProductQuantity(
            Product product,
            int newQuantity
    ) {
        CartItem item = findRequiredItem(product);
        item.changeQuantity(newQuantity);
    }

    public void removeProduct(Product product) {
        CartItem item = findRequiredItem(product);
        items.remove(item);
    }

    private CartItem findItem(Product product) {
        for (CartItem item : items) {
            if (item.belongsTo(product)) {
                return item;
            }
        }

        return null;
    }

    private CartItem findRequiredItem(Product product) {
        CartItem item = findItem(product);

        if (item == null) {
            throw new IllegalStateException(
                    "Product does not exist in the cart."
            );
        }

        return item;
    }

    private void validateBuyer(Buyer buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Cart must belong to a buyer."
            );
        }
    }

    private void validatePublishedProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Cart product must not be null."
            );
        }

        if (!product.isPublished()) {
            throw new IllegalStateException(
                    "Only published products can be added to the cart."
            );
        }
    }
}