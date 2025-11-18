package com.adcaisse.delivery_route_optimizer.model;

public class Driver {
    private long id;
    private Location startLocation;
    private int capacity;

    public Driver() {
    }

    public Driver(long id, Location startLocation, int capacity) {
        this.id = id;
        this.startLocation = startLocation;
        this.capacity = capacity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", startLocation=" + startLocation +
                ", capacity=" + capacity +
                '}';
    }
}
