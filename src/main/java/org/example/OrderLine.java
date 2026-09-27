package org.example;

public class OrderLine {
    private final MenuItem item;
    private final double quantity;

    public OrderLine(MenuItem item, double quantity) {

        if (item == null)
            throw new IllegalArgumentException(
                    "Item cannot be null"
            );

        if (quantity <= 0)
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );

        this.item = item;
        this.quantity = quantity;
    }

    public MenuItem getItem() {
        return item;
    }

    public double getQuantity() {
        return quantity;
    }

    public double calculatePrice() {
        return item.calculatePrice(quantity);
    }
}
