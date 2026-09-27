package org.example;
import java.time.*;
public class Promotion {
    private final String code;
    private final PromotionStrategy strategy;
    private final LocalDateTime expiryDate;

    public Promotion(String code, PromotionStrategy strategy, LocalDateTime expiryDate) {
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("Invalid promotion code");

        if (strategy == null)
            throw new IllegalArgumentException("Invalid promotion strategy");

        this.code = code.toUpperCase();
        this.strategy = strategy;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {

        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    public String getCode() {
        return code;
    }

    public double calculateDiscount(double subtotal, double deliveryFee) {
        if (isExpired())
            throw new IllegalStateException("Promotion expired");
        return strategy.calculateDiscount(subtotal, deliveryFee);
    }
    public boolean isApplicable(Customer customer, Restaurant restaurant, Address address, double subtotal) {
        if (isExpired()) return false;
        return true;
    }

    public double discount(double subtotal, double deliveryFee) {
        return calculateDiscount(subtotal, deliveryFee);
    }
}
