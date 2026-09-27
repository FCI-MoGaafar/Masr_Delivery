package org.example;

public final class PlatformConfig {
    private static final PlatformConfig INSTANCE = new PlatformConfig();
    private final double baseDeliveryFee = 15.0;
    private final double extraKmFee = 3.0;
    private final double freeKm = 3.0;
    private final double serviceRate = 0.10;

    private PlatformConfig() {}
    public static PlatformConfig getInstance() { return INSTANCE; }
    public double getBaseDeliveryFee() { return baseDeliveryFee; }
    public double getExtraKmFee() { return extraKmFee; }
    public double getFreeKm() { return freeKm; }
    public double getServiceRate() { return serviceRate; }
}
