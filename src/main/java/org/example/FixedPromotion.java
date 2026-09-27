package org.example;

public class FixedPromotion implements PromotionStrategy {
    private final double amount;

    public FixedPromotion(double amount) {

        if (amount <= 0)
            throw new IllegalArgumentException(
                    "Invalid promotion amount"
            );

        this.amount = amount;
    }

    @Override
    public double calculateDiscount(double subtotal, double deliveryFee) {
        return Math.min(amount, subtotal);
    }
}
