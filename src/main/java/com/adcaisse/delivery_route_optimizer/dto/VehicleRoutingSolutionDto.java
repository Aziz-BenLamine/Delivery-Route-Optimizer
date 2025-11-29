package com.adcaisse.delivery_route_optimizer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

import java.util.List;

@Schema(description = "Optimized vehicle routing solution")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRoutingSolutionDto {
    @Schema(description = "OptaPlanner score (format: Xhard/Ysoft)", example = "0hard/-108527soft")
    private HardSoftLongScore score;

    @Schema(description = "Total distance traveled by all vehicles in meters", example = "108527")
    private long totalDistance;

    @Schema(description = "Total number of customers served", example = "15")
    private int totalCustomers;

    @Schema(description = "Indicates if a feasible solution was found", example = "true")
    private boolean feasible;

    @Schema(description = "List of vehicle routes in the optimized solution")
    private List<VehicleRouteDto> routes;
}
