package io.deeplay.qlab.algorithm.models;

import java.util.Objects;

public class UnitWithProb {
    private final String name;
    private final Double sourceGoldCount;
    private final Double shield;
    private final Double evasiveness;
    private final Double aggression;
    private final Double responseAggression;
    private int locationPosition;


    public UnitWithProb(String name, Double sourceGoldCount, Double shield, Double evasiveness,
                        Double aggression, Double responseAggression, int locationPosition) {
        this.name = name;
        this.sourceGoldCount = sourceGoldCount;
        this.shield = shield;
        this.evasiveness = evasiveness;
        this.aggression = aggression;
        this.responseAggression = responseAggression;
        this.locationPosition = locationPosition;
    }

    public String getName() {
        return name;
    }

    public Double getSourceGoldCount() {
        return sourceGoldCount;
    }

    public Double getShield() {
        return shield;
    }

    public Double getEvasiveness() {
        return evasiveness;
    }

    public Double getAggression() {
        return aggression;
    }

    public Double getResponseAggression() {
        return responseAggression;
    }

    public int getLocationPosition() {
        return locationPosition;
    }

    public void setLocationPosition(int locationPosition) {
        this.locationPosition = locationPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnitWithProb that)) return false;
        return locationPosition == that.locationPosition
                && Objects.equals(name, that.name)
                && Objects.equals(sourceGoldCount, that.sourceGoldCount)
                && Objects.equals(shield, that.shield)
                && Objects.equals(evasiveness, that.evasiveness)
                && Objects.equals(aggression, that.aggression)
                && Objects.equals(responseAggression, that.responseAggression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sourceGoldCount, shield, evasiveness, aggression, responseAggression, locationPosition);
    }
}
