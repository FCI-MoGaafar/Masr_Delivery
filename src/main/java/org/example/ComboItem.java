package org.example;

import java.util.ArrayList;
import java.util.List;

public class ComboItem extends MenuItem{
    private List<ComboComponent> components;
    private double discount;

    public ComboItem(int id, String name, String category, double prepTimeMinutes, boolean available, double price, List<ComboComponent> components, double discount) {
        super(id, name, category, prepTimeMinutes, available,calculateBundlePrice(components, discount));
        if (components == null || components.isEmpty()) {
            throw new IllegalArgumentException("Combo must contain at least one item");
        }

        if (discount <=0||discount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        this.components = new ArrayList<>(components);
        this.discount = discount;
    }
    private static double calculateBundlePrice(List<ComboComponent> components, double discount){
        if (components == null || components.isEmpty()) {
            throw new IllegalArgumentException("Combo must contain at least one item");
        }

        if (discount <=0||discount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        double total=components.stream().mapToDouble(ComboComponent::totalPrice).sum();
        double discountPrice=discount*total/100;
        return total-discountPrice;

    }

    public List<ComboComponent> getComponents() {
        return components;
    }

    public double getDiscount() {
        return discount;
    }

    @Override
    public double calculatePrice(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        return getPrice()*quantity;
    }
}
