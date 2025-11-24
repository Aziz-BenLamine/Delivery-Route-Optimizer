package com.adcaisse.delivery_route_optimizer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

import java.util.List;

/**
 * DTO for vehicle routing solution results
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRoutingSolutionDto {
    private HardSoftLongScore score;
    private long totalDistance;
    private int totalCustomers;
    private boolean feasible;
    private List<VehicleRouteDto> routes;
}
