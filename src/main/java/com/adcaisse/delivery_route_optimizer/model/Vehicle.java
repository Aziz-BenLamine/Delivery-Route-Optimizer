package com.adcaisse.delivery_route_optimizer.model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * Planning Entity representing a Vehicle with a list of customers to visit.
 * Uses the Planning List Variable approach which is simpler than chained variables.
 */
@PlanningEntity
public class Vehicle {
    
    private Long id;
    private String name;
    private int capacity; // Maximum capacity in kg or number of packages
    private Location depot; // Starting and ending location
    
    @PlanningListVariable(valueRangeProviderRefs = {"customerRange"})
    private List<Customer> customerList;
    
    public Vehicle() {
        this.customerList = new ArrayList<>();
    }
    
    public Vehicle(Long id, String name, int capacity, Location depot) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.depot = depot;
        this.customerList = new ArrayList<>();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public Location getDepot() {
        return depot;
    }
    
    public void setDepot(Location depot) {
        this.depot = depot;
    }
    
    public List<Customer> getCustomerList() {
        return customerList;
    }
    
    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }
    
    // Helper methods for distance calculation
    public long getTotalDistance(DistanceCalculator distanceCalculator) {
        if (customerList.isEmpty()) {
            return 0;
        }
        
        long totalDistance = 0;
        Location previousLocation = depot;
        
        for (Customer customer : customerList) {
            totalDistance += distanceCalculator.getDistance(previousLocation, customer.getLocation());
            previousLocation = customer.getLocation();
        }
        
        // Return to depot
        totalDistance += distanceCalculator.getDistance(previousLocation, depot);
        return totalDistance;
    }
    
    public int getTotalDemand() {
        return customerList.stream()
                .mapToInt(Customer::getDemand)
                .sum();
    }
    
    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", customers=" + customerList.size() +
                '}';
    }
}
