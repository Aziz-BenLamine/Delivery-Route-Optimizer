package com.adcaisse.delivery_route_optimizer.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pre-computed distance matrix for O(1) lookups during OptaPlanner constraint evaluation.
 * This stores GraphHopper-calculated distances indexed by location IDs.
 */
public class DistanceMatrix {
    
    private final long[][] matrix;
    private final Map<Long, Integer> locationIdToIndex;
    
    /**
     * Create a DistanceMatrix from a list of locations and their pre-computed distances.
     * 
     * @param locations List of locations (order must match matrix indices)
     * @param matrix Pre-computed distance matrix from GraphHopper
     */
    public DistanceMatrix(List<Location> locations, long[][] matrix) {
        this.matrix = matrix;
        this.locationIdToIndex = new HashMap<>();
        
        for (int i = 0; i < locations.size(); i++) {
            locationIdToIndex.put(locations.get(i).getId(), i);
        }
    }
    
    /**
     * Get distance between two locations by their IDs.
     * O(1) lookup time.
     * 
     * @param fromId Source location ID
     * @param toId Destination location ID
     * @return Distance in meters, or -1 if locations not found
     */
    public long getDistance(long fromId, long toId) {
        Integer fromIndex = locationIdToIndex.get(fromId);
        Integer toIndex = locationIdToIndex.get(toId);
        
        if (fromIndex == null || toIndex == null) {
            return -1; // Location not in matrix
        }
        
        return matrix[fromIndex][toIndex];
    }
    
    /**
     * Get distance between two Location objects.
     * O(1) lookup time.
     * 
     * @param from Source location
     * @param to Destination location
     * @return Distance in meters, or -1 if locations not found
     */
    public long getDistance(Location from, Location to) {
        return getDistance(from.getId(), to.getId());
    }
    
    /**
     * Check if a location exists in this matrix.
     */
    public boolean hasLocation(long locationId) {
        return locationIdToIndex.containsKey(locationId);
    }
    
    /**
     * Get the number of locations in this matrix.
     */
    public int size() {
        return locationIdToIndex.size();
    }
}
