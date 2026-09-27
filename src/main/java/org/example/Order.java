package org.example;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Order {
    private final int id;
    private final Customer customer;
    private final Restaurant restaurant;
    private final String deliveryAddress;
    private final List<OrderLine> orderLines;
    private final LocalDateTime placedAt;
    private OrderStatus status;
    private Rider rider;
    private Promotion promotion;
    private boolean paid = false;
    private double subtotal;
    private double deliveryFee;
    private double serviceFee;
    private double promotionDiscount;
    private double total;
    private LocalDateTime deliveredAt;
    public Order(int id, Customer customer, Restaurant restaurant, String deliveryAddress, List<OrderLine> orderLines) {
        if (id <= 0)
            throw new IllegalArgumentException("Invalid ID");

        if (customer == null)
            throw new IllegalArgumentException("Customer cannot be null");

        if (restaurant == null)
            throw new IllegalArgumentException("Restaurant cannot be null");

        if (deliveryAddress == null || deliveryAddress.isBlank())
            throw new IllegalArgumentException("Invalid delivery address");

        if (orderLines == null || orderLines.isEmpty())
            throw new IllegalArgumentException("Order must contain at least one item");

        this.id = id;
        this.customer = customer;
        this.restaurant = restaurant;
        this.deliveryAddress = deliveryAddress;
        this.orderLines = new ArrayList<>(orderLines);
        this.placedAt = LocalDateTime.now();
        this.status = OrderStatus.PLACED;
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public List<OrderLine> getOrderLines() {
        return Collections.unmodifiableList(orderLines);
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Rider getRider() {
        return rider;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public void assignRider(Rider rider) {

        if (rider == null)
            throw new IllegalArgumentException("Rider cannot be null");

        this.rider = rider;
    }

    public void changeStatus(OrderStatus newStatus) {

        if (newStatus == null)
            throw new IllegalArgumentException("Status cannot be null");

        if (!isValidTransition(this.status, newStatus))
            throw new IllegalStateException("Illegal order transition: " + this.status + " -> " + newStatus);

        this.status = newStatus;
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.PLACED)
            return next == OrderStatus.ACCEPTED || next == OrderStatus.CANCELLED;

        if (current == OrderStatus.ACCEPTED)
            return next == OrderStatus.PREPARING || next == OrderStatus.CANCELLED;

        if (current == OrderStatus.PREPARING)
            return next == OrderStatus.READY || next == OrderStatus.CANCELLED;

        if (current == OrderStatus.READY)
            return next == OrderStatus.ASSIGNED || next == OrderStatus.CANCELLED;

        if (current == OrderStatus.ASSIGNED)
            return next == OrderStatus.OUT_FOR_DELIVERY;

        if (current == OrderStatus.OUT_FOR_DELIVERY)
            return next == OrderStatus.DELIVERED;

        return false;
    }
    public void setPricing(double subtotal, double deliveryFee, double serviceFee, double promotionDiscount) {
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.serviceFee = serviceFee;
        this.promotionDiscount = promotionDiscount;
        this.total = Math.max(0, subtotal + deliveryFee + serviceFee - promotionDiscount);
    }

    public boolean isPaid() { return paid; }
    public void markPaid() { this.paid = true; }
    public double getSubtotal() { return subtotal; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getServiceFee() { return serviceFee; }
    public double getPromotionDiscount() { return promotionDiscount; }
    public double getTotal() { return total; }

    public long deliveryDurationMinutes() {
        LocalDateTime end = (status == OrderStatus.DELIVERED && deliveredAt != null) ? deliveredAt : LocalDateTime.now();
        return java.time.Duration.between(placedAt, end).toMinutes();
    }
}
