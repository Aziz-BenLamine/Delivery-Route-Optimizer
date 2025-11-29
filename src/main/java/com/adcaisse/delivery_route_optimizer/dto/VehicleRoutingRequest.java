package com.adcaisse.delivery_route_optimizer.dto;

import com.adcaisse.delivery_route_optimizer.model.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Request payload for vehicle routing optimization")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRoutingRequest {
    @Schema(description = "Depot location where all vehicles start and end their routes")
    private Location depot;

    @Schema(description = "List of customer locations to visit")
    private List<Location> customerLocations;

    @Schema(description = "List of vehicle capacities (one per vehicle)")
    private List<Integer> vehicleCapacities;

    @Schema(description = "List of customer demands (parallel to customerLocations)")
    private List<Integer> customerDemands;
}
