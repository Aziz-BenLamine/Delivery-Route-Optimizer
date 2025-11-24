package com.adcaisse.delivery_route_optimizer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a customer that needs to be visited for delivery.
 * This is a problem fact in OptaPlanner terminology.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    private Long id;
    private String name;
    private Location location;
    private int demand; // Package count, weight, or volume
    private int serviceTime = 10; // Time in minutes to serve this customer
    
    // Time window constraints
    private Integer readyTime; // Earliest delivery time in minutes from depot
    private Integer dueTime;   // Latest delivery time in minutes from depot
    
    public Customer(Long id, String name, Location location, int demand) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.demand = demand;
        this.serviceTime = 10;
    }
    
    public boolean hasTimeWindow() {
        return readyTime != null && dueTime != null;
    }
}
