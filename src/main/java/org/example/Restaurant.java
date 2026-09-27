package org.example;

import java.util.HashSet;

public class Restaurant {
    private final int id;
    private  String name;
    private  String location;
    private double rating;
    private boolean open;
    private final Menu menu;
    private final HashSet<String> categories;

    public Restaurant(int id, String name, String location, double rating, Menu menu, HashSet<String> categories) {
        if (id<=0)
            throw new IllegalArgumentException("id must be greater than 0");
        if (name==null||name.isBlank())
            throw new IllegalArgumentException("name cannot be null or blank");
        if (location==null||location.isBlank())
            throw new IllegalArgumentException("location cannot be null or blank");
        if(rating<0||rating>5){
            throw new IllegalArgumentException("Invalid rating");
        }
        if (categories==null||categories.isEmpty())
            throw new IllegalArgumentException("Restaurant must have at least one category");
        this.id = id;
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.open = true;
        this.categories = new HashSet<>(categories);
        this.menu = new Menu();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public Menu getMenu() {
        return menu;
    }

    public double getRating() {
        return rating;
    }

    public boolean isOpen() {
        return open;
    }

    public HashSet<String> getCategories() {
        return new HashSet<>(categories);
    }
    public boolean addCategory(String category) {
        if(categories==null||category.isBlank()){
            return false;
        }
        String existItem= categories.stream().filter(s->s.equalsIgnoreCase(category)).findFirst().orElse(null);
        if(existItem==null){
            categories.add(category);
            return true;
        }
        return false;
    }
    public boolean removeCategory(String category) {
        if(categories==null||category.isBlank()){
            return false;
        }
        return categories.removeIf(s->s.equalsIgnoreCase(category));
    }
    public void setRating(double rating) {
        if(rating<0||rating>5){
            throw new IllegalArgumentException("Invalid rating");
        }
        this.rating = rating;
    }
    public void setOpen(boolean open) {
        this.open = true;
    }
    public void setClose(boolean close) {
        this.open = false;
    }

    public void setLocation(String location) {
        if(location==null||location.isBlank())
            throw new IllegalArgumentException("location cannot be null or blank");
        this.location = location;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", rating=" + rating +
                ", open=" + open +
                ", categories=" + categories +
                '}';
    }
}
