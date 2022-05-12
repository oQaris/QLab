package io.deeplay.qlab.parser.models;

import java.util.Objects;

public class Unit {
    protected String name;
    protected double sourceGoldCount;
    protected int locatePosition;

    public Unit(String name, double sourceGoldCount) {
        this.name = name;
        this.sourceGoldCount = sourceGoldCount;
        this.locatePosition = 0;
    }

    public Unit(String name, double sourceGoldCount, int locatePosition) {
        this.name = name;
        this.sourceGoldCount = sourceGoldCount;
        this.locatePosition = locatePosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSourceGoldCount() {
        return sourceGoldCount;
    }

    public void setSourceGoldCount(double sourceGoldCount) {
        this.sourceGoldCount = sourceGoldCount;
    }

    public int getLocatePosition() {
        return locatePosition;
    }

    public void setLocatePosition(int locatePosition) {
        this.locatePosition = locatePosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit unit)) return false;
        return Double.compare(unit.sourceGoldCount, sourceGoldCount) == 0
                && locatePosition == unit.locatePosition
                && Objects.equals(name, unit.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sourceGoldCount, locatePosition);
    }
}
