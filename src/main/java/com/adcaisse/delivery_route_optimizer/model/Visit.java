package com.adcaisse.delivery_route_optimizer.model;


import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Visit {

    private long id;
    private Order order;

    @PlanningVariable(valueRangeProviderRefs = "visits")
    private Visit previousVisit;

    public Visit() {
    }

    public Visit(long id, Order order, Visit previousVisit) {
        this.id = id;
        this.order = order;
        this.previousVisit = previousVisit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Visit getPreviousVisit() {
        return previousVisit;
    }

    public void setPreviousVisit(Visit previousVisit) {
        this.previousVisit = previousVisit;
    }

    @Override
    public String toString() {
        return "Visit{" +
                "id=" + id +
                ", order=" + order +
                ", previousVisit=" + (previousVisit != null ? previousVisit.getId() : null) +
                '}';
    }

}
