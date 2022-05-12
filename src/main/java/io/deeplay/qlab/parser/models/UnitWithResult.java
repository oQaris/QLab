package io.deeplay.qlab.parser.models;

import java.util.Objects;

public class UnitWithResult extends Unit {
    private double goldProfit;
    private int evasiveness;
    private int aggression;
    private int responseAggression;
    private int shield;

    public UnitWithResult(String name, double sourceGoldCount, int locatePosition) {
        super(name, sourceGoldCount, locatePosition);
        this.goldProfit = 0.0;
        this.evasiveness = 0;
        this.aggression = 0;
        this.responseAggression = 0;
        this.shield = 0;
    }

    public UnitWithResult(String name, double sourceGoldCount, double goldProfit, int locatePosition, int evasiveness, int aggression, int responseAggression, int shield) {
        super(name, sourceGoldCount, locatePosition);
        this.goldProfit = goldProfit;
        this.evasiveness = evasiveness;
        this.aggression = aggression;
        this.responseAggression = responseAggression;
        this.shield = shield;
    }

    public double getGoldProfit() {
        return goldProfit;
    }

    public void setGoldProfit(double goldProfit) {
        this.goldProfit = goldProfit;
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
        if (!(o instanceof UnitWithResult that)) return false;
        if (!super.equals(o)) return false;
        return Double.compare(that.goldProfit, goldProfit) == 0
                && evasiveness == that.evasiveness
                && aggression == that.aggression
                && responseAggression == that.responseAggression
                && shield == that.shield;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), goldProfit, evasiveness, aggression, responseAggression, shield);
    }
}
