package com.adcaisse.delivery_route_optimizer.dto;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import java.util.List;

/**
 * DTO for vehicle routing solution results
 */
public class VehicleRoutingSolutionDto {
    private HardSoftLongScore score;
    private long totalDistance;
    private int totalCustomers;
    private boolean feasible;
    private List<VehicleRouteDto> routes;

    // Constructors
    public VehicleRoutingSolutionDto() {}

    public VehicleRoutingSolutionDto(HardSoftLongScore score, long totalDistance, 
                                    int totalCustomers, boolean feasible, 
                                    List<VehicleRouteDto> routes) {
        this.score = score;
        this.totalDistance = totalDistance;
        this.totalCustomers = totalCustomers;
        this.feasible = feasible;
        this.routes = routes;
    }

    // Getters and setters
    public HardSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

    public long getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(long totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public boolean isFeasible() {
        return feasible;
    }

    public void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }

    public List<VehicleRouteDto> getRoutes() {
        return routes;
    }

    public void setRoutes(List<VehicleRouteDto> routes) {
        this.routes = routes;
    }
}
