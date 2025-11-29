package com.adcaisse.delivery_route_optimizer.service.impl;

import com.adcaisse.delivery_route_optimizer.client.GraphHopperClient;
import com.adcaisse.delivery_route_optimizer.model.DistanceMatrix;
import com.adcaisse.delivery_route_optimizer.model.Location;
import com.adcaisse.delivery_route_optimizer.service.DistanceCalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of DistanceCalculatorService using GraphHopper for real-world routing.
 * All distances are calculated via GraphHopper API - no Haversine approximations.
 * Includes caching to avoid redundant API calls.
 */
@Service
public class DistanceCalculatorServiceImpl implements DistanceCalculatorService {
    
    private static final Logger logger = LoggerFactory.getLogger(DistanceCalculatorServiceImpl.class);
    
    private final GraphHopperClient graphHopperClient;
    private final ConcurrentHashMap<String, Long> distanceCache;
    
    public DistanceCalculatorServiceImpl(GraphHopperClient graphHopperClient) {
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
            // Get distance from GraphHopper API
            long distance = graphHopperClient.getDistance(from, to);
            
            // Cache the result (both directions since we use symmetric distances)
            distanceCache.put(cacheKey, distance);
            distanceCache.put(createCacheKey(to, from), distance);
            
            return distance;
        } catch (Exception e) {
            logger.error("GraphHopper API call failed for distance {} -> {}: {}",
                    from.getId(), to.getId(), e.getMessage());
            throw new RuntimeException("Failed to get distance from GraphHopper", e);
        }
    }
    
    private String createCacheKey(Location from, Location to) {
        return from.getId() + "->" + to.getId();
    }
    
    @Override
    public DistanceMatrix computeDistanceMatrix(List<Location> locations) {
        logger.info("Computing distance matrix for {} locations using GraphHopper...", locations.size());
        long startTime = System.currentTimeMillis();
        
        try {
            long[][] matrix = graphHopperClient.getDistanceMatrix(locations);
            
            // Also populate the local cache for getDistance() calls
            for (int i = 0; i < locations.size(); i++) {
                for (int j = 0; j < locations.size(); j++) {
                    if (i != j) {
                        String cacheKey = createCacheKey(locations.get(i), locations.get(j));
                        distanceCache.put(cacheKey, matrix[i][j]);
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("✅ Distance matrix computed in {}ms ({} locations, {} cache entries)", 
                    duration, locations.size(), distanceCache.size());
            
            return new DistanceMatrix(locations, matrix);
            
        } catch (Exception e) {
            logger.error("❌ Failed to compute distance matrix: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to compute distance matrix from GraphHopper", e);
        }
    }
    
    @Override
    public void clearCache() {
        distanceCache.clear();
        logger.info("Distance cache cleared");
    }
    
    @Override
    public int getCacheSize() {
        return distanceCache.size();
    }
}
