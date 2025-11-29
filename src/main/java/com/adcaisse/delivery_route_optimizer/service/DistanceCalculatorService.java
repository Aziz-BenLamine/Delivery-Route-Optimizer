package com.adcaisse.delivery_route_optimizer.service;

import com.adcaisse.delivery_route_optimizer.model.DistanceMatrix;
import com.adcaisse.delivery_route_optimizer.model.Location;

import java.util.List;

/**
 * Service interface for calculating distances between locations.
 * Implementations may use different distance calculation strategies
 * (e.g., GraphHopper for real road distances, Haversine for straight-line).
 */
public interface DistanceCalculatorService {

    /**
     * Get the distance between two locations.
     *
     * @param from Source location
     * @param to Destination location
     * @return Distance in meters
     */
    long getDistance(Location from, Location to);

    /**
     * Get the estimated travel time between two locations.
     *
     * @param from Source location
     * @param to Destination location
     * @return Travel time in minutes
     */
    default int getTravelTime(Location from, Location to) {
        long distanceInMeters = getDistance(from, to);
        // Assume average speed of 50 km/h = 833 meters per minute
        return (int) (distanceInMeters / 833);
    }

    /**
     * Compute a distance matrix for a list of locations.
     * Pre-computes all pairwise distances for O(1) lookups during optimization.
     *
     * @param locations List of all locations (depot + customers)
     * @return Pre-computed distance matrix
     */
    DistanceMatrix computeDistanceMatrix(List<Location> locations);

    /**
     * Clear any cached distance data.
     */
    void clearCache();

    /**
     * Get the current size of the distance cache.
     *
     * @return Number of cached distance entries
     */
    int getCacheSize();
}
