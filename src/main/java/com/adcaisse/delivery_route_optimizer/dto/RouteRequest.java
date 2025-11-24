package com.adcaisse.delivery_route_optimizer.dto;

import com.adcaisse.delivery_route_optimizer.model.Location;

/**
 * DTO for route requests between two points
 */
public class RouteRequest {
    private Location from;
    private Location to;

    // Constructors
    public RouteRequest() {}

    public RouteRequest(Location from, Location to) {
        this.from = from;
        this.to = to;
    }

    // Getters and setters
    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }
}
