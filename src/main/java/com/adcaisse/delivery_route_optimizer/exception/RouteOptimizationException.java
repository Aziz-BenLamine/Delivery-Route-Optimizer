package com.adcaisse.delivery_route_optimizer.exception;

/**
 * Exception thrown when route optimization fails.
 */
public class RouteOptimizationException extends RuntimeException {
    
    public RouteOptimizationException(String message) {
        super(message);
    }
    
    public RouteOptimizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
