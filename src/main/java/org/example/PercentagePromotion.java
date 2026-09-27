package org.example;

public class PercentagePromotion implements PromotionStrategy {
    private final double percentage;
    private final double maxDiscount;

    public PercentagePromotion(double percentage, double maxDiscount) {

        if (percentage <= 0)
            throw new IllegalArgumentException("Invalid percentage");

        if (maxDiscount <= 0)
            throw new IllegalArgumentException("Invalid max discount");

        this.percentage = percentage;
        this.maxDiscount = maxDiscount;
    }

    @Override
    public double calculateDiscount(double subtotal, double deliveryFee) {

        double discount = subtotal * percentage / 100;

        return Math.min(discount, maxDiscount);
    }
}
