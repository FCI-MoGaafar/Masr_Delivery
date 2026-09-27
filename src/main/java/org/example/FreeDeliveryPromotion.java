package org.example;

public class FreeDeliveryPromotion implements PromotionStrategy {

    @Override
    public double calculateDiscount(double subtotal, double deliveryFee) {
        return deliveryFee;
    }
}
