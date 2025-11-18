package com.adcaisse.delivery_route_optimizer.model;

public class Order {
    private long id;
    private Location location;
    private int size;

    public Order() {
    }

    public Order(long id, Location location, int size) {
        this.id = id;
        this.location = location;
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", location=" + location +
                ", size=" + size +
                '}';

    }
}
