package com.adcaisse.delivery_route_optimizer.service.impl;

import com.adcaisse.delivery_route_optimizer.dto.CustomerStopDto;
import com.adcaisse.delivery_route_optimizer.dto.VehicleRouteDto;
import com.adcaisse.delivery_route_optimizer.dto.VehicleRoutingSolutionDto;
import com.adcaisse.delivery_route_optimizer.model.Customer;
import com.adcaisse.delivery_route_optimizer.model.DistanceMatrix;
import com.adcaisse.delivery_route_optimizer.model.Location;
import com.adcaisse.delivery_route_optimizer.model.Vehicle;
import com.adcaisse.delivery_route_optimizer.model.VehicleRoutingSolution;
import com.adcaisse.delivery_route_optimizer.service.DistanceCalculatorService;
import com.adcaisse.delivery_route_optimizer.service.VehicleRoutingService;
import com.adcaisse.delivery_route_optimizer.solver.VehicleRoutingConstraintProvider;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of VehicleRoutingService using OptaPlanner.
 * Uses GraphHopper for all distance calculations - no Haversine approximations.
 */
@Service
public class VehicleRoutingServiceImpl implements VehicleRoutingService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRoutingServiceImpl.class);

    private final DistanceCalculatorService distanceCalculator;
    private SolverFactory<VehicleRoutingSolution> solverFactory;

    public VehicleRoutingServiceImpl(DistanceCalculatorService distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
        this.initializeSolver();
    }

    private void initializeSolver() {
        this.solverFactory = SolverFactory.createFromXmlResource("solverConfig.xml");
    }

    @Override
    public VehicleRoutingSolution solveVRP(Location depot, 
                                          List<Location> customerLocations,
                                          List<Integer> vehicleCapacities,
                                          List<Integer> customerDemands) {
        
        // Validate input
        if (customerLocations.size() != customerDemands.size()) {
            throw new IllegalArgumentException("Customer locations and demands lists must have the same size");
        }

        logger.info("Starting VRP optimization for {} customers with {} vehicles", 
                   customerLocations.size(), vehicleCapacities.size());

        // Create customers
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < customerLocations.size(); i++) {
            customers.add(new Customer(
                    (long) i + 1,
                    "Customer " + (i + 1),
                    customerLocations.get(i),
                    customerDemands.get(i)
            ));
        }

        // Create vehicles
        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < vehicleCapacities.size(); i++) {
            vehicles.add(new Vehicle(
                    (long) i + 1,
                    "Vehicle " + (i + 1),
                    vehicleCapacities.get(i),
                    depot
            ));
        }

        // Compute distance matrix using GraphHopper (pre-computation for O(1) lookups)
        List<Location> allLocations = new ArrayList<>();
        allLocations.add(depot);
        allLocations.addAll(customerLocations);
        
        DistanceMatrix distanceMatrix = distanceCalculator.computeDistanceMatrix(allLocations);

        // Create solution with distance matrix
        VehicleRoutingSolution problem = new VehicleRoutingSolution(
                "VRP Problem",
                customers,
                vehicles
        );
        problem.setDistanceCalculator(distanceCalculator);
        problem.setDistanceMatrix(distanceMatrix);

        // Set the distance matrix in the constraint provider's holder (for constraint evaluation)
        VehicleRoutingConstraintProvider.DistanceMatrixHolder.setCurrentMatrix(distanceMatrix);

        try {
            // Solve the problem
            logger.info("Starting OptaPlanner solver...");
            Solver<VehicleRoutingSolution> solver = solverFactory.buildSolver();
            VehicleRoutingSolution solution = solver.solve(problem);
            
            logger.info("✅ Optimization complete! Score: {}", solution.getScore());
            return solution;
            
        } finally {
            // Clean up the thread-local holder
            VehicleRoutingConstraintProvider.DistanceMatrixHolder.clear();
        }
    }

    @Override
    public VehicleRoutingSolution solveSampleProblem() {
        // Create a depot location (Tunisia coordinates)
        Location depot = new Location(0L, 36.7682, 10.2753); // Starting location
        
        // Create customer locations around Tunisia
        List<Location> customerLocations = List.of(
            new Location(1L, 36.8196, 10.3035), // Customer 1 - demand 12
            new Location(2L, 36.8064, 10.1817), // Customer 2 - demand 10
            new Location(3L, 36.8625, 10.1956), // Customer 3 - demand 15
            new Location(4L, 36.8782, 10.3247), // Customer 4 - demand 8
            new Location(5L, 36.8687, 10.3417), // Customer 5 - demand 20
            new Location(6L, 36.6800, 10.1600), // Customer 6 - demand 9
            new Location(7L, 36.6808, 10.2903), // Customer 7 - demand 14
            new Location(8L, 36.6965, 10.3858), // Customer 8 - demand 7
            new Location(9L, 36.9097, 10.2867), // Customer 9 - demand 18
            new Location(10L, 36.8093, 10.0863) // Customer 10 - demand 11
        );
        
        // Customer demands (packages to deliver) - based on your provided data
        List<Integer> customerDemands = List.of(12, 10, 15, 8, 20, 9, 14, 7, 18, 11);
        
        // Vehicle capacities - increased to handle higher total demand
        List<Integer> vehicleCapacities = List.of(500); // 2 vehicles with higher capacity
        
        return solveVRP(depot, customerLocations, vehicleCapacities, customerDemands);
    }

    @Override
    public VehicleRoutingSolutionDto getSolutionDto(VehicleRoutingSolution solution) {
        VehicleRoutingSolutionDto dto = new VehicleRoutingSolutionDto();
        dto.setScore(solution.getScore());
        dto.setTotalDistance(solution.getTotalDistance());
        dto.setTotalCustomers(solution.getTotalCustomers());
        dto.setFeasible(solution.isFeasible());
        
        List<VehicleRouteDto> routes = solution.getVehicleList().stream()
                .filter(vehicle -> !vehicle.getCustomerList().isEmpty())
                .map(this::createVehicleRouteDto)
                .collect(Collectors.toList());
        
        dto.setRoutes(routes);
        return dto;
    }

    private VehicleRouteDto createVehicleRouteDto(Vehicle vehicle) {
        VehicleRouteDto routeDto = new VehicleRouteDto();
        routeDto.setVehicleId(vehicle.getId());
        routeDto.setVehicleName(vehicle.getName());
        routeDto.setCapacity(vehicle.getCapacity());
        routeDto.setTotalDemand(vehicle.getTotalDemand());
        routeDto.setDistance(vehicle.getTotalDistance(distanceCalculator));
        
        List<CustomerStopDto> stops = vehicle.getCustomerList().stream()
                .map(customer -> new CustomerStopDto(
                        customer.getId(),
                        customer.getName(),
                        customer.getLocation(),
                        customer.getDemand()
                ))
                .collect(Collectors.toList());
        
        routeDto.setStops(stops);
        return routeDto;
    }
}
