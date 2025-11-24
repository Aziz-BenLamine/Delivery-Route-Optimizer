package com.adcaisse.delivery_route_optimizer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.lookup.PlanningId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    
    @PlanningId
    private long id;
    private double latitude;
    private double longitude;
}
