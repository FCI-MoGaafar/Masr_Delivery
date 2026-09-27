package org.example;

public interface DispatchStrategy {
    boolean canHandle(Rider rider, Order order, double distanceKm);
}
