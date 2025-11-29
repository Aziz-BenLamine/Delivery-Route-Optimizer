package com.adcaisse.delivery_route_optimizer.dto;

import com.adcaisse.delivery_route_optimizer.model.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "A customer stop in a vehicle's route")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStopDto {
    private Long customerId;
    private String customerName;
    private Location location;
    private int demand;
}
