package com.adcaisse.delivery_route_optimizer.model;

import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
public class RoutingSolution {
    private long id;

    @ValueRangeProvider(id = "visits")
    private List<Visit> visits;

    private List<Driver> drivers;
    private long[][] distanceMatrix;

    @PlanningScore
    private HardSoftScore score;

    public RoutingSolution() {
    }

    public RoutingSolution(long id, List<Visit> visits, List<Driver> drivers, long[][] distanceMatrix, HardSoftScore score) {
        this.id = id;
        this.visits = visits;
        this.drivers = drivers;
        this.distanceMatrix = distanceMatrix;
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public long[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public void setDistanceMatrix(long[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}
