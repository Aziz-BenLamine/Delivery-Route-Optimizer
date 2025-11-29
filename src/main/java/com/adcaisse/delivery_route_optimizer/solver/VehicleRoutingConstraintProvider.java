package com.adcaisse.delivery_route_optimizer.solver;

import com.adcaisse.delivery_route_optimizer.model.Customer;
import com.adcaisse.delivery_route_optimizer.model.DistanceMatrix;
import com.adcaisse.delivery_route_optimizer.model.Location;
import com.adcaisse.delivery_route_optimizer.model.Vehicle;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constraint provider for Vehicle Routing Problem.
 * Defines both hard constraints (must be satisfied) and soft constraints (should be minimized).
 * 
 * Uses pre-computed GraphHopper distances from DistanceMatrix for accurate route optimization.
 */
public class VehicleRoutingConstraintProvider implements ConstraintProvider {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRoutingConstraintProvider.class);

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
     */
    private Constraint vehicleCapacityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Vehicle.class)
                .filter(vehicle -> {
                    int totalDemand = vehicle.getTotalDemand();
                    int capacity = vehicle.getCapacity();
                    boolean exceeds = totalDemand > capacity;
                    if (exceeds) {
                        logger.debug("Vehicle {} exceeds capacity: {} > {} (excess: {})", 
                                vehicle.getId(), totalDemand, capacity, (totalDemand - capacity));
                    }
                    return exceeds;
                })
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        vehicle -> (long)(vehicle.getTotalDemand() - vehicle.getCapacity()))
                .asConstraint("Vehicle capacity constraint");
    }

    /**
     * Soft constraint: Minimize total travel distance.
     * Uses pre-computed GraphHopper distances from DistanceMatrix for accurate optimization.
     */
    private Constraint minimizeTotalDistanceConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Vehicle.class)
                .filter(vehicle -> !vehicle.getCustomerList().isEmpty())
                .penalizeLong(HardSoftLongScore.ONE_SOFT,
                        (vehicle) -> calculateVehicleTotalDistance(vehicle))
                .asConstraint("Minimize total distance");
    }

    /**
     * Soft constraint: Minimize number of vehicles used
     */
    private Constraint minimizeVehicleUsageConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Vehicle.class)
                .filter(vehicle -> !vehicle.getCustomerList().isEmpty())
                .penalizeLong(HardSoftLongScore.ONE_SOFT, vehicle -> 1000000L)
                .asConstraint("Minimize vehicle usage");
    }

    /**
     * Calculate total distance for a vehicle's route using pre-computed GraphHopper distances.
     * Accesses the DistanceMatrix via a static holder (set before solving).
     */
    private long calculateVehicleTotalDistance(Vehicle vehicle) {
        if (vehicle.getCustomerList().isEmpty()) {
            return 0;
        }
        
        DistanceMatrix matrix = DistanceMatrixHolder.getCurrentMatrix();
        if (matrix == null) {
            logger.warn("DistanceMatrix not available, returning 0 for vehicle {}", vehicle.getId());
            return 0;
        }
        
        long totalDistance = 0;
        Location depot = vehicle.getDepot();
        
        // Distance from depot to first customer
        Customer firstCustomer = vehicle.getCustomerList().get(0);
        totalDistance += matrix.getDistance(depot, firstCustomer.getLocation());

        // Distance between consecutive customers
        for (int i = 0; i < vehicle.getCustomerList().size() - 1; i++) {
            Location from = vehicle.getCustomerList().get(i).getLocation();
            Location to = vehicle.getCustomerList().get(i + 1).getLocation();
            totalDistance += matrix.getDistance(from, to);
        }

        // Distance from last customer back to depot
        Customer lastCustomer = vehicle.getCustomerList().get(vehicle.getCustomerList().size() - 1);
        totalDistance += matrix.getDistance(lastCustomer.getLocation(), depot);

        return totalDistance;
    }
    
    /**
     * Static holder for the current DistanceMatrix.
     * Set before solving, used during constraint evaluation.
     */
    public static class DistanceMatrixHolder {
        private static final ThreadLocal<DistanceMatrix> currentMatrix = new ThreadLocal<>();
        
        public static void setCurrentMatrix(DistanceMatrix matrix) {
            currentMatrix.set(matrix);
        }
        
        public static DistanceMatrix getCurrentMatrix() {
            return currentMatrix.get();
        }
        
        public static void clear() {
            currentMatrix.remove();
        }
    }
}
