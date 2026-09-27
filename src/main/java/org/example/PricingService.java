package org.example;

public class PricingService {
    public double calculateSubtotal(Order order) {
        return order.getOrderLines().stream().mapToDouble(OrderLine::calculatePrice).sum();
    }

    public double calculateDeliveryFee(Restaurant restaurant, String deliveryDistrict, double distanceKm) {
        double fee = 15.0;
        if (distanceKm > 3)
            fee += (distanceKm - 3) * 3;

        return fee;
    }

    public double calculateServiceFee(double subtotal) {
        return Math.round(subtotal * 0.10 * 100.0) / 100.0;
    }

    public double calculateTotal(Order order, String deliveryDistrict, double distanceKm, double promotionDiscount) {
        double subtotal = calculateSubtotal(order);
        double deliveryFee = calculateDeliveryFee(order.getRestaurant(), deliveryDistrict, distanceKm);

        double serviceFee = calculateServiceFee(subtotal);

        double total = subtotal + deliveryFee + serviceFee - promotionDiscount;

        return Math.max(total, 0);
    }
}
