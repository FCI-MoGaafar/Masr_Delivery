package org.example;
import java.util.*;
public class OderBuilder {
    private int id;
    private Customer customer;
    private Restaurant restaurant;
    private String deliveryAddress;
    private final List<OrderLine> orderLines = new ArrayList<>();

    public OderBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public OderBuilder setCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public OderBuilder setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        return this;
    }

    public OderBuilder setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public OderBuilder addOrderLine(OrderLine orderLine) {

        if (orderLine == null)
            throw new IllegalArgumentException("Order line cannot be null");

        orderLines.add(orderLine);
        return this;
    }

    public Order build() {
        return new Order(id, customer, restaurant, deliveryAddress, orderLines);
    }
}
