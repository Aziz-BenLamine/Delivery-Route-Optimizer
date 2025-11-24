package com.adcaisse.delivery_route_optimizer.service;

import com.adcaisse.delivery_route_optimizer.client.GraphHopperClient;
import com.adcaisse.delivery_route_optimizer.model.DistanceCalculator;
import com.adcaisse.delivery_route_optimizer.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of DistanceCalculator using GraphHopper for real-world routing.
 * Includes caching to avoid redundant API calls.
 */
@Service
public class GraphHopperDistanceCalculator implements DistanceCalculator {
    
    private static final Logger logger = LoggerFactory.getLogger(GraphHopperDistanceCalculator.class);
    
    private final GraphHopperClient graphHopperClient;
    private final ConcurrentHashMap<String, Long> distanceCache;
    
    public GraphHopperDistanceCalculator(GraphHopperClient graphHopperClient) {
        this.graphHopperClient = graphHopperClient;
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
            logger.warn("GraphHopper unavailable for distance calculation from {} to {}, using Euclidean distance: {}", 
                    from.getId(), to.getId(), e.getMessage());
            return calculateEuclideanDistance(from, to);
        }
    }
    
    private long calculateDistanceViaGraphHopper(Location from, Location to) throws Exception {
        return graphHopperClient.getDistance(from, to);
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
            
            logger.info("Preloaded distance matrix for {} locations ({} cache entries)", 
                    locations.size(), distanceCache.size());
        } catch (Exception e) {
            logger.error("Failed to preload distance matrix: {}", e.getMessage(), e);
        }
    }
    
    public void clearCache() {
        distanceCache.clear();
    }
    
    public int getCacheSize() {
        return distanceCache.size();
    }
}
