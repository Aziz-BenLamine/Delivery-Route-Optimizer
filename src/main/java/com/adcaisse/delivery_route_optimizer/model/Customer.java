package com.adcaisse.delivery_route_optimizer.model;

/**
 * Represents a customer that needs to be visited for delivery.
 * This is a problem fact in OptaPlanner terminology.
 */
public class Customer {
    
    private Long id;
    private String name;
    private Location location;
    private int demand; // Package count, weight, or volume
    private int serviceTime; // Time in minutes to serve this customer
    
    // Time window constraints
    private Integer readyTime; // Earliest delivery time in minutes from depot
    private Integer dueTime;   // Latest delivery time in minutes from depot
    
    public Customer() {
    }
    
    public Customer(Long id, String name, Location location, int demand) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.demand = demand;
        this.serviceTime = 10;
    }
    
    public Customer(Long id, String name, Location location, int demand, 
                   int serviceTime, Integer readyTime, Integer dueTime) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.demand = demand;
        this.serviceTime = serviceTime;
        this.readyTime = readyTime;
        this.dueTime = dueTime;
    }
    
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
    
    public int getServiceTime() {
        return serviceTime;
    }
    
    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }
    
    public Integer getReadyTime() {
        return readyTime;
    }
    
    public void setReadyTime(Integer readyTime) {
        this.readyTime = readyTime;
    }
    
    public Integer getDueTime() {
        return dueTime;
    }
    
    public void setDueTime(Integer dueTime) {
        this.dueTime = dueTime;
    }
    
    public boolean hasTimeWindow() {
        return readyTime != null && dueTime != null;
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", demand=" + demand +
                ", location=" + location +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id.equals(customer.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
