package com.adcaisse.delivery_route_optimizer.client;


import com.adcaisse.delivery_route_optimizer.model.Location;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GraphHopperClient {

    private static final Logger logger = LoggerFactory.getLogger(GraphHopperClient.class);
    
    private final String graphHopperUrl;
    private final int maxConcurrentRequests;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GraphHopperClient(
            @Value("${graphhopper.url}") String graphHopperUrl,
            @Value("${graphhopper.max-concurrent-requests:10}") int maxConcurrentRequests,
            @Value("${graphhopper.request-timeout-seconds:10}") int timeoutSeconds) {
        this.graphHopperUrl = graphHopperUrl;
        this.maxConcurrentRequests = maxConcurrentRequests;
        
        logger.info("Initializing GraphHopperClient with URL: {}, max concurrent requests: {}, timeout: {}s",
                   graphHopperUrl, maxConcurrentRequests, timeoutSeconds);
        
        // Optimized HTTP client for parallel requests
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(timeoutSeconds))
            .build();
            
        this.objectMapper = new ObjectMapper();
    }

    public long[][] getDistanceMatrix(List<Location> locations) throws Exception {
        int n = locations.size();
        long[][] distanceMatrix = new long[n][n];
        
        // Set diagonal to 0
        for (int i = 0; i < n; i++) {
            distanceMatrix[i][i] = 0;
        }
        
        // Calculate total number of API calls needed
        int totalCalls = (n * (n - 1)) / 2;  // n choose 2 (upper triangle)
        
        logger.info("Starting parallel distance matrix calculation for {} locations ({} API calls with {} max concurrent requests)", 
                   n, totalCalls, maxConcurrentRequests);
        long startTime = System.currentTimeMillis();
        
        // Create custom executor with limited thread pool
        ExecutorService executor = Executors.newFixedThreadPool(maxConcurrentRequests);
        
        try {
            // Build list of all async tasks for upper triangle
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            AtomicInteger completedCalls = new AtomicInteger(0);
            
            // Create async tasks for upper triangle
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    final int fi = i;
                    final int fj = j;
                    final Location from = locations.get(i);
                    final Location to = locations.get(j);
                    
                    // Create async task with controlled executor
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            long distance = getDistance(from, to);
                            
                            // Store in both positions (symmetric)
                            distanceMatrix[fi][fj] = distance;
                            distanceMatrix[fj][fi] = distance;
                            
                            int completed = completedCalls.incrementAndGet();
                            if (completed % 50 == 0 || completed == totalCalls) {
                                logger.debug("Distance calculation progress: {}/{} completed ({} %)", 
                                           completed, totalCalls, (completed * 100) / totalCalls);
                            }
                        } catch (Exception e) {
                            logger.error("Failed to calculate distance from location {} to {}: {}", 
                                        from.getId(), to.getId(), e.getMessage());
                            // Set fallback distance (Haversine)
                            /*long fallbackDistance = calculateHaversineDistance(from, to);
                            distanceMatrix[fi][fj] = fallbackDistance;
                            distanceMatrix[fj][fi] = fallbackDistance;*/
                        }
                    }, executor);  // Use custom executor with controlled parallelism
                    
                    futures.add(future);
                }
            }
            
            // Wait for all async operations to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Distance matrix calculation completed in {}ms ({} calls, avg {}.{}ms per call)", 
                       duration, totalCalls, duration / Math.max(totalCalls, 1), 
                       (duration * 10 / Math.max(totalCalls, 1)) % 10);
            
            return distanceMatrix;
            
        } catch (Exception e) {
            logger.error("Error during parallel distance calculation", e);
            throw new Exception("Failed to calculate distance matrix", e);
        } finally {
            // Always shutdown executor to prevent thread leaks
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public long getDistance(Location from, Location to) throws Exception {
        String url = String.format("%s/route?point=%f,%f&point=%f,%f&profile=car",
                graphHopperUrl,
                from.getLatitude(), from.getLongitude(),
                to.getLatitude(), to.getLongitude());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode jsonNode = objectMapper.readTree(response.body());
        
        // Check for errors in response
        JsonNode pathsNode = jsonNode.get("paths");
        if (pathsNode == null || pathsNode.isEmpty()) {
            // GraphHopper couldn't find a route - check for error message
            JsonNode messageNode = jsonNode.get("message");
            String errorMsg = messageNode != null ? messageNode.asText() : "No route found";
            
            logger.warn("GraphHopper couldn't find route from ({}, {}) to ({}, {}): {}",
                    from.getLatitude(), from.getLongitude(),
                    to.getLatitude(), to.getLongitude(), errorMsg);
            
            // Fall back to Haversine distance (straight-line) as estimate
            long fallbackDistance = calculateHaversineDistance(from, to);
            logger.debug("Using Haversine fallback distance: {} meters", fallbackDistance);
            return fallbackDistance;
        }
        
        JsonNode path = pathsNode.get(0);
        return path.get("distance").asLong();
    }

    /**
     * Calculate straight-line (Haversine) distance between two points.
     * Used as fallback when GraphHopper can't find a route.
     * 
     * @param from Source location
     * @param to Destination location
     * @return Distance in meters
     */
    private long calculateHaversineDistance(Location from, Location to) {
        final int EARTH_RADIUS_METERS = 6_371_000; // Earth's radius in meters
        
        double lat1 = Math.toRadians(from.getLatitude());
        double lat2 = Math.toRadians(to.getLatitude());
        double deltaLat = Math.toRadians(to.getLatitude() - from.getLatitude());
        double deltaLon = Math.toRadians(to.getLongitude() - from.getLongitude());
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // Multiply by 1.3 to approximate road distance (roads are rarely straight)
        return (long) (EARTH_RADIUS_METERS * c * 1.3);
    }

    public String getRoutePolyline(Location from, Location to) throws Exception {
        String url = String.format("%s/route?point=%f,%f&point=%f,%f&profile=car&points_encoded=true",
                graphHopperUrl,
                from.getLatitude(), from.getLongitude(),
                to.getLatitude(), to.getLongitude());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode jsonNode = objectMapper.readTree(response.body());
        JsonNode pathsNode = jsonNode.get("paths");
        
        if (pathsNode == null || pathsNode.isEmpty()) {
            JsonNode messageNode = jsonNode.get("message");
            String errorMsg = messageNode != null ? messageNode.asText() : "No route found";
            throw new Exception("GraphHopper couldn't find route: " + errorMsg);
        }
        
        JsonNode path = pathsNode.get(0);
        return path.get("points").asText(); // encoded polyline
    }
}
