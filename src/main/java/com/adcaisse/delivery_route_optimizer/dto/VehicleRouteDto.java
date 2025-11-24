package com.adcaisse.delivery_route_optimizer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for vehicle route information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRouteDto {
    private Long vehicleId;
    private String vehicleName;
    private int capacity;
    private int totalDemand;
    private long distance;
    private List<CustomerStopDto> stops;
}
