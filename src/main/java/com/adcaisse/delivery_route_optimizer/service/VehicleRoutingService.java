package com.adcaisse.delivery_route_optimizer.service;

import com.adcaisse.delivery_route_optimizer.dto.VehicleRoutingSolutionDto;
import com.adcaisse.delivery_route_optimizer.model.Location;
import com.adcaisse.delivery_route_optimizer.model.VehicleRoutingSolution;

import java.util.List;

/**
 * Service interface for solving Vehicle Routing Problems (VRP).
 * Provides methods to optimize delivery routes for a fleet of vehicles.
 */
public interface VehicleRoutingService {

    /**
     * Solve vehicle routing problem for given locations and vehicles.
     * All distances are computed via the configured distance calculator for accurate routing.
     *
     * @param depot The depot location (starting/ending point for all vehicles)
     * @param customerLocations List of customer locations to visit
     * @param vehicleCapacities List of vehicle capacities
     * @param customerDemands List of demands for each customer (parallel to customerLocations)
     * @return Optimized routing solution
     */
    VehicleRoutingSolution solveVRP(Location depot,
                                    List<Location> customerLocations,
                                    List<Integer> vehicleCapacities,
                                    List<Integer> customerDemands);

    /**
     * Solve a sample problem for testing purposes.
     * Uses predefined test data (Tunisia coordinates).
     *
     * @return Optimized routing solution for sample data
     */
    VehicleRoutingSolution solveSampleProblem();

    /**
     * Convert a VehicleRoutingSolution to a DTO for API responses.
     *
     * @param solution The solution to convert
     * @return DTO representation of the solution
     */
    VehicleRoutingSolutionDto getSolutionDto(VehicleRoutingSolution solution);
}
