package org.example;

public class ComboComponent {
    private final MenuItem item;
    private final int quantity;
    public ComboComponent(MenuItem item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.item = item;
        this.quantity = quantity;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }
    public double totalPrice(){
        return item.getPrice()*quantity;
    }
}
