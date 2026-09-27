package org.example;

public abstract class MenuItem {
    private int id;
    private String name;
    private String category;
    private double prepTimeMinutes;
    private boolean Available;
    private double price;
    private double dailyStock = 0;

    public MenuItem(int id, String name, String category, double prepTimeMinutes, boolean available, double price) {
        if(id <= 0)
            throw new IllegalArgumentException("id must be greater than 0");
        if(name == null || name.isBlank())
            throw new IllegalArgumentException("name cannot be null or blank");
        if(category == null || category.isBlank())
            throw new IllegalArgumentException("category cannot be null or blank");
        if(prepTimeMinutes <= 0)
            throw new IllegalArgumentException("prepTimeMinutes must be greater than 0");
        if(price <= 0)
            throw new IllegalArgumentException("price must be greater than 0");
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.prepTimeMinutes = prepTimeMinutes;
        Available = available;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public boolean isAvailable() {
        return Available;
    }

    public double getPrice() {
        return price;
    }

    public void setAvailable(boolean available) {
        Available = available;
    }
    public double getDailyStock() {
        return dailyStock;
    }

    public void setDailyStock(double dailyStock) {
        this.dailyStock = dailyStock;
    }

    public void consumeStock(double quantity) {
        if (!Double.isInfinite(dailyStock)) {
            if (dailyStock < quantity) {
                throw new IllegalArgumentException("Insufficient stock");
            }
            dailyStock -= quantity;
        }
    }

    public abstract double calculatePrice(double quantity);
}
