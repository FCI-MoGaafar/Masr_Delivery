package org.example;

public class Rider {
    private final int id;
    private final String name;
    private final VehicleType vehicleType;
    private String district;
    private RiderStatus status;
    private int completedDeliveries;
    private Order activeOrder;

    public Rider(int id, String name, VehicleType vehicleType, String district) {
        if (id <= 0)
            throw new IllegalArgumentException("Invalid ID");

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Invalid name");

        if (vehicleType == null)
            throw new IllegalArgumentException("Invalid vehicle type");

        if (district == null || district.isBlank())
            throw new IllegalArgumentException("Invalid district");

        this.id = id;
        this.name = name;
        this.vehicleType = vehicleType;
        this.district = district;
        this.status = RiderStatus.OFF_DUTY;
        this.completedDeliveries = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getDistrict() {
        return district;
    }

    public RiderStatus getStatus() {
        return status;
    }

    public int getCompletedDeliveries() {
        return completedDeliveries;
    }

    public Order getActiveOrder() {
        return activeOrder;
    }

    public void goOnDuty() {
        if (activeOrder != null)
            throw new RiderBusyException("Rider is busy");

        status = RiderStatus.AVAILABLE;
    }

    public void goOffDuty() {
        if (activeOrder != null)
            throw new RiderBusyException("Rider is busy");

        status = RiderStatus.OFF_DUTY;
    }

    public void assignOrder(Order order) {

        if (activeOrder != null)
            throw new RiderBusyException("Rider already has an active order");

        if (status != RiderStatus.AVAILABLE)
            throw new RiderBusyException("Rider is not available");

        activeOrder = order;
        status = RiderStatus.BUSY;
    }

    public void completeDelivery() {

        if (activeOrder == null)
            throw new IllegalStateException("Rider has no active order");

        completedDeliveries++;

        activeOrder = null;

        status = RiderStatus.AVAILABLE;
    }
    public void setDistrict(String district) {

        if (district == null || district.isBlank())
            throw new IllegalArgumentException(
                    "Invalid district"
            );

        this.district = district;
    }
}
