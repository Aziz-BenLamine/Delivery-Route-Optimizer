package com.adcaisse.delivery_route_optimizer.dto;

import com.adcaisse.delivery_route_optimizer.model.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request for route information between two points")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequest {
    @Schema(description = "Starting location")
    private Location from;

    @Schema(description = "Destination location")
    private Location to;
}
