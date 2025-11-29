package com.adcaisse.delivery_route_optimizer.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.lookup.PlanningId;

@Schema(description = "Geographic location with coordinates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    
    @PlanningId
    private long id;
    private double latitude;
    private double longitude;
}
