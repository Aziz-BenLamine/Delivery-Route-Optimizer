package com.adcaisse.delivery_route_optimizer.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Route Optimizer API")
                        .description("A Vehicle Routing Problem (VRP) solver using OptaPlanner and GraphHopper. " +
                                "Optimizes delivery routes for a fleet of vehicles serving multiple customers.")
                        .version("1.0.0"));

    }

}
