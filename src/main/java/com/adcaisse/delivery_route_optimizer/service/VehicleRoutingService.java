package com.adcaisse.delivery_route_optimizer.service;

import com.adcaisse.delivery_route_optimizer.model.Customer;
import com.adcaisse.delivery_route_optimizer.model.Location;
import com.adcaisse.delivery_route_optimizer.model.Vehicle;
import com.adcaisse.delivery_route_optimizer.model.VehicleRoutingSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for solving vehicle routing problems using OptaPlanner
 */
@Service
public class VehicleRoutingService {

    private final GraphHopperDistanceCalculator distanceCalculator;
    private SolverFactory<VehicleRoutingSolution> solverFactory;

    @Autowired
    public VehicleRoutingService(GraphHopperDistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
        this.initializeSolver();
    }

    private void initializeSolver() {
        // Load solver configuration from XML
        this.solverFactory = SolverFactory.createFromXmlResource("solverConfig.xml");
    }

    /**
     * Solve vehicle routing problem for given locations and vehicles
     *
     * @param depot The depot location (starting/ending point for all vehicles)
     * @param customerLocations List of customer locations to visit
     * @param vehicleCapacities List of vehicle capacities
     * @param customerDemands List of demands for each customer (parallel to customerLocations)
     * @return Optimized routing solution
     */
    public VehicleRoutingSolution solveVRP(Location depot, 
                                          List<Location> customerLocations,
                                          List<Integer> vehicleCapacities,
                                          List<Integer> customerDemands) {
        
        // Validate input
        if (customerLocations.size() != customerDemands.size()) {
            throw new IllegalArgumentException("Customer locations and demands lists must have the same size");
        }

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

        // Preload distance matrix for better performance
        List<Location> allLocations = new ArrayList<>();
        allLocations.add(depot);
        allLocations.addAll(customerLocations);
        distanceCalculator.preloadDistanceMatrix(allLocations);

        // Create uninitialized solution
        VehicleRoutingSolution problem = new VehicleRoutingSolution(
                "VRP Problem",
                customers,
                vehicles
        );
        problem.setDistanceCalculator(distanceCalculator);

        // Solve the problem
        Solver<VehicleRoutingSolution> solver = solverFactory.buildSolver();
        VehicleRoutingSolution solution = solver.solve(problem);

        return solution;
    }

    /**
     * Simple method for testing with default values
     */
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

    /**
     * Get a summary of the solution for API responses
     */
    public VehicleRoutingSolutionSummary getSolutionSummary(VehicleRoutingSolution solution) {
        VehicleRoutingSolutionSummary summary = new VehicleRoutingSolutionSummary();
        summary.setScore(solution.getScore());
        summary.setTotalDistance(solution.getTotalDistance());
        summary.setTotalCustomers(solution.getTotalCustomers());
        summary.setFeasible(solution.isFeasible());
        
        List<VehicleRouteSummary> routes = solution.getVehicleList().stream()
                .filter(vehicle -> !vehicle.getCustomerList().isEmpty())
                .map(this::createVehicleRouteSummary)
                .collect(Collectors.toList());
        
        summary.setRoutes(routes);
        return summary;
    }

    private VehicleRouteSummary createVehicleRouteSummary(Vehicle vehicle) {
        VehicleRouteSummary routeSummary = new VehicleRouteSummary();
        routeSummary.setVehicleId(vehicle.getId());
        routeSummary.setVehicleName(vehicle.getName());
        routeSummary.setCapacity(vehicle.getCapacity());
        routeSummary.setTotalDemand(vehicle.getTotalDemand());
        routeSummary.setDistance(vehicle.getTotalDistance(distanceCalculator));
        
        List<CustomerStop> stops = vehicle.getCustomerList().stream()
                .map(customer -> new CustomerStop(
                        customer.getId(),
                        customer.getName(),
                        customer.getLocation(),
                        customer.getDemand()
                ))
                .collect(Collectors.toList());
        
        routeSummary.setStops(stops);
        return routeSummary;
    }

    // Inner classes for API responses
    public static class VehicleRoutingSolutionSummary {
        private org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore score;
        private long totalDistance;
        private int totalCustomers;
        private boolean feasible;
        private List<VehicleRouteSummary> routes;

        // Getters and setters
        public org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore getScore() {
            return score;
        }

        public void setScore(org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore score) {
            this.score = score;
        }

        public long getTotalDistance() {
            return totalDistance;
        }

        public void setTotalDistance(long totalDistance) {
            this.totalDistance = totalDistance;
        }

        public int getTotalCustomers() {
            return totalCustomers;
        }

        public void setTotalCustomers(int totalCustomers) {
            this.totalCustomers = totalCustomers;
        }

        public boolean isFeasible() {
            return feasible;
        }

        public void setFeasible(boolean feasible) {
            this.feasible = feasible;
        }

        public List<VehicleRouteSummary> getRoutes() {
            return routes;
        }

        public void setRoutes(List<VehicleRouteSummary> routes) {
            this.routes = routes;
        }
    }

    public static class VehicleRouteSummary {
        private Long vehicleId;
        private String vehicleName;
        private int capacity;
        private int totalDemand;
        private long distance;
        private List<CustomerStop> stops;

        // Getters and setters
        public Long getVehicleId() {
            return vehicleId;
        }

        public void setVehicleId(Long vehicleId) {
            this.vehicleId = vehicleId;
        }

        public String getVehicleName() {
            return vehicleName;
        }

        public void setVehicleName(String vehicleName) {
            this.vehicleName = vehicleName;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getTotalDemand() {
            return totalDemand;
        }

        public void setTotalDemand(int totalDemand) {
            this.totalDemand = totalDemand;
        }

        public long getDistance() {
            return distance;
        }

        public void setDistance(long distance) {
            this.distance = distance;
        }

        public List<CustomerStop> getStops() {
            return stops;
        }

        public void setStops(List<CustomerStop> stops) {
            this.stops = stops;
        }
    }

    public static class CustomerStop {
        private Long customerId;
        private String customerName;
        private Location location;
        private int demand;

        public CustomerStop(Long customerId, String customerName, Location location, int demand) {
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
}
