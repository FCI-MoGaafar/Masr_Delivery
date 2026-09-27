package org.example;

public class StandardItem extends MenuItem{
    public StandardItem(int id, String name, String category, double prepTimeMinutes, boolean available, double price) {
        super(id, name, category, prepTimeMinutes, available, price);
    }

    @Override
    public double calculatePrice(double quantity) {
        if(quantity <= 0){
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if(quantity-Math.floor(quantity)!=0){
            throw new IllegalArgumentException("Standard item quantity must be a whole number");
        }
        return getPrice()*quantity;
    }
}
