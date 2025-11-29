package com.adcaisse.delivery_route_optimizer.controller;

import com.adcaisse.delivery_route_optimizer.client.GraphHopperClient;
import com.adcaisse.delivery_route_optimizer.dto.RouteRequest;
import com.adcaisse.delivery_route_optimizer.dto.VehicleRoutingRequest;
import com.adcaisse.delivery_route_optimizer.dto.VehicleRoutingSolutionDto;
import com.adcaisse.delivery_route_optimizer.model.Location;
import com.adcaisse.delivery_route_optimizer.model.VehicleRoutingSolution;
import com.adcaisse.delivery_route_optimizer.service.VehicleRoutingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Route Optimization", description = "APIs for optimizing delivery routes and retrieving routing information")
@RestController
@RequestMapping("/api/routing")
public class RoutingController {

    private final GraphHopperClient graphHopperClient;
    private final VehicleRoutingService vehicleRoutingService;

    public RoutingController(VehicleRoutingService vehicleRoutingService,
                            GraphHopperClient graphHopperClient) {
        this.vehicleRoutingService = vehicleRoutingService;
        this.graphHopperClient = graphHopperClient;
    }

    @PostMapping("/matrix")
    public long[][] getDistanceMatrix(@RequestBody List<Location> locations) throws Exception {
        return graphHopperClient.getDistanceMatrix(locations);
    }

    @Operation(summary = "Optimize delivery routes",
               description = "Optimize delivery routes based on depot, customer locations, vehicle capacities, and customer demands.")
    @PostMapping("/optimize")
    public ResponseEntity<VehicleRoutingSolutionDto> optimizeRoutes(
            @RequestBody VehicleRoutingRequest request) {
        
        try {
            VehicleRoutingSolution solution = vehicleRoutingService.solveVRP(
                request.getDepot(),
                request.getCustomerLocations(),
                request.getVehicleCapacities(),
                request.getCustomerDemands()
            );
            
            VehicleRoutingSolutionDto dto = 
                vehicleRoutingService.getSolutionDto(solution);
            
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Test with sample data",
            description = "Runs route optimization with predefined sample data (Tunisia locations). " +
                    "Useful for testing the API without providing custom data."
    )
    @GetMapping("/optimize/sample")
    public ResponseEntity<VehicleRoutingSolutionDto> optimizeSampleRoutes() {
        try {
            VehicleRoutingSolution solution = vehicleRoutingService.solveSampleProblem();
            VehicleRoutingSolutionDto dto = 
                vehicleRoutingService.getSolutionDto(solution);
            
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Get route polyline",
            description = "Returns an encoded polyline string representing the route between two points. " +
                    "Can be used for map visualization."
    )
    @PostMapping("/polyline")
    public ResponseEntity<String> getRoutePolyline(@RequestBody RouteRequest request) {
        try {
            String polyline = graphHopperClient.getRoutePolyline(request.getFrom(), request.getTo());
            return ResponseEntity.ok(polyline);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting route: " + e.getMessage());
        }
    }
}