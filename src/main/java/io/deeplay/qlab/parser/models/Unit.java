package io.deeplay.qlab.parser.models;

import java.util.Objects;

public class Unit {
    private final String name;
    private double sourceGoldCount;
    private double goldProfit;
    private int locatePosition;
    private int evasiveness;
    private int aggression;
    private int responseAggression;
    private int shield;

    public Unit(String name, double sourceGoldCount) {
        this.name = name;
        this.sourceGoldCount = sourceGoldCount;
        this.goldProfit = 0.0;
        this.locatePosition = 0;
        this.evasiveness = 0;
        this.aggression = 0;
        this.responseAggression = 0;
        this.shield = 0;
    }

    public Unit(String name, double sourceGoldCount, int locatePosition) {
        this.name = name;
        this.sourceGoldCount = sourceGoldCount;
        this.goldProfit = 0.0;
        this.locatePosition = locatePosition;
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

    public void setSourceGoldCount(double sourceGoldCount) {
        this.sourceGoldCount = sourceGoldCount;
    }

    public double getGoldProfit() {
        return goldProfit;
    }

    public void setGoldProfit(double goldProfit) {
        this.goldProfit = goldProfit;
    }

    public int getLocatePosition() {
        return locatePosition;
    }

    public void setLocatePosition(int locatePosition) {
        this.locatePosition = locatePosition;
    }

    public int getEvasiveness() {
        return evasiveness;
    }

    public void setEvasiveness(int evasiveness) {
        this.evasiveness = evasiveness;
    }

    public int getAggression() {
        return aggression;
    }

    public void setAggression(int aggression) {
        this.aggression = aggression;
    }

    public int getResponseAggression() {
        return responseAggression;
    }

    public void setResponseAggression(int responseAggression) {
        this.responseAggression = responseAggression;
    }

    public int getShield() {
        return shield;
    }

    public void setShield(int shield) {
        this.shield = shield;
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
