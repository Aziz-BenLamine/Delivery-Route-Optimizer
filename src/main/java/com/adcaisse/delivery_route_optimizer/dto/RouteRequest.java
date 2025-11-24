package com.adcaisse.delivery_route_optimizer.dto;

import com.adcaisse.delivery_route_optimizer.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for route requests between two points
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequest {
    private Location from;
    private Location to;
}
