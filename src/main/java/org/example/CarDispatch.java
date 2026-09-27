package org.example;

public class CarDispatch implements DispatchStrategy {
    public boolean canHandle(Rider r, Order o, double distanceKm) {
        return true;
    }
}
