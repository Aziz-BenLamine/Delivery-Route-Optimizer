package com.adcaisse.delivery_route_optimizer.solver;

import com.adcaisse.delivery_route_optimizer.model.Customer;
import com.adcaisse.delivery_route_optimizer.model.Vehicle;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

/**
 * Constraint provider for Vehicle Routing Problem.
 * Defines both hard constraints (must be satisfied) and soft constraints (should be minimized).
 */
public class VehicleRoutingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
                vehicleCapacityConstraint(constraintFactory),
                
                // Soft constraints
                minimizeTotalDistanceConstraint(constraintFactory),
                minimizeVehicleUsageConstraint(constraintFactory)
        };
    }

    /**
     * Hard constraint: Vehicle capacity must not be exceeded
     * This constraint penalizes any vehicle that has more total demand than capacity
     */
    private Constraint vehicleCapacityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Vehicle.class)
                .filter(vehicle -> {
                    int totalDemand = vehicle.getTotalDemand();
                    int capacity = vehicle.getCapacity();
                    boolean exceeds = totalDemand > capacity;
                    if (exceeds) {
                        System.out.println("Vehicle " + vehicle.getId() + " exceeds capacity: " + 
                            totalDemand + " > " + capacity + " (excess: " + (totalDemand - capacity) + ")");
                    }
                    return exceeds;
                })
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        vehicle -> (long)(vehicle.getTotalDemand() - vehicle.getCapacity()))
                .asConstraint("Vehicle capacity constraint");
    }

    /**
     * Soft constraint: Minimize total travel distance
     */
    private Constraint minimizeTotalDistanceConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Vehicle.class)
                .filter(vehicle -> !vehicle.getCustomerList().isEmpty())
                .penalizeLong(HardSoftLongScore.ONE_SOFT,
                        vehicle -> calculateVehicleDistance(vehicle))
                .asConstraint("Minimize total distance");
    }

    /**
     * Soft constraint: Minimize number of vehicles used
     */
    private Constraint minimizeVehicleUsageConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Vehicle.class)
                .filter(vehicle -> !vehicle.getCustomerList().isEmpty())
                .penalizeLong(HardSoftLongScore.ONE_SOFT, vehicle -> 1000000L) // Large penalty per vehicle
                .asConstraint("Minimize vehicle usage");
    }

    /**
     * Calculate total distance for a vehicle including return to depot
     */
    private long calculateVehicleDistance(Vehicle vehicle) {
        if (vehicle.getCustomerList().isEmpty()) {
            return 0;
        }

        long totalDistance = 0;
        
        // Distance from depot to first customer
        Customer firstCustomer = vehicle.getCustomerList().get(0);
        totalDistance += calculateDistance(vehicle.getDepot(), firstCustomer.getLocation());

        // Distance between customers
        for (int i = 0; i < vehicle.getCustomerList().size() - 1; i++) {
            Customer from = vehicle.getCustomerList().get(i);
            Customer to = vehicle.getCustomerList().get(i + 1);
            totalDistance += calculateDistance(from.getLocation(), to.getLocation());
        }

        // Distance from last customer back to depot
        Customer lastCustomer = vehicle.getCustomerList().get(vehicle.getCustomerList().size() - 1);
        totalDistance += calculateDistance(lastCustomer.getLocation(), vehicle.getDepot());

        return totalDistance;
    }

    /**
     * Simple distance calculation using Euclidean distance.
     * In production, this should use the actual GraphHopper distance.
     */
    private long calculateDistance(com.adcaisse.delivery_route_optimizer.model.Location from, 
                                 com.adcaisse.delivery_route_optimizer.model.Location to) {
        double deltaLat = Math.toRadians(to.getLatitude() - from.getLatitude());
        double deltaLon = Math.toRadians(to.getLongitude() - from.getLongitude());
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(Math.toRadians(from.getLatitude())) * 
                Math.cos(Math.toRadians(to.getLatitude())) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double earthRadius = 6371000; // Earth radius in meters
        
        return (long) (earthRadius * c);
    }
}
