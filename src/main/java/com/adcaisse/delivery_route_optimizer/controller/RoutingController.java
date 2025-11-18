package com.adcaisse.delivery_route_optimizer.controller;

import com.adcaisse.delivery_route_optimizer.client.GraphHopperClient;
import com.adcaisse.delivery_route_optimizer.model.Location;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/routing")
public class RoutingController {

    private final GraphHopperClient graphHopperClient;

    public RoutingController() {
        this.graphHopperClient = new GraphHopperClient("http://localhost:8989");
    }

    @PostMapping("/matrix")
    public long[][] getDistanceMatrix(@RequestBody List<Location> locations) throws Exception {
        return graphHopperClient.getDistanceMatrix(locations);
    }
}