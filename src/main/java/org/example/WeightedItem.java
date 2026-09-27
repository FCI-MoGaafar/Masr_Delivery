package org.example;

public class WeightedItem extends MenuItem{
    public WeightedItem(int id, String name, String category, double prepTimeMinutes, boolean available, double price) {
        super(id, name, category, prepTimeMinutes, available, price);
    }

    @Override
    public double calculatePrice(double quantity) {
        if(quantity<=0){
            throw new IllegalArgumentException("Kilograms must be a positive number");
        }
        return getPrice()*quantity;
    }
}
