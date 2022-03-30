package io.deeplay.qlab.parser.models;

import java.util.Objects;

public class Unit {
    private final String name;
    private final double sourceGoldCount;
    private final double goldProfit;
    private final int locatePosition;
    private final int evasiveness;
    private final int aggression;
    private final int responseAggression;
    private final int shield;

    public Unit() {
        this.name = null;
        this.sourceGoldCount = 0.0;
        this.goldProfit = 0.0;
        this.locatePosition = 0;
        this.evasiveness = 0;
        this.aggression = 0;
        this.responseAggression = 0;
        this.shield = 0;
    }

    public Unit(String name, double sourceGoldCount, double goldProfit, int locatePosition, int evasiveness, int aggression, int responseAggression, int shield) {
        this.name = name;
        this.sourceGoldCount = sourceGoldCount;
        this.goldProfit = goldProfit;
        this.locatePosition = locatePosition;
        this.evasiveness = evasiveness;
        this.aggression = aggression;
        this.responseAggression = responseAggression;
        this.shield = shield;
    }

    public String getName() {
        return name;
    }

    public double getSourceGoldCount() {
        return sourceGoldCount;
    }

    public double getGoldProfit() {
        return goldProfit;
    }

    public int getLocatePosition() {
        return locatePosition;
    }

    public int getEvasiveness() {
        return evasiveness;
    }

    public int getAggression() {
        return aggression;
    }

    public int getResponseAggression() {
        return responseAggression;
    }

    public int getShield() {
        return shield;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit unit)) return false;
        return Double.compare(unit.sourceGoldCount, sourceGoldCount) == 0
                && Double.compare(unit.goldProfit, goldProfit) == 0
                && locatePosition == unit.locatePosition
                && evasiveness == unit.evasiveness
                && aggression == unit.aggression
                && responseAggression == unit.responseAggression
                && shield == unit.shield
                && Objects.equals(name, unit.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sourceGoldCount, goldProfit, locatePosition, evasiveness, aggression, responseAggression, shield);
    }
}
