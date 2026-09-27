package org.example;

import java.awt.*;
import java.util.List;

public class FactoryMenuItem {
    public static MenuItem createMenuItem( String type, int id, String name, double price, String category, int preparationTime, boolean available, List<ComboComponent>components, double discountPercentage) {
        if(type.equalsIgnoreCase("standard")) {
            return new StandardItem(id,name,category,preparationTime,available,price);
        }
        else if(type.equalsIgnoreCase("combo")) {
            return new ComboItem(id,name,category,preparationTime,available,price,components,discountPercentage);
        }
        else if (type.equalsIgnoreCase("weighted")) {
            return new WeightedItem(id,name,category,preparationTime,available,price);
        }
        else
            throw new IllegalArgumentException("Invalid type");
    }
}
