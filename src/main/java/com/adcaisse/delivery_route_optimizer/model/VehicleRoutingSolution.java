package com.adcaisse.delivery_route_optimizer.model;

import com.adcaisse.delivery_route_optimizer.service.DistanceCalculatorService;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

import java.util.List;

/**
 * Planning Solution class that represents both the problem and the solution.
 * Contains all vehicles (planning entities) and customers (problem facts).
 */
@PlanningSolution
public class VehicleRoutingSolution {
    
    private String name;
    
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "customerRange")
    private List<Customer> customerList;
    
    @PlanningEntityCollectionProperty
    private List<Vehicle> vehicleList;
    
    @PlanningScore
    private HardSoftLongScore score;
    
    private DistanceCalculatorService distanceCalculator;
    
    /**
     * Pre-computed distance matrix for O(1) lookups during constraint evaluation.
     * This is populated with GraphHopper distances before solving.
     */
    @ProblemFactProperty
    private DistanceMatrix distanceMatrix;
    
    public VehicleRoutingSolution() {
    }
    
    public VehicleRoutingSolution(String name, List<Customer> customerList, 
                                 List<Vehicle> vehicleList) {
        this.name = name;
        this.customerList = customerList;
        this.vehicleList = vehicleList;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Customer> getCustomerList() {
        return customerList;
    }
    
    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }
    
    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }
    
    public void setVehicleList(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }
    
    public HardSoftLongScore getScore() {
        return score;
    }
    
    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }
    
    public DistanceCalculatorService getDistanceCalculator() {
        return distanceCalculator;
    }
    
    public void setDistanceCalculator(DistanceCalculatorService distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }
    
    public DistanceMatrix getDistanceMatrix() {
        return distanceMatrix;
    }
    
    public void setDistanceMatrix(DistanceMatrix distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    // Helper methods for analysis
    public long getTotalDistance() {
        if (distanceCalculator == null) {
            return 0;
        }
        
        return vehicleList.stream()
                .mapToLong(vehicle -> vehicle.getTotalDistance(distanceCalculator))
                .sum();
    }
    
    public int getTotalCustomers() {
        return vehicleList.stream()
                .mapToInt(vehicle -> vehicle.getCustomerList().size())
                .sum();
    }
    
    public boolean isFeasible() {
        return score != null && score.hardScore() >= 0;
    }
    
    @Override
    public String toString() {
        return "VehicleRoutingSolution{" +
                "name='" + name + '\'' +
                ", vehicles=" + vehicleList.size() +
                ", customers=" + customerList.size() +
                ", score=" + score +
                ", totalDistance=" + getTotalDistance() +
                '}';
    }
}
