package io.deeplay.qlab.parser.models.input;

import io.deeplay.qlab.parser.models.Unit;

import java.util.List;
import java.util.Objects;

public class EnemyLocation {
    private final String locationName;
    private final int locationLevel;
    private final int maxPositionsQuantity;
    private final List<Unit> opponentUnits;

    public EnemyLocation(String locationName, int locationLevel, int maxPositionsQuantity, List<Unit> opponentUnits) {
        this.locationName = locationName;
        this.locationLevel = locationLevel;
        this.maxPositionsQuantity = maxPositionsQuantity;
        this.opponentUnits = opponentUnits;
    }

    public String getLocationName() {
        return locationName;
    }

    public int getLocationLevel() {
        return locationLevel;
    }

    public int getMaxPositionsQuantity() {
        return maxPositionsQuantity;
    }

    public List<Unit> getOpponentUnits() {
        return opponentUnits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnemyLocation that)) return false;
        return locationLevel == that.locationLevel
                && maxPositionsQuantity == that.maxPositionsQuantity
                && Objects.equals(locationName, that.locationName)
                && Objects.equals(opponentUnits, that.opponentUnits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationName, locationLevel, maxPositionsQuantity, opponentUnits);
    }
}
