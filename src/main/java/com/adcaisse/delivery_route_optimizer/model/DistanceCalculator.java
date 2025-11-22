package com.adcaisse.delivery_route_optimizer.model;

public interface DistanceCalculator {
    
    long getDistance(Location from, Location to);
    
    default int getTravelTime(Location from, Location to) {
        long distanceInMeters = getDistance(from, to);
        // Assume average speed of 50 km/h = 833 meters per minute
        return (int) (distanceInMeters / 833);
    }
}
