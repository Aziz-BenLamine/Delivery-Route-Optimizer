package com.adcaisse.delivery_route_optimizer.exception;

/**
 * Exception thrown when GraphHopper API calls fail.
 */
public class GraphHopperException extends RuntimeException {
    
    public GraphHopperException(String message) {
        super(message);
    }
    
    public GraphHopperException(String message, Throwable cause) {
        super(message, cause);
    }
}
