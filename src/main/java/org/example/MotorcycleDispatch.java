package org.example;

public class MotorcycleDispatch implements DispatchStrategy{
    public boolean canHandle(Rider r, Order o, double distanceKm) {
        return distanceKm <= 30;
    }

}
