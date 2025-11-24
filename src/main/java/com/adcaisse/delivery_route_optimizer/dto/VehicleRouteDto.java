package com.adcaisse.delivery_route_optimizer.dto;

import java.util.List;

/**
 * DTO for vehicle route information
 */
public class VehicleRouteDto {
    private Long vehicleId;
    private String vehicleName;
    private int capacity;
    private int totalDemand;
    private long distance;
    private List<CustomerStopDto> stops;

    // Constructors
    public VehicleRouteDto() {}

    public VehicleRouteDto(Long vehicleId, String vehicleName, int capacity, 
                          int totalDemand, long distance, List<CustomerStopDto> stops) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.capacity = capacity;
        this.totalDemand = totalDemand;
        this.distance = distance;
        this.stops = stops;
    }

    // Getters and setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getTotalDemand() {
        return totalDemand;
    }

    public void setTotalDemand(int totalDemand) {
        this.totalDemand = totalDemand;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public List<CustomerStopDto> getStops() {
        return stops;
    }

    public void setStops(List<CustomerStopDto> stops) {
        this.stops = stops;
    }
}
