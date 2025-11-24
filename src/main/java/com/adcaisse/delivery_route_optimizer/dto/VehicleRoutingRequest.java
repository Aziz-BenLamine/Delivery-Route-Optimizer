package com.adcaisse.delivery_route_optimizer.dto;

import com.adcaisse.delivery_route_optimizer.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for vehicle routing optimization requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRoutingRequest {
    private Location depot;
    private List<Location> customerLocations;
    private List<Integer> vehicleCapacities;
    private List<Integer> customerDemands;
}
