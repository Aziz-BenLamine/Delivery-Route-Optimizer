package com.adcaisse.delivery_route_optimizer.service;

import com.adcaisse.delivery_route_optimizer.model.DistanceMatrix;
import com.adcaisse.delivery_route_optimizer.model.Location;

import java.util.List;


public interface DistanceCalculatorService {

    long getDistance(Location from, Location to);


    default int getTravelTime(Location from, Location to) {
        long distanceInMeters = getDistance(from, to);
        return (int) (distanceInMeters / 833);
    }

    DistanceMatrix computeDistanceMatrix(List<Location> locations);
    void clearCache();
    int getCacheSize();
}
