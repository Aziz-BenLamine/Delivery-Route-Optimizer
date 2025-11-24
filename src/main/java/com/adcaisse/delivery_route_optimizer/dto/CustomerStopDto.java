package com.adcaisse.delivery_route_optimizer.dto;

import com.adcaisse.delivery_route_optimizer.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a customer stop in a vehicle route
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStopDto {
    private Long customerId;
    private String customerName;
    private Location location;
    private int demand;
}
