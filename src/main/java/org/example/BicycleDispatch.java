package org.example;

public class BicycleDispatch implements DispatchStrategy {
    public boolean canHandle(Rider r, Order o, double distanceKm) {
        return distanceKm <= 10;
    }
}
