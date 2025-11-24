package com.adcaisse.delivery_route_optimizer.client;


import com.adcaisse.delivery_route_optimizer.model.Location;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class GraphHopperClient {

    private final String graphHopperUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GraphHopperClient(@Value("${graphhopper.url}") String graphHopperUrl) {
        this.graphHopperUrl = graphHopperUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Get distance matrix for a list of locations
     * @param locations List of delivery locations and driver starting points
     * @return distance matrix in meters
     * @throws Exception
     */
    public long[][] getDistanceMatrix(List<Location> locations) throws Exception {
        int n = locations.size();
        long[][] distanceMatrix = new long[n][n];

        for (int i = 0; i < n; i++) {
            Location from = locations.get(i);
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                }else{
                    Location to = locations.get(j);
                    distanceMatrix[i][j] = getDistance(from, to);
                }
            }
        }
        return  distanceMatrix;
    }

    /**
     * Get distance in meters between two locations using GET /route
     * @param from Source location
     * @param to Destination location
     * @return Distance in meters
     * @throws Exception if the API call fails
     */
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
        JsonNode path = jsonNode.get("paths").get(0);

        return path.get("distance").asLong(); // distance in meters
    }

    /**
     * Get the polyline route from origin to destination
     */
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
        JsonNode path = jsonNode.get("paths").get(0);

        return path.get("points").asText(); // encoded polyline
    }
}
