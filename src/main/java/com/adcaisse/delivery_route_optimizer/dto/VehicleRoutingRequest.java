package com.adcaisse.delivery_route_optimizer.dto;

import com.adcaisse.delivery_route_optimizer.model.Location;
import java.util.List;

/**
 * DTO for vehicle routing optimization requests
 */
public class VehicleRoutingRequest {
    private Location depot;
    private List<Location> customerLocations;
    private List<Integer> vehicleCapacities;
    private List<Integer> customerDemands;

    // Constructors
    public VehicleRoutingRequest() {}

    public VehicleRoutingRequest(Location depot, List<Location> customerLocations, 
                                List<Integer> vehicleCapacities, List<Integer> customerDemands) {
        this.depot = depot;
        this.customerLocations = customerLocations;
        this.vehicleCapacities = vehicleCapacities;
        this.customerDemands = customerDemands;
    }

    // Getters and setters
    public Location getDepot() {
        return depot;
    }

    public void setDepot(Location depot) {
        this.depot = depot;
    }

    public List<Location> getCustomerLocations() {
        return customerLocations;
    }

    public void setCustomerLocations(List<Location> customerLocations) {
        this.customerLocations = customerLocations;
    }

    public List<Integer> getVehicleCapacities() {
        return vehicleCapacities;
    }

    public void setVehicleCapacities(List<Integer> vehicleCapacities) {
        this.vehicleCapacities = vehicleCapacities;
    }

    public List<Integer> getCustomerDemands() {
        return customerDemands;
    }

    public void setCustomerDemands(List<Integer> customerDemands) {
        this.customerDemands = customerDemands;
    }
}
