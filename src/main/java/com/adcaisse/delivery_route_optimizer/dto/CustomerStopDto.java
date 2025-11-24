package com.adcaisse.delivery_route_optimizer.dto;

import com.adcaisse.delivery_route_optimizer.model.Location;

/**
 * DTO representing a customer stop in a vehicle route
 */
public class CustomerStopDto {
    private Long customerId;
    private String customerName;
    private Location location;
    private int demand;

    // Constructors
    public CustomerStopDto() {}

    public CustomerStopDto(Long customerId, String customerName, Location location, int demand) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.location = location;
        this.demand = demand;
    }

    // Getters and setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }
}
