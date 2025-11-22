package com.adcaisse.delivery_route_optimizer.service;

import com.adcaisse.delivery_route_optimizer.client.GraphHopperClient;
import com.adcaisse.delivery_route_optimizer.model.DistanceCalculator;
import com.adcaisse.delivery_route_optimizer.model.Location;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of DistanceCalculator using GraphHopper for real-world routing.
 * Includes caching to avoid redundant API calls.
 */
@Service
public class GraphHopperDistanceCalculator implements DistanceCalculator {
    
    private final GraphHopperClient graphHopperClient;
    private final ConcurrentHashMap<String, Long> distanceCache;
    
    public GraphHopperDistanceCalculator() {
        this.graphHopperClient = new GraphHopperClient("http://localhost:8989");
        this.distanceCache = new ConcurrentHashMap<>();
    }
    
    @Override
    public long getDistance(Location from, Location to) {
        // Same location
        if (from.getId() == to.getId()) {
            return 0L;
        }
        
        // Check cache first
        String cacheKey = createCacheKey(from, to);
        Long cachedDistance = distanceCache.get(cacheKey);
        if (cachedDistance != null) {
            return cachedDistance;
        }
        
        try {
            // Create a mock route request using existing GraphHopper integration
            long distance = calculateDistanceViaGraphHopper(from, to);
            
            // Cache the result (both directions for optimization)
            distanceCache.put(cacheKey, distance);
            distanceCache.put(createCacheKey(to, from), distance);
            
            return distance;
        } catch (Exception e) {
            // Fallback to Euclidean distance if GraphHopper is unavailable
            System.err.println("GraphHopper unavailable, using Euclidean distance: " + e.getMessage());
            return calculateEuclideanDistance(from, to);
        }
    }
    
    private long calculateDistanceViaGraphHopper(Location from, Location to) throws Exception {
        try {
            // Use reflection to access private getDistance method from GraphHopperClient
            var method = graphHopperClient.getClass().getDeclaredMethod("getDistance", Location.class, Location.class);
            method.setAccessible(true);
            return (Long) method.invoke(graphHopperClient, from, to);
        } catch (Exception e) {
            // If reflection fails, fall back to Euclidean distance
            return calculateEuclideanDistance(from, to);
        }
    }
    
    private long calculateEuclideanDistance(Location from, Location to) {
        double deltaLat = Math.toRadians(to.getLatitude() - from.getLatitude());
        double deltaLon = Math.toRadians(to.getLongitude() - from.getLongitude());
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(Math.toRadians(from.getLatitude())) * 
                Math.cos(Math.toRadians(to.getLatitude())) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double earthRadius = 6371000; // Earth radius in meters
        
        return (long) (earthRadius * c);
    }
    
    private String createCacheKey(Location from, Location to) {
        return from.getId() + "->" + to.getId();
    }
    
    /**
     * Pre-populate cache with distance matrix for better performance during solving
     */
    public void preloadDistanceMatrix(java.util.List<Location> locations) {
        try {
            long[][] matrix = graphHopperClient.getDistanceMatrix(locations);
            
            for (int i = 0; i < locations.size(); i++) {
                for (int j = 0; j < locations.size(); j++) {
                    if (i != j) {
                        String cacheKey = createCacheKey(locations.get(i), locations.get(j));
                        distanceCache.put(cacheKey, matrix[i][j]);
                    }
                }
            }
            
            System.out.println("Preloaded distance matrix for " + locations.size() + " locations");
        } catch (Exception e) {
            System.err.println("Failed to preload distance matrix: " + e.getMessage());
        }
    }
    
    public void clearCache() {
        distanceCache.clear();
    }
    
    public int getCacheSize() {
        return distanceCache.size();
    }
}
